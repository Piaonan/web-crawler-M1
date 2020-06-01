package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeSet;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import graphic.Graphic;
import server.Server;

public class ControllerSea implements ActionListener {
	private JTextField wordField;

	public ControllerSea(JTextField wF) {
		wordField = wF;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String word = wordField.getText();
		if (word == null || word.trim().length() < 3) {
			Graphic.setResponse(Color.red, "Mot incorrect, il faut saisir au moins 3 lettres.");
			return;
		}

		Graphic.setEnabled(false);
		Graphic.setResponse(Color.blue, "En cours de recherche, veuillez patienter...");
		Graphic.setText("", JTextArea::setText);

		new Thread(() -> {
			TreeSet<String> urls = Server.indekkusu(word);
			if (urls == null)
				Graphic.setResponse(Color.red, "Erreur : Aucun index n'est construit, veuillez entrer une URL.");
			else if (urls.isEmpty())
				Graphic.setResponse(Color.blue, "Réponse : Ce mot n'a pas été trouvé dans l'index.");
			else {
				urls.forEach(link -> Graphic.setText(link + "\n", JTextArea::append));
				Graphic.setResponse(Color.green, "Voici les URLs associées à votre mot.");
			}

			Graphic.setEnabled(true);
		}).start();
	}

}
