package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.util.ArrayList;

import cards.BasicCard;
import cards.BuildingCard;
import cards.CardType;
import cards.UnitCard;

/**
 * Located in bottom side of the frame this class draws cards, held by player (player's hand)
 * @author Abar
 */
public class CardsDrawer extends Panel {

	public SwingVS parent;
	
	private static final long serialVersionUID = -4212993318911101034L;

	private ArrayList<BasicCard> cards;
	private int mana;
	int lastClick = 0;
	
	public CardsDrawer() {
		cards = new ArrayList<BasicCard>(0);
		this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                float x = evt.getPoint().x;
                setLastClick((int) (x / (getWidth()/10)));
            }
		});
	}
	
	public void paint (Graphics g) {
	    Graphics2D g2 = (Graphics2D) g;
	    int cardSize = this.getWidth() / 10;
	    g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
	    ArrayList<BasicCard> copycards = new ArrayList<BasicCard>(cards);
	    for(int i = 0; i < copycards.size(); i++) {
	    	if(copycards.get(i).cost <= mana && !parent.turnEnded) g2.setColor(Color.GREEN);
	    	else g2.setColor(Color.BLACK);
	    	g2.drawRect(cardSize * i + 5, 5, cardSize - 10, this.getHeight() - 10);
	    	g2.setColor(Color.BLACK);
	    	
	    	g2.drawString(copycards.get(i).name, cardSize * i + 7, 20);
	    	g2.drawString(copycards.get(i).description, cardSize * i + 7, 40);
	    	String dstr = "";
	    	BasicCard bc = copycards.get(i);
	    	if(bc.type == CardType.Unit) {
				dstr = (String.format("%2dd/%2dh%2d$", ((UnitCard) bc).getDamage(),
						((UnitCard) bc).getHealth(), bc.cost));
			} else if(bc.type == CardType.Spell) {
				dstr =  String.format("Spell  %2d$", bc.cost);
			} else if(bc.type == CardType.Building) {
			    if(((BuildingCard)bc).product != null) {
    			    dstr = (String.format("Product: %2d$", ((BuildingCard) bc).product.cost));
    			    g2.drawString(dstr, cardSize * i + 7, this.getHeight() - 25);
			    }
			    dstr = (String.format("%2dd/%2dh%2d$", ((BuildingCard) bc).getDamage(),
                        ((BuildingCard) bc).getHealth(), bc.cost));
			}
	    	g2.drawString(dstr, cardSize * i + 7, this.getHeight() - 15);
	    }
	}
	
	public void setLastClick(final int lc) {
		lastClick = lc;
		new Thread(new Runnable() {
			@Override
			public void run() {
				parent.receiveCardClick(lc);
			}
		}).start();
		repaint();
	}
	
	public void setCards(ArrayList<BasicCard> c, int mana) {
		cards = c;
		this.mana = mana;
		repaint();
	}
	
	public CardsDrawer(LayoutManager arg0) {
		super(arg0);
		
	}

}
