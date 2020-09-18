package com.haroon96.client.gui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class UIElements {

	static Font appFont = new Font("Roboto", 0, 13);
	static ImageIcon appIcon;
	static ImageIcon userIcon;
	static ImageIcon infoIcon;

	static {
		appIcon = new ImageIcon(UIElements.class.getResource("appIcon.png"));
		userIcon = new ImageIcon(UIElements.class.getResource("userIcon.png"));
		infoIcon = new ImageIcon(UIElements.class.getResource("infoIcon.png"));
	}

}
