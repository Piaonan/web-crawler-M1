package explorer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Explorer implements Runnable {
	private int port;
	private SocketChannel client;
	private String toFile = "htmlFiles/" + UUID.randomUUID().toString().replace("-", "").substring(0, 12) + ".html";

	public Explorer(final int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			client = SocketChannel.open(new InetSocketAddress("localhost", port));
			boolean stop = false;

			while (!stop) {

				String data = getDataFromServer();
				switch (data) {

				case "EXIT_RESCON":
					stop = true;
					break;
				default:
					System.out.println("url reçu par l'explorateur : " + data);
					try {
						SuperParser sP = new SuperParser(data, toFile);
						sendDataToServer("START|" + data + "><");
						sP.links.forEach(link -> {
							try {
								sendDataToServer("LINKS|" + link + "><");
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
						sP.words.forEach(word -> {
							try {
								sendDataToServer("WORDS|" + word + "><");
							} catch (IOException e) {
								e.printStackTrace();
							}
						});;
						
						sendDataToServer("RESCON");
					} catch (Exception e) {
						try {
							sendDataToServer("ERROR|" + data +"[]"+ e.getMessage().replaceAll(" ", "_"));
							//e.printStackTrace();
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
					break;
				}
			}
			client.close();
			System.out.println("Fermeture de la connexion client.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendDataToServer(String data) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
		/*int bytesWritten = */client.write(buffer);
		buffer.clear();
		//System.out.println(String.format("Message envoyé : %s bytes: %d", data, bytesWritten));
	}

	private String getDataFromServer() throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		/*int i = */client.read(buffer);
		buffer.flip();
		//System.out.println("bytes lues " + i);
		String s = StandardCharsets.UTF_8.decode(buffer).toString().trim();
		buffer.clear();
		return s;
	}
}
