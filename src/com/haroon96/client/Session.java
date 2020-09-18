package com.haroon96.client;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import javax.swing.ImageIcon;

import com.haroon96.client.gui.ChatFrame;
import com.haroon96.common.Message;

public class Session {

	public Session(String username, Socket socket, ImageIcon avatar) {
		this.username = username;
		this.socket = socket;
		String connections = null;
		try {
			Message m;
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
			m = (Message)in.readObject();
			this.scheme = m.getColor();
			connections = m.getMessage();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		cf = new ChatFrame(username, scheme, this, avatar, connections);
		cf.setVisible(true);
		MessageReceiver mr = new MessageReceiver();
		mr.start();
	}

	public void sendMessage(String message) {
		try {
			Message m = new Message(message, username, scheme);
			out.writeObject(m);
			out.flush();
			cf.addOutgoingMessage(m);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	private class MessageReceiver extends Thread {
		@Override
		public void run() {
			try {
				Message m;
				while(true) {
					try {
						m = (Message)in.readObject();
						cf.addIncomingMessage(m);
					} catch(IOException e) {
						System.out.println(e.getMessage());
					}
				}
			} catch (ClassNotFoundException e) {
				System.out.println(e);
			}
		}
	}


	private String username;
	private Color scheme;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private ChatFrame cf;


}