package ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.util.ArrayList;

import cards.BasicCard;
import cards.CardType;
import cards.UnitCard;
import src.FieldSituation;
import units.Unit;

public class FieldDrawer extends Panel {

	private static final long serialVersionUID = 3562732874731857165L;
	
	private FieldSituation fs;
	private int selectedSide = 0, selectedUnit = 0;
	private int playerNumber = 0;
	
	public FieldDrawer() {
		fs = new FieldSituation();
	}

	public FieldDrawer(LayoutManager arg0) {
		super(arg0);
	}
	
	public void paint (Graphics g) {
	    Graphics2D g2 = (Graphics2D) g;
	    
	    int unitWidth = this.getWidth() / 10;
	    int unitHeight = this.getHeight() / 2;
	    int uOffset = unitHeight / 2 - unitWidth / 2;
	    for(int y = 1; y >= 0; y--) {
	    	int ry = (y + 1) % 2;
	    	int player = (y + playerNumber) % 2;
	    	for(int x = 0; x < fs.countUnitsForSide(player, false); x++) {
	    		g2.drawOval(unitWidth * x, unitHeight * ry + uOffset, unitWidth, unitWidth);
	    		Unit u = fs.unitForPlayer(x, player);
	    		g2.drawString(u.myCard.name, unitWidth * x + 10, unitHeight * ry + uOffset - 5);
	    		g2.drawString(u.descriptionString(), unitWidth * x + 10, unitHeight * ry + unitHeight / 2 - 10);
	    		g2.drawString(String.format("%2dd/%2dh%2d$", u.getCurrentDamage(),
	    				u.getCurrentHealth(), u.myCard.cost), 
	    				unitWidth * x, unitHeight * ry + unitHeight / 2 + 3);
	    	}
	    }
	    
	    g2.drawLine(0, unitHeight, this.getWidth(), unitHeight);
	 
	}
	
	public void setLastClick(int side, int unit) {
		selectedSide = side;
		selectedUnit = unit;
		repaint();
	}
	
	public void setSituation(FieldSituation fs, int pn) {
		this.fs = fs;
		playerNumber = pn;
		repaint();
	}


}
