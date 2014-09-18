package ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import src.DeckBuilder;
import cards.BasicCard;
import cards.CardType;
import cards.UnitCard;

/**
 * Class for drawing available cards for player to choose. Placed inside CardPickingFrame
 * @author Abar
 *
 */
public class CardPickingScreen extends Panel {

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

	public void printCardAt(Graphics2D g2, BasicCard card, int startX, int startY, int width, int height) {
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
	}
	
	public void click(int c) {
		parent.cardClicked(cards.get(c + start));
	}
	
	public void paint (Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(new Font("SansSerif", Font.BOLD, 12));
		
		if(cards == null) return;
		int cardWidth = (this.getWidth() - 50) / 5;
		for(int i = 0; i < 5; i++) {
			printCardAt(g2, cards.get(i + start), 25 + (cardWidth + 5) * i, 50, cardWidth - 5, 200);
			printCardAt(g2, cards.get(i + 5 + start), 25 + (cardWidth + 5) * i, 260, cardWidth - 5, 200);
		}
	}
	
}
