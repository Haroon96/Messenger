package com.haroon96.client.gui;

import com.haroon96.client.Session;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.regex.Pattern;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class LoginFrame extends JFrame {

	public LoginFrame() {
		super("Login to Messenger");

		setSize(400, 500);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		nameField = new JTextField();
		userImage = UIElements.userIcon;

		JLabel title = new JLabel(
				"Messenger",
				UIElements.appIcon,
				JLabel.CENTER);

		title.setVerticalTextPosition(JLabel.BOTTOM);
		title.setHorizontalTextPosition(JLabel.CENTER);
		title.setFont(UIElements.appFont.deriveFont(22f));
		
		loginBtn = new JButton("Login");
		loginBtn.setBackground(new Color(50,130,255));
		loginBtn.setForeground(Color.white);
		loginBtn.setPreferredSize(new Dimension(80, 40));
		loginBtn.setFont(UIElements.appFont);

		nameField.setPreferredSize(new Dimension(160, 30));
		nameField.setFont(UIElements.appFont);
		nameField.setHorizontalAlignment(JTextField.CENTER);
		nameField.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

		photo = new JLabel("Add photo (optional)", JLabel.CENTER);
		photo.setForeground(Color.gray);
		photo.setFont(UIElements.appFont);

		prompt = new JLabel(" ", JLabel.CENTER);
		prompt.setForeground(Color.red);
		prompt.setFont(UIElements.appFont);

		JLabel uname = new JLabel("Username");
		uname.setFont(UIElements.appFont);
		
		JPanel content = new JPanel();
		JPanel jp1 = new JPanel();
		JPanel jp2 = new JPanel();
		JPanel jp3 = new JPanel();
		JPanel jp4 = new JPanel();
		JPanel jp5 = new JPanel();
		
		content.setBackground(Color.WHITE);
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

		jp1.setBackground(Color.WHITE);
		jp2.setBackground(Color.WHITE);
		jp3.setBackground(Color.WHITE);
		jp4.setBackground(Color.WHITE);
		jp5.setBackground(Color.WHITE);
		
		jp1.add(title);
		jp2.add(uname);
		jp2.add(nameField);
		jp3.add(photo);
		jp4.add(loginBtn);
		jp5.add(prompt);
		
		content.add(jp1);
		content.add(jp2);
		content.add(jp3);
		content.add(jp4);
		content.add(jp5);

		add(content, BorderLayout.CENTER);
		
		initListeners();
	}
	private void initListeners() {
		loginListener(nameField);
		photoListener();
		addPhotoHoverEn();
		addPhotoHoverEx();
        nameField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
				if (nameField.getText().length() >= 10 && e.getKeyChar() != '\n') {
					e.consume();
					nameField.setText(nameField.getText().substring(0, 10));
				}
			}

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyChar() == '\n') {
                    loginBtn.getActionListeners()[0].actionPerformed(null);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }
	private void addPhotoHoverEn() {
		photo.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e)  {
				photo.setForeground(Color.lightGray);
				repaint();
			}
		});
	}
	private void addPhotoHoverEx() {
		photo.addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent e) {
				photo.setForeground(Color.gray);
				repaint();
			}
		});
	}
	private void photoListener() {
		photo.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(LoginFrame.this);
				File f = chooser.getSelectedFile();
				userImage = new ImageIcon(f.getAbsolutePath());
				photo.setText("Select a different photo");
	  		}
	  	});
	}
	public void loginListener(JTextField name) {
		loginBtn.addActionListener((ActionEvent arg0) -> {

			String username = name.getText();

			if (username.isEmpty()) {
				prompt.setText("Please enter a username.");
				return;
			}

			// check if username only contains alpha-numeric characters
			if (Pattern.compile("[^a-zA-Z0-9]").matcher(username).find()) {
				prompt.setText("Username contains invalid characters.");
			}

			prompt.setText("Attempting to connect to server...");
			establishConnection();

		});
	}
	
	private void establishConnection() {
		String username = nameField.getText();
		try {
			DatagramSocket ds = new DatagramSocket();
			// multicast address
			InetAddress address = InetAddress.getByName("224.2.2.3");
			int port = 8888;
			int listenPort = (int)(Math.random() * 40000);
			int usernameLength = username.getBytes().length;

			// allocate a bytebuffer for username and port number
			ByteBuffer bb = ByteBuffer.allocate(4 + usernameLength);
			bb = bb.putInt(listenPort);
			bb = bb.put(username.getBytes());
			byte[] bytes = bb.array();

			// send udp packet to multicast address
			DatagramPacket dp = new DatagramPacket(bytes, bytes.length, address, port);
			ds.send(dp);

			// wait for server to establish connection
			ServerSocket ss = new ServerSocket(listenPort);
			ss.setSoTimeout(10000);
			Socket skt = ss.accept();
			new Session(username, skt, userImage);
			setVisible(false);
		}
		catch (SocketException e){
			prompt.setText("Connection timed-out. Verify whether server is running.");
		} catch (IOException e) {
			prompt.setText("Unable to read from server connection.");
		}
	}

	private ImageIcon userImage;
 	private JLabel photo;
	private JTextField nameField;
	private JButton loginBtn;
	private JLabel prompt;

}