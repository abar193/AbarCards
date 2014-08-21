package units;

import cards.UnitCard;
import java.util.Stack;
import java.util.ArrayList;

public class Unit {
	public enum Quality {
		Battlecry(1), Stealth(2), Taunt(4), Charge(8);
		private final int value;

	    private Quality(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}
	
	public UnitCard myCard;
	public int modDmg, modHealth;
	
	private int currentHealth;
	private int currentDamage;
	private int maxHealth;
	private int qualities = 0;
	private int myPlayer;
	
	private ArrayList<UnitPower> powers;
	
	private boolean canAttack, attackedAlready;
	
	public Unit(UnitCard card, int player) {
		currentHealth = card.getHealth();
		maxHealth = currentHealth;
		currentDamage = card.getDamage();
		myCard = card;
		powers = new ArrayList<UnitPower>();
		
		canAttack = false;
		attackedAlready = false;
		modHealth = 0;
		modDmg = 0;
		myPlayer = player;
	}
	
	public Unit(UnitCard card, int player, int qualities) {
		currentHealth = card.getHealth();
		maxHealth = currentHealth;
		currentDamage = card.getDamage();
		myCard = card;
		this.qualities = qualities;
		powers = new ArrayList<UnitPower>();
		
		canAttack = ((this.qualities & Quality.Charge.value) == 1);
		attackedAlready = false;
		modHealth = 0;
		modDmg = 0;
		myPlayer = player;
	}
	
	
	public void appyQualities(int q) {
		qualities = q;
	}
	
	public void addPower(UnitPower p) {
		powers.add(p);
	}
	
	public void startTurn() {
		this.respondToEvent(TriggeringCondition.OnTurnStart);
	}
	
	public void endTurn() {
		canAttack = true;
		attackedAlready = false;
		this.respondToEvent(TriggeringCondition.OnTurnEnd);
	}
	
	
	public boolean canAttack() {
		return (currentDamage > 0) & (canAttack | hasQuality(Quality.Charge)) & 
				!attackedAlready; 
	}
	
	public int getCurrentHealth() {
		return currentHealth + modHealth;
	}
	
	public int getCurrentDamage() {
		return currentDamage + modDmg;
	}
	
	public boolean hasQuality(Quality q) {
		return (qualities & q.getValue()) != 0;
	}
	
	public void setQuality(Quality q) {
		qualities |= q.getValue();
	}
	
	public void setQuality(int i) {
		qualities |= i;
	}
	
	public String descriptionString() {
		String s = "";
		for(Quality q : Quality.values()) {
			if(hasQuality(q)) {
				s += q.toString().charAt(0);
			}
		}
		if(s.equals("")) 
			s = myCard.description;
		else s = "<" + s + ">";
		return s;
	}
	
	/**
	 * Applies damage to unit. Method calls OnDamage event.
	 * @param d
	 */
	public void damage(int d) {
		currentHealth -= d;
		if(src.Game.currentGame != null)
			src.Game.currentGame.informAll(String.format("%s takes %d damage", 
				myCard.name, d));
		this.respondToEvent(TriggeringCondition.OnDamage);
	}
	
	public boolean isDead() {
		return currentHealth <= 0;
	}
	
	public void removeQuality(Quality q) {
		qualities = (0xFFFF ^ q.getValue()) & qualities;
	}
	
	public void applyBuff(effects.Buff b) {
		switch (b.type) {
		case AddDamage:
			currentDamage += b.value;
			break;
		case AddHealth:
			currentHealth += b.value;
			maxHealth += b.value;
			break;
		case AddQuality:
			setQuality(b.value);
			break;
		case Silence:
			currentHealth = Math.min(myCard.getHealth(), currentHealth);
			maxHealth = myCard.getHealth();
			currentDamage = myCard.getDamage();
			powers = new ArrayList<UnitPower>();
			qualities = 0;
			break;
		case DamageSetTo:
			currentDamage = b.value;
			break;
		case HealthSetTo:
			currentHealth = b.value;
			break;
		case Hurt: 
			damage(b.value);
			break;
		case Heal:
			currentHealth = Math.min(maxHealth, currentHealth + b.value);
			break;
		case Kill:
			damage(getCurrentHealth());
			break;
		default:
			break;
			
		}
	}
	
	public void attackUnit(Unit u) {
		if(canAttack()) {
			if(u != null) {
				u.damage(getCurrentDamage());
				damage(u.getCurrentDamage());
			}
			attackedAlready = true;
		}
	}
	
	/*  EVENTS   */
	
	public Stack<UnitPower> powersMatchingCondition(TriggeringCondition c) {
		Stack<UnitPower> s = new Stack<UnitPower>();
		for(UnitPower p : powers) {
			if(p.matchesCondition(c)) 
				s.push(p);
		}
		return s;
	}
	
	public void respondToEvent(TriggeringCondition e) {
		if(e == TriggeringCondition.OnDamage) {
			if(src.Game.currentGame != null)
				src.Game.currentGame.passEventAboutUnit(this, TriggeringCondition.OnAllyDamage);
		}
		for(UnitPower p : powersMatchingCondition(e)) {
			src.Game.currentGame.informAll(String.format("%s invokes his power", 
					myCard.name));
			p.exequte(this, myPlayer);
		}
	}
}
