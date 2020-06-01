package explorer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuperParser {
	final String PATTERN_HREF = "\\s*(?i)<a[^>]+href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	final String PATTERN_WORD = "<[^>]*>([^<>]*)";
	final String PATTERN_SCRIPT_CSS = "<script(.*?)<\\/script>|<style(.*?)<\\/style>";
	final String PATTERN_ALPHABET = "[^a-zA-ZÀ-ÿ0-9- ]";
	final int LETTERS_AT_LEAST = 2;
	ArrayList<String> links;
	ArrayList<String> words;
	private String toFile;
	
	public SuperParser(String url, String toFile) throws Exception {
		this.toFile = toFile;
		links = new ArrayList<>();
		words = new ArrayList<>();
		getWebPage(url);
	}

	private void getWebPage(String url) throws IOException {
		URL website = new URL(url);
		isHTMLType(website);		

		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(toFile);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();

		getContents(toFile, replaceUselessChar(url));
	}

	private void isHTMLType(URL website) throws IOException {
		URLConnection connection = website.openConnection();
		String str = connection.getContentType();
		if (str == null)
			throw new IOException("BAD URL");
		if (!str.contains("text/html"))
			throw new IOException(
					"Fichier WEB; type: " + str + "; taille en octets: " + connection.getContentLengthLong());
	}

	private void getContents(String toFile, String url) throws IOException {
		StringBuilder sb = new StringBuilder();
		try {
			Files.lines(Paths.get(toFile), StandardCharsets.UTF_8).forEach(sb::append);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("Problème d'encodage (autre que UTF-8).");
		}

		String html = sb.toString().replaceAll(PATTERN_SCRIPT_CSS, "");
		String original = url.substring(0, url.indexOf("."));

		parsing(PATTERN_HREF, html, str -> {
			if (str.isEmpty() || str.contains("#"))
				return;
			else if (replaceUselessChar(str).startsWith(original))
				links.add(str);
			else if (str.startsWith("/"))
				links.add(str);
		});

		parsing(PATTERN_WORD, html, str -> {
			if (str.length() > LETTERS_AT_LEAST)
				words.add(str.replaceAll(PATTERN_ALPHABET, ""));
		});
	}

	private void parsing(String PATTERN, String html, Consumer<String> fun) {
		Matcher matcherLink;

		matcherLink = Pattern.compile(PATTERN).matcher(html);
		while (matcherLink.find()) {
			fun.accept(matcherLink.group(1).replace("'", "").replace("\"", "").trim());
		}
	}
	
	private String replaceUselessChar(String s) {
		return s.replace("www.", "").replace("https", "").replace("http", "").replaceAll("/", "");
	}
}
