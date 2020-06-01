package main;

import server.Server;

public class Main {
	private static int port = 8000;
	private static int nbExplorers = 10;
	private static int limit = 150;

	public static void main(String[] args) {
		setArgs(args);
		System.out.println("Numéro de port : "+ port);
		System.out.println("Nombre d'explorateurs : "+ nbExplorers);
		System.out.println("Limite d'URLs par exploration : "+ limit);
		new Server(port, nbExplorers, limit);
	}

	private static void setArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			switch (args[i++]) {
			case "-port":
				port = numeric(args[i], port);
				break;
			case "-nbExp":
				nbExplorers = numeric(args[i], nbExplorers);
				break;
			case "-limit":
				limit = numeric(args[i], limit);
				break;
			default:
				break;
			}
		}
	}

	private static int numeric(String s, int initial) {
		if (s != null && s.matches("\\d+"))
			return Integer.parseInt(s);
		else
			return initial;
	}
}