package ui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import cards.BasicCard;
import cards.CardType;
import cards.UnitCard;

/**
 * Class for drawing available cards for player to choose. Placed inside CardPickingFrame
 * @author Abar
 *
 */
public class CardPickingScreen extends JPanel {

	private static final long serialVersionUID = 3908875203258467002L;
	public CardPickingFrame parent;
	private ArrayList<BasicCard> cards;
	int start;
	
	public CardPickingScreen() {
		this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                float x = evt.getPoint().x;
                float y = evt.getPoint().y;
                int side = (int)(y / (getHeight() / 2));
                int card = (int)(x / (getWidth() / 5));
                click((side) * 5 + card);
            }
		});
	}

	public void setDrawnCards(ArrayList<BasicCard> cards, int start) {
		this.cards = cards;
		this.start = start;
		repaint();
	}
	
    private static final Hashtable<TextAttribute, Object> map = 
            new Hashtable<TextAttribute, Object>();
    static {
        map.put(TextAttribute.FAMILY, "Serif");
        map.put(TextAttribute.SIZE, new Float(18.0));
    }  

	
	public void printCardAt(Graphics2D g2, BasicCard card, int startX, int startY, int width, 
	        int height) 
	{
		g2.drawRect(startX, startY, width, height);
		DrawingOperations.drawCenteredStringAt(g2, card.name, startX, width, startY + 15);
		DrawingOperations.drawCenteredStringAt(g2, card.description, startX, width, startY + 30);
		String type;
		if(card.type == CardType.Unit) {
			UnitCard bc = (UnitCard)card;
			type = String.format("|%2dd/%2dh%2d$|", bc.getDamage(),
					bc.getHealth(), bc.cost);
		} else {
			type = String.format("|%6s%3d$|", "Spell", card.cost);
		}
		DrawingOperations.drawCenteredStringAt(g2, type, startX, width, startY + height - 30); 
		
		if(card.fullDescription != null) {
		    String[] splits = splitLines(card.fullDescription, g2.getFontMetrics(), width);
		    for(int i = 0; i < splits.length; i++) {
		        g2.drawString(splits[i], startX + 5, startY + 50 + i * 15);
		    }
		}
        

	}
	
	public void click(int c) {
		parent.cardClicked(cards.get(c + start));
	}
	
	public void paint (Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(new Font("SansSerif", Font.BOLD, 12));
		g2.setColor(java.awt.Color.white);
		
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g2.setColor(java.awt.Color.BLACK);
		if(cards == null) return;
		int cardWidth = (this.getWidth() - 50) / 5;
		for(int i = 0; i < 5; i++) {
			printCardAt(g2, cards.get(i + start), 25 + (cardWidth + 5) * i, 50, cardWidth - 5, 200);
			printCardAt(g2, cards.get(i + 5 + start), 25 + (cardWidth + 5) * i, 260, cardWidth - 5, 200);
		}
	}
	
	/** Splits lines in fullDescription word by word, cuts words longer than 10 chars
     * into smaller ones, and groups small words if their sum length < 10.
     * @param deck ArrayList of cards, from where fullDescription is being read
     * @param start Starting index. Will be proceeded cards with 
     *      indexes [start..start + CARDS_IN_A_ROW]
     * @return String[][] array, where first index stands for corresponding card, 
     *      and second index - for cut lines of fullDescription.    
     */
    public String[] splitLines(String fullDescription, FontMetrics fm, float width) {
        String[] splits;
        
        if(fullDescription != null) {
            ArrayList<String>arr = new ArrayList<String>(Arrays.asList(fullDescription.split(" ")));
            for(int j = 0; j < arr.size() - 1;) {
                String both = arr.get(j) + " " + arr.get(j + 1);
                if(fm.stringWidth(both) < width - 10) {
                    arr.remove(j + 1);
                    arr.remove(j);
                    arr.add(j, both);
                } else {  
                    j++;
                }
            }
            
            splits = arr.toArray(new String[arr.size()]);
        } else {
            splits = new String[0];
        }    
        return splits;
    }
}
