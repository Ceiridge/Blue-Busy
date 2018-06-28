package org.ceiridge.bluebusy;

import java.awt.Graphics;
import javax.swing.JFrame;

public class MyFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public MyFrame(String title) {
		super(title);
	}

	@Override
	public void paint(Graphics g) {
		try {
			Main.pDraw(g);
		} catch (Exception e) {
		}
	}
}
