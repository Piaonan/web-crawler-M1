package backup;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;

import server.Notice;

public class Backup {
	private static HashMap<String, Domain> data = new HashMap<>();

	private static void load() {
		StringBuilder sb = new StringBuilder();

		try {
			Files.lines(Paths.get("backup.json"), StandardCharsets.UTF_8).forEach(sb::append);
			for (Member m : Json.parse(sb.toString()).asObject()) {
				HashMap<String, Notice> urlsMap = new HashMap<>();
				HashMap<String, HashSet<String>> termsMap = new HashMap<>();
				String id = m.getName();

				for (JsonValue jV : m.getValue().asObject().get("urls").asArray()) {
					JsonObject notice = jV.asObject().get("notice").asObject();
					urlsMap.put(jV.asObject().get("url").asString(),
							new Notice(notice.get("link").asString(), notice.get("message").asString()));
				}
				for (JsonValue jV : m.getValue().asObject().get("terms").asArray()) {
					HashSet<String> urlsSet = new HashSet<>();
					for(JsonValue jV2 : jV.asObject().get("urls").asArray())
						urlsSet.add(jV2.asString());
					termsMap.put(jV.asObject().get("term").asString(), urlsSet);
				}
				
				data.put(id, new Domain(urlsMap, termsMap));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void linkStart() {
		File file = Paths.get("htmlFiles").toFile();
		if (!file.isDirectory())
			file.mkdir();
		if (Paths.get("backup.json").toFile().exists())
			load();
	}

	public static void save() {
		JsonObject backup = Json.object();

		data.forEach((key, domain) -> {
			JsonArray array1 = Json.array();
			JsonObject object = Json.object();

			domain.getUrls().forEach((url, notice) -> {
				JsonObject subObject = Json.object();
				subObject.add("url", url).add("notice",
						Json.object().add("link", notice.getLinkState()).add("message", notice.getMessage()));
				array1.add(subObject);
			});
			object.add("urls", array1);

			JsonArray array2 = Json.array();
			domain.getTerms().forEach((term, urls) -> {
				JsonArray subArray = Json.array();
				urls.forEach(url -> subArray.add(url));
				array2.add(Json.object().add("term", term).add("urls", subArray));
			});
			object.add("terms", array2);

			backup.add(key, object);
		});

		try {
			Files.write(Paths.get("backup.json"), backup.toString(WriterConfig.PRETTY_PRINT).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void put(String s, Domain d) {
		data.put(s, d);
	}

	public static Domain search(String s) {
		return data.get(s);
	}
}
