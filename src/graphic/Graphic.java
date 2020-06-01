package graphic;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.function.BiConsumer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import backup.Backup;
import controller.ControllerBox;
import controller.ControllerExp;
import controller.ControllerSea;
import server.Server;

public class Graphic {
	private JFrame mainFrame;
	private static JTextField urlField;
	private static JTextField wordField;
	private static JTextField responseField;
	private static JButton btnExplorer;
	private static JButton btnSearch;
	private static JTextArea textArea;
	private JLabel urlLabel;
	private JLabel wordLabel;
	private static JComboBox<String> box;

	public static void launch() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Graphic window = new Graphic();
					window.mainFrame.setResizable(false);
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Graphic() {
		initialize();
	}

	private void initialize() {
		mainFrame = new JFrame();
		mainFrame.setTitle("Web Crawler");
		mainFrame.setBounds(100, 100, 840, 640);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				Backup.save();
				try {
					Server.closeAll();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mainFrame.dispose();
				System.exit(0);
			}
		});
		mainFrame.getContentPane().setLayout(null);

		box = new JComboBox<>();
		box.addItem("Liste des liens");
		box.addItem("Liste des mots");
		box.setBounds(670, 45, 117, 28);
		box.addActionListener(new ControllerBox(box));
		mainFrame.add(box);

		urlLabel = new JLabel("URL à saisir");
		urlLabel.setBounds(32, 32, 86, 15);
		mainFrame.add(urlLabel);

		urlField = new JTextField();
		urlField.setBounds(110, 31, 379, 19);
		mainFrame.add(urlField);
		urlField.setColumns(15);

		btnExplorer = new JButton("Explorer");
		btnExplorer.setBounds(500, 27, 117, 25);
		mainFrame.add(btnExplorer);

		wordLabel = new JLabel("Mot à saisir");
		wordLabel.setBounds(32, 72, 86, 15);
		mainFrame.add(wordLabel);

		wordField = new JTextField();
		wordField.setBounds(110, 71, 379, 19);
		mainFrame.add(wordField);
		wordField.setColumns(15);

		btnSearch = new JButton("Rechercher");
		btnSearch.setBounds(500, 67, 117, 25);
		mainFrame.add(btnSearch);

		responseField = new JTextField();
		responseField.setBounds(32, 117, 769, 19);
		mainFrame.add(responseField);
		responseField.setColumns(10);
		responseField.setEditable(false);
		Font font = new Font("Verdana", Font.BOLD, 12);
		responseField.setFont(font);
		responseField.setForeground(Color.blue);
		responseField.setText("Réponse du serveur");

		textArea = new JTextArea();
		textArea.setBounds(32, 103, 769, 205);
		mainFrame.getContentPane().add(textArea);
		textArea.setEditable(false);

		JScrollPane textAreaScrollPane = new JScrollPane(textArea);
		textAreaScrollPane.setBounds(32, 155, 769, 440);
		mainFrame.add(textAreaScrollPane);

		btnExplorer.addActionListener(new ControllerExp(urlField));
		btnSearch.addActionListener(new ControllerSea(wordField));
	}

	public static void setResponse(Color c, String msg) {
		responseField.setForeground(c);
		responseField.setText(msg);
	}

	public static void setEnabled(boolean option) {
		btnExplorer.setEnabled(option);
		btnSearch.setEnabled(option);
		box.setVisible(option);
		urlField.setEditable(option);
		wordField.setEditable(option);
	}

	public static void setText(String msg, BiConsumer<JTextArea, String> fun) {
		fun.accept(textArea, msg);
	}
}
