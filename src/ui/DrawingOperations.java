package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import cards.BasicCard;
import cards.CardType;
import cards.UnitCard;

/**
 * Small helper class, with drawing functions. 
 * @author Abar
 */

public class DrawingOperations extends Frame {
    
    private static Font font1 = new Font("SansSerif", Font.BOLD, 12);
    private static Font font1a = new Font("SansSerif", Font.PLAIN, 12);
    private static Font font2 = new Font("SansSerif", Font.BOLD, 14);
    
    private static Color[] colors = {Color.BLACK, new Color(0, 64, 0), new Color(0, 0, 128), new Color(128, 0, 128), 
            new Color(128, 0, 0), new Color(0, 128, 192), new Color(167, 199, 56), new Color(16, 245, 10)};
    
    private static Color[] prices = {new Color(128, 255, 128), new Color(20, 255, 20), new Color(20, 128, 20), 
        new Color(255, 255, 128), new Color(255, 255, 0), new Color(179, 179, 0), new Color(255, 170, 170),
        new Color(255, 72, 72), new Color(240, 0, 0), new Color(255, 0, 128), new Color(64, 0, 128)};
    
	public static void drawCenteredStringAt(Graphics2D g2, String s, int startWidth, int width, 
			int height) 
	{
		FontMetrics fm = g2.getFontMetrics();
		int point = startWidth + width / 2 - fm.stringWidth(s) / 2;
		g2.drawString(s, point, height);
	}

	/** Splits line in fullDescription word by word, cuts words longer than width into smaller ones.
     * @param fullDescription String to cut
     * @param fm font metrics for calculation
     * @param width required width
     * @return divided lines
     */
    public static String[] splitLines(String fullDescription, FontMetrics fm, float width, int picSize, int lineSize) {
        String[] splits;
        float spaceSize = fm.stringWidth(" ");
        if(fullDescription != null) {
            ArrayList<String>arr = new ArrayList<String>(Arrays.asList(fullDescription.split(" ")));
            for(int j = 0; j < arr.size() - 1;) {
                if(j * lineSize < picSize && !arr.get(j).startsWith(" ")) {
                    String s = arr.get(j);
                    for(int x = 0; x < (picSize) / spaceSize; x++) {
                         s = " " + s;
                    }
                    s = " " + s;
                    arr.remove(j);
                    arr.add(j, s);
                }
                String both = arr.get(j) + " " + arr.get(j + 1);
                if(fm.stringWidth(both) < width - 10) {
                    arr.remove(j + 1);
                    arr.remove(j);
                    arr.add(j, both);
                } else {  
                    j++;
                }
            }
            int j = arr.size() - 1;
            if(j * lineSize < picSize && !arr.get(j).startsWith(" ")) {
                String s = arr.get(j);
                for(int x = 0; x < (picSize) / spaceSize; x++) {
                     s = " " + s;
                }
                s = " " + s;
                arr.remove(j);
                arr.add(j, s);
            }
            splits = arr.toArray(new String[arr.size()]);
        } else {
            splits = new String[0];
        }    
        return splits;
    }
	
