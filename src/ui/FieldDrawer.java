package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.util.ArrayList;

import cards.BasicCard;
import cards.CardType;
import cards.UnitCard;
import src.FieldSituation;
import units.Building;
import units.FieldObject;
import units.PlayerUnit;
import units.Unit;
import units.Quality;

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
	
	
	private void drawUnitsLine(Graphics2D g2, ArrayList<FieldObject> units, 
	        ArrayList<Integer> marked, int center) 
	{
		int ballSize = Math.min(this.getWidth() / FieldSituation.MAXFIELDUNITS, this.getHeight() / 4);
	    int x = 0;
	    
	    if(units == null || units.size() == 0 || marked.size() == 0) return;
	    
	    for(x = 0; x < units.size(); x++) {
	    	if(marked.get(x) == 0) g2.setColor(Color.BLACK);
	    	else if(marked.get(x) == 1) g2.setColor(Color.GREEN);
	    	else if(marked.get(x) == 2) g2.setColor(Color.BLUE); 
	    	else g2.setColor(Color.RED);
    		g2.drawOval(ballSize * x, center - ballSize / 2, ballSize, ballSize);
    		FieldObject u = units.get(x);
    		DrawingOperations.drawCenteredStringAt(g2, u.card.name, ballSize * x, ballSize, center - 15);
    		DrawingOperations.drawCenteredStringAt(g2, u.descriptionString(), ballSize * x, ballSize, center + 5);
    		DrawingOperations.drawCenteredStringAt(g2, String.format("%2dd/%2dh%2d$", u.getCurrentDamage(),
    				u.getCurrentHealth(), u.card.cost), ballSize * x, ballSize, center + 15);
    	}
	}
	
	
	private void calculateRow(Graphics2D g2, ArrayList<FieldObject> units, int height) {
	    ArrayList<Integer> statuses = new ArrayList<Integer>(units.size());
        int taunts = fs.tauntObjectsForPlayerCount((playerNumber + 1) % 2);
        int i = 0;
        for(FieldObject u : units) {
            if(u.player != parent.playerNumber) {
                if((taunts == 0 || u.hasQuality(Quality.Taunt)) && (!u.hasQuality(Quality.Stealth))) {
                    statuses.add(-1);
                } else {
                    statuses.add(0);
                }
            } else {
                if(parent.turnEnded) statuses.add(0);
                else if(parent.targeting && parent.selectedUnit == i) statuses.add(2);
                else if(u instanceof Unit) {
                    statuses.add(((Unit)units.get(i)).canAttack() ? 1 : 0);
                } else statuses.add(0);
            }
            i++;
        }
        drawUnitsLine(g2, units, statuses, height);
	}
	
	public void paint(Graphics g) {
	    Graphics2D g2 = (Graphics2D) g;
	    float ballSize = this.getHeight() / 4;
	    
	    g2.setFont(new Font("SansSerif", Font.BOLD, 12));
	    int opponent = (playerNumber + 1) % 2;
	    
	    ArrayList<FieldObject> units = fs.allBuildingsFromOneSide(opponent);
	    calculateRow(g2, units, (int)(ballSize / 2));
	    units = fs.allObjectsFromOneSide(opponent, false);
	    calculateRow(g2, units, (int)(3 * ballSize / 2));
	    
	    g2.drawLine(0, (int)(ballSize * 2), this.getWidth(), (int)(ballSize * 2));
	    
	    units = fs.allObjectsFromOneSide(playerNumber, false);
        calculateRow(g2, units, (int)(5 * ballSize / 2));
        units = fs.allBuildingsFromOneSide(playerNumber);
        calculateRow(g2, units, (int)(7 * ballSize / 2));
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
