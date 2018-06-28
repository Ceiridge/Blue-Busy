package org.ceiridge.bluebusy;

import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.ThreadLocalRandom;

public class BlueBox {
	public int x, y, width, height, alpha;
	private boolean alphaRise = true;

	public static Color origBlueBorder = new Color(0x2980b9);
	public static Color origBlueFill = new Color(0x3498db);

	public Color blueBorder = origBlueBorder;
	public Color blueFill = origBlueFill;

	public BlueBox(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.alpha = Math.min(255, y / height + x / width);
	}

	public void draw(Graphics g) {
		g.setColor(blueBorder);
		g.drawRect(x, y, width, height);
		g.drawRect(x + 1, y + 1, width - 1, height - 1);
		g.setColor(blueFill);
		g.fillRect(x + 2, y + 2, width - 2, height - 2);

		blueBorder = brightenOrDarken(blueBorder, origBlueBorder, alpha);
		blueFill = brightenOrDarken(blueFill, origBlueFill, alpha);

		if (alphaRise) {
			alpha = Math.min(255, alpha + 1);
			if (alpha == 255)
				alphaRise = false;

		} else {
			alpha = Math.max(0, alpha - 1);
			if (alpha == 0)
				alphaRise = true;
		}
	}

	private static Color brightenOrDarken(Color c, Color orig, int alpha) {
		int factor = 1;
		double diff = ((c.getRed() + c.getGreen() + c.getBlue()) * 1d / (orig.getRed() + orig.getGreen() + orig.getBlue()) * 1d) * 100d;

		if (ThreadLocalRandom.current().nextBoolean() || diff < 85) {
			c = new Color(Math.min(255, c.getRed() + factor), Math.min(255, c.getGreen() + factor), Math.min(255, c.getBlue() + factor), alpha);
		} else {
			if (diff > 120)
				factor = 2;
			c = new Color(Math.max(0, c.getRed() - factor), Math.max(0, c.getGreen() - factor), Math.max(0, c.getBlue() - factor), alpha);
		}
		return c;
	}
}
