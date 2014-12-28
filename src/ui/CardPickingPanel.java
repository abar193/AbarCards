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
public class CardPickingPanel extends JPanel {

	private static final long serialVersionUID = 3908875203258467002L;
	public CardPickingFrame frameparent;
	private ArrayList<BasicCard> cards;
	int start;
	public int cardsPerPage = 0;
	
	public CardPickingPanel() {
		this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                float x = evt.getPoint().x;
                float y = evt.getPoint().y;
                int side = (int)(y / (getHeight() / 2));
                int card = (int)(x / (getWidth() / cardsPerPage));
                click((side) * cardsPerPage + card);
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
	
	public void click(int c) {
		frameparent.cardClicked(cards.get(c + start));
	}
	
	public void paint (Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setColor(java.awt.Color.white);
		
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g2.setColor(java.awt.Color.BLACK);
		if(cards == null) return;
		cardsPerPage = (this.getWidth() - 50) / 130;
		for(int i = 0; i < cardsPerPage; i++) {
		    if(cards.size() <= i + start) break;
			g2.drawImage(DrawingOperations.generateCard(cards.get(i+start)), 25 + 135 * i, 50, null);
			if(cards.size() > i + cardsPerPage + start)
			    g2.drawImage(DrawingOperations.generateCard(cards.get(i+cardsPerPage+start)), 
			            25 + 135 * i, 260, null);
		}
	}
}
