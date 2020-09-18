package com.haroon96.client.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
	
public class InformationFrame extends JFrame {
	String username;
	ArrayList<String> connectedUsers;

	InformationFrame(String username, ImageIcon img, String list, Color scheme) {
		super("Group Information");
		this.username = username;
		initUI(img, list, scheme);
	}

	private void initUI(ImageIcon img, String list, Color scheme) {

		connectedUsers = new ArrayList<>();

		setSize(300, 500);
		setResizable(false);

		connections = new JPanel();
		connections.setBackground(Color.WHITE);
		connections.setBorder(new EmptyBorder(1, 1, 1, 1));
		connections.setPreferredSize(new Dimension(200, 200));

		JPanel content = new JPanel();
		content.setBackground(Color.WHITE);
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		content.setBorder(new EmptyBorder(5, 5, 5, 5));

		ImageIcon i = new ImageIcon(img.getImage().getScaledInstance(120,120,Image.SCALE_DEFAULT));
		JLabel imgLabel = new JLabel(i);

		JLabel loginLabel = new JLabel(username, SwingConstants.CENTER);
		loginLabel.setFont(UIElements.appFont);

		JLabel online = new JLabel("Online");
		online.setFont(UIElements.appFont.deriveFont(12f));

		backBtn = new JButton("Back");
		backBtn.setBackground(scheme);
		backBtn.setForeground(Color.white);
		backBtn.setFont(UIElements.appFont);

		JScrollPane js = new JScrollPane(connections);

		JPanel jp1 = new JPanel();
		JPanel jp2 = new JPanel();
		JPanel jp3 = new JPanel();
		JPanel jp4 = new JPanel();

		jp1.add(imgLabel);
		jp2.add(loginLabel);
		jp3.add(online);
		jp4.add(backBtn);

		jp1.setBackground(Color.white);
		jp2.setBackground(Color.white);
		jp3.setBackground(Color.white);

		jp1.setMaximumSize(new Dimension(300, 150));
		jp2.setMaximumSize(new Dimension(300, 50));
		jp3.setMaximumSize(new Dimension(300, 50));
		jp4.setMaximumSize(new Dimension(300, 50));
		jp3.setBorder(new EmptyBorder(20, 0, 0, 0));

		content.add(jp1);
		content.add(jp2);
		content.add(jp3);
		content.add(js);
		content.add(jp4);

		add(content);
		addListeners();

		for (String user : list.split(" ")) {
			addUsername(user);
		}
	}

	private void addListeners() {
		backBtn.addActionListener((ActionEvent arg0)->{
			closeWindow();
		});
	}

	public void addUsername(String username) {
		if (!this.username.equals(username))
			connectedUsers.add(username);
	}

	public void removeUsername(String username)
	{
		connectedUsers.remove(username);
	}

	public void showWindow() {
		closeWindow();
		setVisible(true);
		JLabel jl;
		for (String username : connectedUsers) {
			jl = new JLabel(username);
			jl.setFont(UIElements.appFont.deriveFont(16f));
			jl.setBorder(new EmptyBorder(2, 2, 2, 2));
			connections.add(jl);
		}
		connections.revalidate();
	}

	private void closeWindow() {
		connections.removeAll();
		setVisible(false);
	}

	private JButton backBtn;
	private JPanel connections;
}
