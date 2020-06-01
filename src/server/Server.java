package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;

import backup.Backup;
import backup.Domain;
import explorer.Explorer;
import graphic.Graphic;
import server.Notice.Link;

public class Server {
	private static Selector selector = null;
	private static ServerSocketChannel socket = null;
	private static int nbExplorers;
	private static int LIMIT;
	private static HashMap<String, Notice> urls = new HashMap<>();
	private static HashMap<String, HashSet<String>> terms = new HashMap<>();
	private static String firstURL = "";

	public Server(int port, int n, int lim) {
		nbExplorers = n;
		LIMIT = lim;
		Backup.linkStart();
		start(port);
	}

	private void start(final int port) {
		try {
			selector = Selector.open();
			socket = ServerSocketChannel.open();
			ServerSocket serverSocket = socket.socket();
			serverSocket.bind(new InetSocketAddress("localhost", port));
			socket.configureBlocking(false);
			socket.register(selector, socket.validOps(), null);

			createExplorers(port);

			int n = 0;
			do {
				selector.select();
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> i = selectedKeys.iterator();

				while (i.hasNext()) {
					SelectionKey key = i.next();

					if (key.isAcceptable()) {
						handleAccept(socket, key);
						n++;
					}

					i.remove();
				}
			} while (n < nbExplorers);

			Graphic.launch();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void handleAccept(ServerSocketChannel mySocket, SelectionKey key) throws IOException {
		System.out.println("Connexion acceptée...");
		SocketChannel client = mySocket.accept();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_WRITE);
	}

	private void createExplorers(final int port) {
		for (int i = 0; i < nbExplorers; i++)
			new Thread(new Explorer(port)).start();
	}

	public static void searching(String url) throws IOException {
		firstURL = url;
		Domain domain = null;
		if ((domain = Backup.search(url)) != null) {
			urls = domain.getUrls();
			terms = domain.getTerms();
		} else
			processing(url);
	}

	public static void processing(String url) throws IOException {
		urls.clear();
		terms.clear();
		urls.put(url, new Notice(Link.Unexplored));

		while (!urls.values().stream().allMatch(Link.Explored::is)
				&& (urls.size() < LIMIT || urls.values().stream().anyMatch(Link.Ongoing::is))) {
			selector.select();
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> i = selectedKeys.iterator();

			while (i.hasNext()) {
				SelectionKey key = i.next();

				if (key.isReadable()) {
					handleRead(key);
				} else if (key.isWritable() && urls.values().stream().anyMatch(Link.Unexplored::is)
						&& urls.size() < LIMIT) {
					url = urls.entrySet().stream().filter(state -> state.getValue().check(Link.Unexplored))
							.map(Map.Entry::getKey).findFirst().get();
					handleWrite(key, url);
					urls.replace(url, new Notice(Link.Ongoing));
				}

				i.remove();
			}
		}
		Backup.put(firstURL, new Domain(new HashMap<>(urls), new HashMap<>(terms)));
	}

	private static void decode(String chain, SocketChannel client) throws IOException {
		BiFunction<String, HashSet<String>, HashSet<String>> biFunWords = null;
		for (String data : chain.split("><")) {
			//System.out.println("je coupe [" + data + "]");
			switch (data.substring(0, 6)) {
			case "LINKS|":
				String value = data.substring(6);
				if (value.startsWith("/"))
					value = firstURL + value.substring(firstURL.endsWith("/") ? 1 : 0);
				urls.computeIfAbsent(value, k -> new Notice(Link.Unexplored));
				break;
			case "WORDS|":
				for (String w : data.substring(6).split("\\s+"))
					terms.compute(w, biFunWords);
				break;
			case "ERROR|":
				int separator = data.indexOf("[]");
				urls.replace(data.substring(6, separator), new Notice(data.substring(separator + 2)));
				break;
			case "START|":
				biFunWords = (k, v) -> {
					if (k.length() < 3)
						return v;
					if (v == null)
						v = new HashSet<>();
					v.add(data.substring(6));
					return v;
				};
				urls.replace(data.substring(6), new Notice(Link.Explored));
				break;
			case "RESCON":
				break;
			default:
				break;
			}
		}
	}

	private static void handleRead(SelectionKey key) throws IOException {
		System.out.println("Lecture...");
		SocketChannel client = (SocketChannel) key.channel();

		decode(getDataFromClient(client), client);
		client.register(selector, SelectionKey.OP_WRITE);
	}

	private static void handleWrite(SelectionKey key, String url) throws IOException {
		System.out.println("Écriture...");
		SocketChannel client = (SocketChannel) key.channel();

		sendDataToClient(client, url);
		client.register(selector, SelectionKey.OP_READ);
	}

	private static String getDataFromClient(SocketChannel client) throws IOException {
		StringBuilder sb = new StringBuilder();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		String s = "";

		do {
			int i = client.read(buffer);
			buffer.flip();
			if (i > 0) {
				s = StandardCharsets.UTF_8.decode(buffer).toString().trim();
				sb.append(s);
			}
			buffer.clear();
		} while (!s.contains("RESCON") && !s.contains("ERROR|"));
		return sb.toString().trim();
	}

	private static void sendDataToClient(SocketChannel client, String data) throws IOException {
		System.out.println("Données à envoyer : " + data);
		ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
		client.write(buffer);
		buffer.clear();
	}

	public static void closeAll() throws IOException {
		selector.select();
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		Iterator<SelectionKey> i = selectedKeys.iterator();

		while (i.hasNext()) {
			SelectionKey key = i.next();
			SocketChannel client = (SocketChannel) key.channel();
			sendDataToClient(client, "EXIT_RESCON");
			client.close();
			i.remove();
		}
		socket.close();
	}

	public static String getLinks() {
		StringBuilder sb = new StringBuilder();
		TreeSet<String> set = new TreeSet<>();
		urls.forEach((url, notice) -> set.add(url + notice.getNotice()));
		set.forEach(s -> sb.append(s));
		return sb.toString();
	}

	public static TreeSet<String> getWords() {
		return new TreeSet<>(terms.keySet());
	}

	public static TreeSet<String> indekkusu(String word) {
		if (terms.isEmpty())
			return null;
		else if (terms.containsKey(word))
			return new TreeSet<>(terms.get(word));
		else
			return new TreeSet<String>();
	}
}
