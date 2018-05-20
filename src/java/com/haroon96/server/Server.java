package com.haroon96.server;

import com.haroon96.common.Message;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Server extends JFrame {

	public static void main(String[] args) {
		Server server = new Server();
		server.setVisible(true);
		server.startServer();
	}

	private void initWindow() {
		add(new JLabel("Server is running", JLabel.CENTER));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(300, 300);
	}

	private Server() {
		messagePool = new ArrayList<>();
		usernames = new ArrayList<>();
		outputStreams = new ArrayList<>();
		try {
			address = InetAddress.getByName("224.2.2.3");
		} catch(Exception e) {
			System.out.println(e);
		}

		port = 8888;
		new ClientSender().start();

		initWindow();
	}

	public void startServer() {
		MulticastSocket ms = null;
		try {
			ms = new MulticastSocket(port);
			ms.joinGroup(address);
		} catch (Exception e) {
			System.out.println(e);
		}
		DatagramPacket dp ;
		byte[] bytesArray;
		while (true) {
			boolean usernameExists;
			try {
				// need new dp each time to avoid continuity errors
				bytesArray = new byte[30];
				dp = new DatagramPacket(bytesArray, bytesArray.length);
				ms.receive(dp);
				InetAddress ip = dp.getAddress();

				ByteBuffer bb = ByteBuffer.wrap(dp.getData());
				int port = bb.getInt();
				byte[] bytes = new byte[bb.capacity() - 4];
				bb.get(bytes);
				String username = new String(bytes).trim();
				System.out.println("Connecting to " + username + " on " + ip);
				synchronized (usernames) {
					if ((usernameExists = usernameTaken(username)) == false) {
						usernames.add(username);
					}
				}
				if (!usernameExists) {
					addClient(new Socket(ip, port));
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}

	}

	private void addClient(Socket socket) {
		int r = (int)(Math.random() * 130);
		int g = (int)(Math.random() * 130);
		int b = (int)(Math.random() * 130);

		Color color = new Color(r, g, b);
		ObjectInputStream in;
		ObjectOutputStream out;
		StringBuilder sb = new StringBuilder();

		for (String username : usernames) {
			sb.append(username);
			sb.append(' ');
		}

		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			out.writeObject(new Message(sb.toString(), null, color));
			outputStreams.add(out);
			String username;
			synchronized (usernames) {
				username = usernames.get(usernames.size() - 1);
			}
			sendServerMessage(username + " has joined the conversation.");
			ClientReceiver cr = new ClientReceiver(in, out);
			cr.start();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private boolean usernameTaken(String username) {

		for (String connectedUsers : this.usernames) {
			if (username.toLowerCase().equals(connectedUsers.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	private class ClientReceiver extends Thread {
		private ObjectInputStream in;
		private ObjectOutputStream out;

		ClientReceiver(ObjectInputStream in, ObjectOutputStream out) {
			this.in = in;
			this.out = out;
		}
		@Override
		public void run() {
			Message message = null;
			try {
				while (true) {
					message = (Message)in.readObject();
					synchronized (messagePool) {
						messagePool.add(message);
						messagePool.notify();
					}
				}
			}
			catch (Exception e) {
				int index;
				String username;
				synchronized (outputStreams) {
					index = outputStreams.indexOf(out);
					outputStreams.remove(index);
				}
				synchronized (usernames) {
					username = usernames.get(index);
				}
				usernames.remove(index);
				sendServerMessage(username + " has left the conversation.");
			}
		}
	}

	private void sendServerMessage(String message) {
		synchronized (messagePool) {
			messagePool.add(new Message(message));
			messagePool.notify();
		}
	}

	private class ClientSender extends Thread {
		@Override
		public void run() {
			int currentMessage = 0;
			Message m;
			try {
				while (true) {
					synchronized (messagePool) {
						while (messagePool.size() == currentMessage) {
							if (messagePool.size() == currentMessage) {
								messagePool.wait();
							}
						}
						m = messagePool.get(currentMessage);
					}
					Forwarder fw = new Forwarder(m);
					fw.start();
					++currentMessage;
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		private class Forwarder extends Thread {
			private Message m;
			Forwarder(Message m){ this.m = m; }
			@Override
			public void run() {
				for (ObjectOutputStream out : outputStreams) {
					synchronized (outputStreams) {
						try {
							out.writeObject(this.m);
							out.flush();
						}
						catch(IOException e){
							System.out.println(e.getMessage());
						}
					}
				}
			}
		}
	}
	private ArrayList<Message> messagePool;
	private ArrayList<String> usernames;
	private ArrayList<ObjectOutputStream> outputStreams;
	private InetAddress address;
	private int port;

}