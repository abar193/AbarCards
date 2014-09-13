package ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Panel;


public class EnemySideDrawer extends Panel {

	
	private static final long serialVersionUID = -552044581190090915L;
	int cardsCount = 0;
	
	public EnemySideDrawer() {
		// TODO Auto-generated constructor stub
	}

	public EnemySideDrawer(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}
	
	public void paint (Graphics g) {
	    Graphics2D g2 = (Graphics2D) g;
	    int cardSize = this.getWidth() / 10;
	    for(int i = 0; i < cardsCount; i++) {
	    	g2.drawRect(cardSize * i + 5, 5, cardSize - 10, this.getHeight() - 10);
	    }
	 
	}
		
	public void setCards(int c) {
		cardsCount = c;
		repaint();
	}


}
