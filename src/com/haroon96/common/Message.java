package com.haroon96.common;

import java.awt.Color;
import java.io.Serializable;

public class Message implements Serializable
{
	public Message(String message) {
		this.message = message;
		this.fromServer = true;
	}
	public Message(String message, String username, Color scheme) {
		this.message = message;
		this.username = username;
		this.scheme = scheme;
	}

	public String getUsername()
	{
		return username;
	}
	public String getMessage()
	{
		return message;
	}
	public Color getColor() { return scheme; }
	public boolean fromServer() { return fromServer; }

	private String username;
	private String message;
	private Color scheme;
	private boolean fromServer;

}