package units;

import cards.BasicCard;
import cards.UnitCard;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

/**
 * Represents single unit from battelfield. 
 * Each unit should be initialised with UnitCard, from witch base stats like health or 
 * damage should come. 
 * 
 * After initialising stats may be modified by applying BuffSpell on unit via applyBuff() method 
 * or heal(), damage().
 *  
 * Each unit may have some combnation of qualities. For now there are 4:
 * Windfury - unit may attack twice a turn;
 * Stealth - unit may not be targeted. Unit looses this quaity after first attack he makes;
 * Taunt - enemy units have to attack this unit;
 * Charge - unit may attack from the moment he spawned.
 * 
 * Each unit may also poses some "powers". Power is a spell, triggered by event from enum {@link TriggeringCondition}.
 * If a certain event (like "AllySpawn") occurs, respondToEvent() method of each unit should be called. 
 * If that unit has corresponding power (usually unit may have only 1 power, but there's room for more)
 *   he will call power's execute method passing himself and player as parameters.
 *   
 * @author Abar
 *
 */
public class Unit extends FieldObject {
	/** Enum for qualities unit may have. They are stored in int value  */
	
	private int attackedAlready;
	private boolean canAttack;
	
	@Override
	public Map<String, String> toMap() {
		Map<String, String> m = new LinkedHashMap<String, String>();
		m.put("MyCard", JSONValue.toJSONString(cards.CardJSONOperations.instance.mapFromCard(card)));
		m.put("ModDmg", Integer.toString(modDmg));
		m.put("ModHealth", Integer.toString(modHealth));
		m.put("ModQualities", Integer.toString(modQualities));
		m.put("MyPlayer", Integer.toString(player));
		m.put("CurrentHealth", Integer.toString(currentHealth));
		m.put("MaxHealth", Integer.toString(maxHealth));
		m.put("CurrentDamage", Integer.toString(currentDamage));
		m.put("Qualities", Integer.toString(qualities));
		m.put("AttackedAlready", Integer.toString(attackedAlready));
		m.put("CanAttack", Integer.toString(canAttack ? 1 : 0));
		return m;
	}
	
	@SuppressWarnings("unchecked")
	public Unit(Map m, src.ProviderGameInterface currentGame) {
		try { 
			card = (UnitCard) cards.CardJSONOperations.instance.cardFromMap((Map)m.get("MyCard"));
		} catch(ClassCastException e) {
			card = (UnitCard) cards.CardJSONOperations.instance.
					cardFromMap((Map)JSONValue.parse((String)m.get("MyCard")));
		}
		
		modDmg = Integer.parseInt((String) m.get("ModDmg"));
		modHealth = Integer.parseInt((String) m.get("ModHealth"));
		modQualities = Integer.parseInt((String) m.get("ModQualities"));
		player = Integer.parseInt((String) m.get("MyPlayer"));
		currentHealth = Integer.parseInt((String) m.get("CurrentHealth"));
		maxHealth = Integer.parseInt((String) m.get("MaxHealth"));
		currentDamage = Integer.parseInt((String) m.get("CurrentDamage"));
		qualities = Integer.parseInt((String) m.get("Qualities"));
		attackedAlready = Integer.parseInt((String) m.get("AttackedAlready"));
		canAttack = Integer.parseInt((String) m.get("CanAttack")) == 1;
		this.currentGame = currentGame;
	}
		
	/* Initialisation */
	public Unit(UnitCard card, int player, src.ProviderGameInterface currentGame) {
	    this.card = card;
        this.player = player;
        this.currentGame = currentGame;
        
		currentHealth = card.getHealth();
		maxHealth = currentHealth;
		currentDamage = card.getDamage();
		powers = new ArrayList<UnitPower>();
		modHealth = 0;
        modDmg = 0;
        
		canAttack = false;
		attackedAlready = 0;
	}
	
	/* Interaction */ 
	@Override
	public void startTurn() {
		canAttack = true;
		attackedAlready = 0;
		this.respondToEvent(TriggeringCondition.OnTurnStart, this);
	}
	
	@Override
	public void endTurn() {
		this.respondToEvent(TriggeringCondition.OnTurnEnd, this);
	}
	
	public void attackUnit(FieldObject u) {
		if(canAttack()) {
			if(u != null) {
				int td = u.getCurrentDamage();
				int md = getCurrentDamage();
				u.damage(md);
				damage(td);
			}
			
			if(hasQuality(Quality.Stealth)) {
				removeQuality(Quality.Stealth);
			}
			attackedAlready++;
		}
	}
	
	/* Status */
	public boolean canAttack() {
		if(hasQuality(Quality.NoAttack)) return false;
		return (getCurrentDamage() > 0) & (canAttack | hasQuality(Quality.Charge)) & 
				(attackedAlready <= (qualities & 1));
	}
	
	@Override
	public String toString() {
		return String.format("U[%s, D%d/%dH]", card.name, getCurrentDamage(), getCurrentHealth());
	}
}