	public static Image generateCard(BasicCard card) {
	    BufferedImage bi = new java.awt.image.BufferedImage(130, 200, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g2 = (Graphics2D)bi.getGraphics();
	    g2.setColor(Color.WHITE);
	    g2.fillRect(0, 0, bi.getWidth(), bi.getHeight());
	    
        g2.setColor(java.awt.Color.black);
        g2.drawRoundRect(0, 0, bi.getWidth()-1, bi.getHeight()-1, 45, 45);
        
        g2.setFont(font1);
        drawCenteredStringAt(g2, card.name, 10, bi.getWidth() - 20, 15);
        g2.setFont(font1a);
        DrawingOperations.drawCenteredStringAt(g2, card.description, 0, bi.getWidth(), 30);
        String type;
        if(card.type == CardType.Unit || card.type == CardType.Building) {
            UnitCard bc = (UnitCard)card;
            if(bc.cardClass != null && bc.cardClass != "") {
                DrawingOperations.drawCenteredStringAt(g2, String.format("*%s*", bc.cardClass),
                        0, bi.getWidth(), bi.getHeight() - 10);
            }
            type = String.format("%2dD / %2dH", bc.getDamage(), bc.getHealth());
        } else {
            type = "SPELL";
        }
        g2.setFont(font2);
        DrawingOperations.drawCenteredStringAt(g2, type, 0, bi.getWidth(), bi.getHeight() - 30); 
        g2.drawString(card.cost + "$", bi.getWidth() - 25, bi.getHeight() - 15); 
        if(card.energyCost != 0) {
            g2.drawString(card.cost + "E", 10, bi.getHeight() - 15);
        }
        g2.setColor(java.awt.Color.gray);
        g2.drawLine(0, bi.getHeight()-65, bi.getWidth(), bi.getHeight() - 65);
        g2.setColor(java.awt.Color.black);
        
        g2.drawImage(generateCardImg(card.name, card.cost, card.type), 5, 40, null);
        
        g2.setFont(font1a);
        if(card.fullDescription != null) {
            String[] splits = splitLines(card.fullDescription, g2.getFontMetrics(), bi.getWidth(), 50, 15);
            for(int i = 0; i < splits.length; i++) {
                g2.drawString(splits[i], 5, 45 + i * 15);
            }
        }

	    return bi;
	}
	
	private static void fillBuildingImage(String s, BufferedImage bi, int cost) {
	    Graphics2D g2 = bi.createGraphics();
	    int blockSize = 5;
        int maxBlocks = 5;
        
        if(cost >= 5) {
            blockSize = 7;
            maxBlocks = 3;
        } else if(cost > 7) {
            blockSize = 10;
            maxBlocks = 2;
        }
        
        Random r = new Random(s.charAt(0));
        int off = 0;
        boolean drawingUpper = true;
        for(int i = 0; i < s.length(); i++) {
            int posX = r.nextInt(s.charAt(i)) % maxBlocks;
            int posY = r.nextInt(s.charAt(i)) % maxBlocks;
            
            g2.setColor(colors[Math.abs(s.charAt(i) - 'a') % colors.length]);
            g2.fillRect(posX * blockSize, posY * blockSize + off, blockSize, blockSize);
            g2.fillRect(50 - (posX+1) * blockSize, posY * blockSize + off, blockSize, blockSize);
            if(i + 1 >= s.length() / 2 && drawingUpper) {
                drawingUpper = false;
                off += 25;
            }
        }
	}
	
	private static void fillSpellImage(String s, BufferedImage bi, int cost) {
	    Graphics2D g2 = bi.createGraphics();
        
        g2.setColor(prices[cost % 11]);
        int max = Math.min(s.length(), 10);
        for(int i = 0; i < max; i++) {
            int a = Math.abs(s.charAt(i) - 'a') % 27;
            int x = ((bi.getWidth() * a) / (27));
            int y = i * bi.getHeight() / max;
            g2.setColor(colors[Math.abs(s.charAt(i) - 'a') % colors.length]);
            g2.fillRect(x, y, 5, 5);
        }
	}
	
	public static Image generateCardImg(String s, int cost, CardType type) {
	    BufferedImage bi = new java.awt.image.BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g2 = bi.createGraphics();
	    g2.setColor(Color.WHITE);
	    g2.fillRect(0, 0, 50, 50);
	    g2.setColor(Color.black);
	    if(s.contains("."))
	        s = s.substring(0, s.indexOf("."));
	    s = s.toLowerCase(java.util.Locale.ENGLISH);
        
	    g2.drawRect(0, 0, bi.getWidth() - 1, bi.getHeight() - 1);
	    
	    if(type == CardType.Spell) {
	        fillSpellImage(s, bi, cost);
	    } else {
	        fillBuildingImage(s, bi, cost);
	    }
	    return bi;
	}
	
	public void paint (Graphics g) {
	    FileInputStream fis = null;
        try {
            fis = new FileInputStream("lines.txt");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        
        String line = null;
        int x = 0;
        int y = 1;
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.GREEN);
        g2.drawRect(1, 1, this.getWidth()-2, this.getHeight()-2);
        try {
            while ((line = br.readLine()) != null) {
                g2.drawImage(generateCardImg(line, 0, CardType.Building), x * 50, y * 50, 50, 50, null);
                x++;
                if(x > 15) {
                    x = 0;
                    y++;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/*public static void main(String[] args) throws FileNotFoundException {
	    DrawingOperations drop = new DrawingOperations();
	    drop.setSize(750, 500);
	    drop.setVisible(true);
	}*/
}
