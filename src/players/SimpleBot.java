package players;

import src.FieldSituation;
import src.Game;
import units.Unit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SimpleBot implements PlayerInterface {

	private Game parent;
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
		availableTargets = new ArrayList<Unit>(field.playerUnits.
				get(enemyData.playerNumber).size());
		
		for(Unit u : field.playerUnits.get(opponent.playerNumber)) {
			if(enemyTaunts == 0 || u.hasQuality(Unit.Quality.Taunt)) {
				availableTargets.add(u);
			}
		}
		
		totalAvailableDamage = 0;
		totalAvailableHealth = totalAvailableUnits = 0;
		for(Unit u : field.playerUnits.get(me.playerNumber)) {
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
	public void setParentGame(Game g) {
		parent = g;

	}

	@Override
	public void run() {
		
		attackWithAllUnits();
		playAllCards();
		attackWithAllUnits();
		
	}

	protected void attackWithAllUnits() {
		
		ArrayList<Unit> myDeck = latestSituation.playerUnits.get(me.playerNumber);
		ArrayList<Unit> hisDeck = latestSituation.playerUnits.get(opponent.playerNumber);
		Random r = new Random();
		
		while(totalAvailableUnits > 0) {
			if(totalAvailableDamage >= opponent.health && enemyTaunts == 0) { // Finish him
				for(int i = 0; i < myDeck.size(); i++) {
					if(myDeck.get(i).canAttack()) {
						parent.commitAttack(i, -1, me.playerNumber, opponent.playerNumber);
						myDeck = latestSituation.playerUnits.get(me.playerNumber);
					}
				}
			} else {
				for(int i = 0; i < myDeck.size();) {
					if(myDeck.get(i).canAttack()) {
						if(availableTargets.size() > 0) {
							parent.commitAttack(myDeck.get(i), 
									availableTargets.get(r.nextInt(availableTargets.size())));
							myDeck = latestSituation.playerUnits.get(me.playerNumber);
							hisDeck = latestSituation.playerUnits.get(opponent.playerNumber);
							i = 0;
						} else {
							parent.commitAttack(i, -1, me.playerNumber, opponent.playerNumber);
						}
					} else {
						i++;
					}
				}
			} 
		}
	}
	
	protected void playAllCards() {
		for(int i = 0; i < me.getHandSize(); i++) {
			if(me.canPlayCard(me.getHand().get(i))) {
				parent.playCard(me.getHand().get(i), me.playerNumber);
			}
		}
	}
	
	@Override
	public Unit selectTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
