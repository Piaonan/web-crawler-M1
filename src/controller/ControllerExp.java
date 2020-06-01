package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import graphic.Graphic;
import server.Server;

public class ControllerExp implements ActionListener {
	private JTextField urlField;

	public ControllerExp(JTextField uF) {
		urlField = uF;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		final String url = urlField.getText();
		if (url == null || url.trim().isEmpty()) {
			Graphic.setResponse(Color.red, "URL invalide, veuillez saissir une adresse correcte.");
			return;
		}
		
		Graphic.setEnabled(false);
		Graphic.setResponse(Color.blue, "En cours d'exploration, veuillez patienter...");
		Graphic.setText("", JTextArea::setText);

		new Thread(() -> {
			try {
				Server.searching(url);
				Graphic.setText(Server.getLinks(), JTextArea::setText);
				Graphic.setResponse(Color.green, "Voici les URLs trouvées sur les pages WEB.");
			} catch (Exception e) {
				Graphic.setResponse(Color.red, "Erreur : " + e.getMessage());
				e.printStackTrace();
			} finally {
				Graphic.setEnabled(true);
			}
		}).start();

	}
}
