package com.haroon96.driver;

import com.haroon96.server.Server;

public class ServerDriver {
	public static void main(String[] args) {
		Server s = new Server();
		System.out.println("Server is running...");
		s.startServer();
    }
}
