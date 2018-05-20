package com.haroon96.client.gui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class UIElements {

	static Font appFont = new Font("Segoe UI", 0, 14);
	static ImageIcon appIcon;
	static ImageIcon userIcon;
	static ImageIcon infoIcon;

	static {
		try {
			appIcon = new ImageIcon(UIElements.class.getResourceAsStream("appIcon.png").readAllBytes());
			userIcon = new ImageIcon(UIElements.class.getResourceAsStream("userIcon.png").readAllBytes());
			infoIcon = new ImageIcon(UIElements.class.getResourceAsStream("infoIcon.png").readAllBytes());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
