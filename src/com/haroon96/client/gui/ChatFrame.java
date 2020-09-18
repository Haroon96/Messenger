package com.haroon96.client.gui;

import com.haroon96.client.Session;
import com.haroon96.common.Message;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import javax.swing.ImageIcon;

public class ChatFrame extends JFrame {

	public ChatFrame(String username, Color scheme, Session session, ImageIcon avatar, String connections) {
		super("Messenger");

		this.session = session;
		this.username = username;

		inf = new InformationFrame(username, avatar, connections, scheme);

		messageFont = UIElements.appFont;

		setSize(300, 500);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.WHITE);

		conversationWindow = new JPanel();
		conversationWindow.setLayout(new BoxLayout(conversationWindow, BoxLayout.PAGE_AXIS));
		conversationWindow.setBackground(new Color(240, 240, 240));
		conversationWindow.setBorder(new EmptyBorder(10, 5, 0, 5));

		messageBox = new JTextArea();
		messageBox.setSize(200, 300);
		messageBox.setLineWrap(true);
		messageBox.setFont(UIElements.appFont);

		sendBtn = new JButton("Send");
		sendBtn.setFont(UIElements.appFont);
		sendBtn.setBackground(scheme);
		sendBtn.setForeground(Color.WHITE);
		sendBtn.setEnabled(false);

		JPanel jp = new JPanel();
		jp.setBackground(Color.WHITE);
		jp.setLayout(new FlowLayout());
		jp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray));
		jp.add(messageBox);
		jp.add(sendBtn);

		JPanel jp2 = new JPanel();
		JLabel heading = new JLabel(this.username.toUpperCase(), JLabel.CENTER);
		heading.setPreferredSize(new Dimension(100, 50));
		heading.setFont(UIElements.appFont.deriveFont(16f).deriveFont(Font.BOLD));
		heading.setForeground(scheme);
		infoLabel = new JLabel(UIElements.infoIcon);
		infoLabel.setOpaque(true);
		infoLabel.setBackground(scheme);

		js = new JScrollPane(conversationWindow);
		js.setBorder(null);

		jp2.add(heading);
		jp2.add(Box.createRigidArea(new Dimension(50, 10)));
		jp2.add(infoLabel);
		jp2.setBackground(Color.white);
		jp2.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));

		add(jp2, BorderLayout.NORTH);
		add(js);
		add(jp, BorderLayout.SOUTH);
		addListeners();
	}

	private void addListeners() {
		messageBox.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				check();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				check();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				check();
			}

			public void check() {
				sendBtn.setEnabled(!messageBox.getText().isEmpty());
			}
		});
		messageBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == '\n') {
					sendBtn.getActionListeners()[0].actionPerformed(null);
					messageBox.setText("");
				}
			}
		});
		sendBtn.addActionListener((ActionEvent e) -> {
				if (messageBox.getText().trim().equals("") == false) {
					session.sendMessage(messageBox.getText().trim());
					messageBox.setText("");
				}
		});
		
		infoLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e)  {
				inf.showWindow();
			}
		});
	}

	public void addOutgoingMessage(Message m) {
		String message = m.getMessage();
		String sender = m.getUsername();
		Color color = m.getColor();
		addMessage(message, sender, color, BorderLayout.EAST);
	}

	public void addIncomingMessage(Message m) {
		String message = m.getMessage();
		if (m.fromServer()) {
			addServerMessage(message);
		}
		else {
			String sender = m.getUsername();
			Color color = m.getColor();
			if(!sender.equals(username)) {
				addMessage(message, sender, color, BorderLayout.WEST);
			}
		}
	}

	public void addServerMessage(String message) {
		JPanel jp = new JPanel();
		JTextPane jtp = new JTextPane();
		StyledDocument sd = jtp.getStyledDocument();
		Style messageStyle = jtp.addStyle("messageStyle", null);
		StyleConstants.setForeground(messageStyle, new Color(200, 200, 200));
		StyleConstants.setAlignment(messageStyle, StyleConstants.ALIGN_CENTER);
		sd.setParagraphAttributes(0, sd.getLength(), messageStyle, false);
		try {
			sd.insertString(sd.getLength(), message, messageStyle);
		}
		catch(BadLocationException e) {
			System.out.println(e.getMessage());
		}
		jtp.setBackground(new Color(120, 120, 120));
		jtp.setSize(new Dimension(270, Short.MAX_VALUE));
		updateConversationWindow(jp, jtp, BorderLayout.CENTER);
		processMessage(message);
	}

	private void processMessage(String message) {
		if (message.indexOf("left") > 0) {
			inf.removeUsername(message.substring(0, message.indexOf(' ')));
		}
		else if (message.indexOf("joined") > 0) {
			inf.addUsername(message.substring(0, message.indexOf(' ')));
		}
	}

	private void addMessage(String message, String sender, Color color, String direction) {
		JPanel jp = new JPanel();
		JTextPane jtp = new JTextPane();

		StyledDocument sd = jtp.getStyledDocument();
		Style senderNameStyle = jtp.addStyle("senderNameStyle", null);
		Style messageStyle = jtp.addStyle("messageStyle", null);
		StyleConstants.setBold(senderNameStyle, true);
		StyleConstants.setFontSize(senderNameStyle, 13);
		StyleConstants.setForeground(senderNameStyle, new Color(200, 200, 200));
		try {
			sd.insertString(sd.getLength(), sender + "\n", senderNameStyle);
			sd.insertString(sd.getLength(), message, messageStyle);
		}
		catch(BadLocationException e) {
			System.out.println(e.getMessage());
		}
		jtp.setBackground(color);
		jtp.setSize(new Dimension(160, Short.MAX_VALUE));
		updateConversationWindow(jp, jtp, direction);
	}

	private void updateConversationWindow(JPanel jp, JTextPane jtp, String direction) {
		jp.setLayout(new BorderLayout());
		jtp.setFont(messageFont);
		jtp.setDisabledTextColor(Color.white);
		jtp.setEnabled(false);
		int height = (int)jtp.getPreferredSize().getHeight();
		jtp.setPreferredSize(new Dimension(160, height));
		jp.setMaximumSize(new Dimension(270, height));
		jp.add(jtp, direction);

		conversationWindow.revalidate();
		conversationWindow.add(jp);
		conversationWindow.add(Box.createRigidArea(new Dimension(10, 10)));
		scrollToBottom(js);
	}
	
	private void scrollToBottom(JScrollPane scrollPane)  {
		JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
		AdjustmentListener downScroller = new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				Adjustable adjustable = e.getAdjustable();
				adjustable.setValue(adjustable.getMaximum());
				verticalBar.removeAdjustmentListener(this);
			}
		};
		verticalBar.addAdjustmentListener(downScroller);
	}


	private Session session;
	private String username;
	private JTextArea messageBox;
	private JButton sendBtn;
	private Font messageFont;
	private JPanel conversationWindow;
	private JScrollPane js;
	private JLabel infoLabel;
	private InformationFrame inf;
}
