package ui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class DrawingOperations {


	public static void drawCenteredStringAt(Graphics2D g2, String s, int startX, int width, 
			int height) 
	{
		FontMetrics fm = g2.getFontMetrics();
		int point = startX + width / 2 - fm.stringWidth(s) / 2;
		g2.drawString(s, point, height);
	}

}
