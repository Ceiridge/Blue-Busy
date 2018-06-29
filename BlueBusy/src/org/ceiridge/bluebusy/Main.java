package org.ceiridge.bluebusy;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFrame;
import org.ceiridge.intelligentconfig.ConfigContainer;
import org.ceiridge.intelligentconfig.IntelligentConfig;

public class Main {

	public static JFrame frame;
	public static MyPanel panel;
	public static BlueBox[] boxes = null;
	public static IntelligentConfig config;
	public static ConfigContainer settings;
	
	public static int mAura = 0;

	public static void main(String[] args) throws Throwable {
		config = new IntelligentConfig(new File("settings.bluebusy.txt"), true, false);
		settings = config.getContainer("settings");

		if (!settings.doesValueExist("BlocksCountDiv")) {
			settings.setInteger("BlocksCountDiv", 15);
			settings.setInteger("MouseAura", 120);
			settings.setString("BorderColor", "0x2980b9");
			settings.setString("FillColor", "0x3498db");
			config.save();
		}
		
		mAura = settings.getInteger("MouseAura");

		frame = new JFrame("BlueBusy - By Ceiridge");
		frame.setSize(750, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				initBoxes();
			}
		});
		frame.setBackground(Color.white);
		frame.setContentPane(panel = new MyPanel());

		BufferedImage cursorI = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorI, new Point(0, 0), "Hidden Cursor");
		panel.setCursor(cursor);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						try {
							panel.repaint();
						} catch (Exception e) {
						}
						Thread.sleep(16l);
					}
				} catch (Exception e) {
				}
			}

		}, "Repaint Thread").start();
	}


	public static void initBoxes() {
		boxes = null;
		int width = frame.getWidth();
		int height = frame.getHeight();

		int blocksDiv = settings.getInteger("BlocksCountDiv"); // Editable
		int count = 0;

		for (int y = 0; y < height / blocksDiv; y++) {
			for (int x = 0; x < width / blocksDiv; x++) {
				count++;
			}
		}

		boxes = new BlueBox[count];
		count = 0;

		for (int y = 0; y < height / blocksDiv; y++) {
			for (int x = 0; x < width / blocksDiv; x++) {
				boxes[count] = new BlueBox(x * blocksDiv, y * blocksDiv, blocksDiv, blocksDiv);
				count++;
			}
		}
	}

	public static void pDraw(Graphics g) throws Exception {
		Point mousePos = panel.getMousePosition();

		double mouseX = mousePos == null ? -1 : mousePos.getX();
		double mouseY = mousePos == null ? -1 : mousePos.getY();

		for (BlueBox bb : boxes) {
			if (mouseX != -1) {
				double diffX = Math.abs(mouseX - bb.x);
				double diffY = Math.abs(mouseY - bb.y);
				double diff = diffX + diffY;

				bb.mouseDiff = diff;
			} else {
				bb.mouseDiff = 10000;
			}

			bb.draw(g);
		}

	}
}
