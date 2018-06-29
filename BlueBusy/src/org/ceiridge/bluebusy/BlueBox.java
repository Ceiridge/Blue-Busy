package org.ceiridge.bluebusy;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.concurrent.ThreadLocalRandom;

public class BlueBox {
	public int x, y, width, height, alpha;
	public double mouseDiff = 1000;

	private boolean alphaRise = true;

	public static Color origBlueBorder = new Color(Integer.decode(Main.settings.getString("BorderColor")));
	public static Color origBlueFill = new Color(Integer.decode(Main.settings.getString("FillColor")));

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
		Graphics2D g2d = (Graphics2D) g;

		g.setColor(blueBorder);
		g2d.setComposite(AlphaComposite.Src.derive(alpha / 255f));

		int adder = 1;
		if (x == 0 || y == 0)
			adder = 0;
		g.drawRect(x + adder, y + adder, width - adder, height - adder);

		g.setColor(blueFill);
		g.fillRect(x + adder + 1, y + adder + 1, width - adder - 1, height - adder - 1);

		blueBorder = brightenOrDarken(blueBorder, origBlueBorder, mouseDiff);
		blueFill = brightenOrDarken(blueFill, origBlueFill, mouseDiff);

		g2d.setComposite(AlphaComposite.Src);

		if (alphaRise) {
			alpha = Math.min(255, alpha + ThreadLocalRandom.current().nextInt(0, 2));
			if (alpha >= 255)
				alphaRise = false;
		} else {
			alpha = Math.max(127, alpha - ThreadLocalRandom.current().nextInt(0, 2));
			if (alpha <= 150)
				alphaRise = true;
		}
	}

	private static Color brightenOrDarken(Color c, Color orig, double mouseDiff) {
		int factor = 1;
		double diff = ((c.getRed() + c.getGreen() + c.getBlue()) * 1d / (orig.getRed() + orig.getGreen() + orig.getBlue()) * 1d) * 100d;

		if (ThreadLocalRandom.current().nextBoolean() || diff < 85 || (mouseDiff <= Main.mAura && diff < 100)) {
			factor = mouseDiff <= Main.mAura ? (int) Math.max(1, ((Main.mAura / mouseDiff) * 5)) : factor;
			c = new Color(Math.min(255, c.getRed() + factor), Math.min(255, c.getGreen() + factor), Math.min(255, c.getBlue() + factor));
		} else {
			if (diff > 120)
				factor = 2;
			c = new Color(Math.max(0, c.getRed() - factor), Math.max(0, c.getGreen() - factor), Math.max(0, c.getBlue() - factor));
		}

		if (diff > 150) {
			return orig;
		}
		return c;
	}
}
