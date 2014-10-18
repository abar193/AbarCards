package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.util.ArrayList;
import java.util.PriorityQueue;

import cards.BasicCard;
import cards.CardType;
import cards.UnitCard;
import src.FieldSituation;
import units.Unit;

/**
 * Class for drawing on-field situation. Calls reciveUnitClick method on SwingVS parent - should be set manualy before use of class
 * @author Abar
 */
public class FieldDrawer extends Panel {

	public SwingVS parent;
	
	private static final long serialVersionUID = 3562732874731857165L;
	
	private FieldSituation fs;
	private int selectedSide = 0, selectedUnit = 0;
	private int playerNumber = 0;
	
	public FieldDrawer() {
		fs = new FieldSituation(null);
		this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                float x = evt.getPoint().x;
                float y = evt.getPoint().y;
                int side = (int)(y / (getHeight() / 2));
                setLastClick((side + 1) % 2, (int)(x / (getWidth() / fs.MAXFIELDUNITS)));
            }
		});
	}

	public FieldDrawer(LayoutManager arg0) {
		super(arg0);
	}
	
	
	private void drawUnitsLine(Graphics2D g2, ArrayList<Unit> units, ArrayList<Integer> marked, int centerHeight) {
		int unitWidth = this.getWidth() / fs.MAXFIELDUNITS;
	    int uOffset = centerHeight - unitWidth / 2;
	    for(int x = 0; x < units.size(); x++) {
	    	if(marked.get(x) == 0) g2.setColor(Color.BLACK);
	    	else if(marked.get(x) == 1) g2.setColor(Color.GREEN);
	    	else if(marked.get(x) == 2) g2.setColor(Color.BLUE); 
	    	else g2.setColor(Color.RED);
    		g2.drawOval(unitWidth * x, uOffset - unitWidth / 2, unitWidth, unitWidth);
    		Unit u = units.get(x);
    		DrawingOperations.drawCenteredStringAt(g2, u.myCard.name, unitWidth * x, unitWidth, uOffset - 15);
    		DrawingOperations.drawCenteredStringAt(g2, u.descriptionString(), unitWidth * x, unitWidth, uOffset + 5);
    		DrawingOperations.drawCenteredStringAt(g2, String.format("%2dd/%2dh%2d$", u.getCurrentDamage(),
    				u.getCurrentHealth(), u.myCard.cost), unitWidth * x, unitWidth, uOffset + 15);
   
    	}
	}
	
	public void paint (Graphics g) {
	    Graphics2D g2 = (Graphics2D) g;
	    
	    int unitWidth = this.getWidth() / fs.MAXFIELDUNITS;
	    int unitHeight = this.getHeight() / 2;
	    g2.setFont(new Font("SansSerif", Font.BOLD, 12));
	    
	    ArrayList<Unit> units = fs.allUnitFromOneSide((playerNumber + 1) % 2, true);
	    ArrayList<Integer> statuses = new ArrayList<Integer>(units.size());
	    int taunts = fs.tauntUnitsForPlayer((playerNumber + 1) % 2);
	    for(Unit u : units) {
	    	if((taunts == 0 || u.hasQuality(Unit.Quality.Taunt)) && (!u.hasQuality(Unit.Quality.Stealth))) {
	    		statuses.add(-1);
	    	} else {
	    		statuses.add(0);
	    	}
	    }
	    drawUnitsLine(g2, fs.allUnitFromOneSide((playerNumber + 1) % 2, true), statuses, unitHeight * 2 / 3);
	    
	    units = fs.allUnitFromOneSide(playerNumber, true);
	    statuses = new ArrayList<Integer>(units.size());
	    
	    for(int i = 0; i < units.size(); i++) {
	        if(parent.turnEnded) statuses.add(0);
	        else if(parent.targeting && parent.selectedUnit == i) statuses.add(2);
	    	else statuses.add(units.get(i).canAttack() ? 1 : 0);
	    }
	    drawUnitsLine(g2, fs.allUnitFromOneSide(playerNumber, true), statuses, unitHeight * 5 / 3);
	    
	    
	    g2.drawLine(0, unitHeight, this.getWidth(), unitHeight);
	 
	}
	
	public void setLastClick(int side, int unit) {
		selectedSide = side;
		selectedUnit = unit;
		parent.receiveUnitClick(side, unit);
		repaint();
	}
	
	public void setSituation(FieldSituation fs, int pn) {
		this.fs = fs;
		playerNumber = pn;
		repaint();
	}


}
