package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JTextArea;

import graphic.Graphic;
import server.Server;

public class ControllerBox implements ActionListener {
	private JComboBox<String> box;

	public ControllerBox(JComboBox<String> cB) {
		box = cB;
		box.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Graphic.setText("", JTextArea::setText);
		switch (box.getSelectedIndex()) {
		case 0:
			Graphic.setText(Server.getLinks(), JTextArea::setText);
			break;
		case 1:
			Server.getWords().forEach(word -> Graphic.setText(word + "\n", JTextArea::append));
			break;
		default:
			break;
		}
		Graphic.setResponse(Color.green, "Vous avez souhaitez voir la " + box.getSelectedItem().toString() + ".");
	}

}
