package players;

import src.FieldSituation;
import src.GameInterface;
import ui.VisualSystemInterface;
import units.Unit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SimpleBot implements PlayerInterface {

	private GameInterface parent;
	private FieldSituation latestSituation;
	private PlayerData me;
	private PlayerOpenData opponent;
	
	private int totalAvailableDamage;
	private int totalAvailableHealth;
	private int totalAvailableUnits;
	private int enemyTaunts;
	private ArrayList<Unit> availableTargets;
	
	public SimpleBot() {
		
	}

	@Override
	public void reciveInfo(PlayerData yourData, FieldSituation field,
			PlayerOpenData enemyData) 
	{
		me = yourData;
		latestSituation = field;
		opponent = enemyData;
		
		enemyTaunts = field.tauntUnitsForPlayer(enemyData.playerNumber);
		availableTargets = new ArrayList<Unit>(field.allUnitFromOneSide(enemyData.playerNumber, 
				false).size());
		
		for(Unit u : field.allUnitFromOneSide(opponent.playerNumber, false)) {
			if((enemyTaunts == 0 || u.hasQuality(Unit.Quality.Taunt)) & 
					!u.hasQuality(Unit.Quality.Stealth)) {
				availableTargets.add(u);
			}
		}
		
		totalAvailableDamage = 0;
		totalAvailableHealth = totalAvailableUnits = 0;
		for(Unit u : field.allUnitFromOneSide(me.playerNumber, false)) {
			if(u.canAttack()) {
				totalAvailableDamage += u.getCurrentDamage();
				totalAvailableUnits++;
			}
			totalAvailableHealth += u.getCurrentHealth();
		}
	}

	@Override
	public void reciveAction(String m) {

	}

	@Override
	public void run() {
		attackWithAllUnits();
		playAllCards();
		attackWithAllUnits();
	}

	protected void attackWithAllUnits() {
		
		ArrayList<Unit> myDeck = latestSituation.allUnitFromOneSide(me.playerNumber, false);
		ArrayList<Unit> hisDeck = latestSituation.allUnitFromOneSide(opponent.playerNumber, false);
		Random r = new Random();
		
		while(totalAvailableUnits > 0) {
		    if(Thread.interrupted()) return;
		    
			if(totalAvailableDamage >= opponent.health && enemyTaunts == 0) { // Finish him
				for(int i = 0; i < myDeck.size(); i++) {
					if(myDeck.get(i).canAttack()) {
						parent.commitAttack(i, -1, me.playerNumber);
						myDeck = latestSituation.allUnitFromOneSide(me.playerNumber, false);
					}
				}
			} else {
				for(int i = 0; i < myDeck.size();) {
				    if(Thread.interrupted()) return;
					if(myDeck.get(i).canAttack()) {
						if(availableTargets.size() > 0) {
							parent.commitAttack(myDeck.get(i), 
									availableTargets.get(r.nextInt(availableTargets.size())));
							myDeck = latestSituation.allUnitFromOneSide(me.playerNumber, false);
							hisDeck = latestSituation.allUnitFromOneSide(opponent.playerNumber, 
									false);
							i = 0;
						} else {
							parent.commitAttack(i, -1, me.playerNumber);
						}
					} else {
						i++;
					}
				}
			} 
		}
	}
	
	protected void playAllCards() {
		for(int i = 0; i < me.getHandSize(); ) {
		    if(Thread.interrupted()) return;
			if(me.canPlayCard(me.getHand().get(i))) {
				parent.playCard(me.getHand().get(i), me.playerNumber);
				i = 0;
			} else {
				i++;
			}
		}
	}
	
	@Override
	public Unit selectTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VisualSystemInterface visual() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParentGameInterface(GameInterface g) {
		parent = g;
		
	}

}
