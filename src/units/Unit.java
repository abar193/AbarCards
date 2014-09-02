package units;

import cards.UnitCard;

import java.util.Stack;
import java.util.ArrayList;

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
 *   he will call power's execute method passing himself and myPlayer as parameters.
 *   
 * @author Abar
 *
 */
public class Unit {
	/** Enum for qualities unit may have. They are stored in int value  */
	public enum Quality {
		Windfury(1), Stealth(2), Taunt(4), Charge(8);
		private final int value;

	    private Quality(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}
	
	public UnitCard myCard;
	
	public int modDmg, modHealth, modQualities;
	public int myPlayer;
	
	protected int currentHealth;
	protected int maxHealth;
	
	private int currentDamage;
	private int qualities = 0;
	private int attackedAlready;
	
	private ArrayList<UnitPower> powers;
	private boolean canAttack;
	
	
	/* Initialisation */
	public Unit(UnitCard card, int player) {
		currentHealth = card.getHealth();
		maxHealth = currentHealth;
		currentDamage = card.getDamage();
		myCard = card;
		powers = new ArrayList<UnitPower>();
		
		canAttack = false;
		attackedAlready = 0;
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
		attackedAlready = 0;
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
	
	/* Interaction */ 
	public void startTurn() {
		canAttack = true;
		attackedAlready = 0;
		this.respondToEvent(TriggeringCondition.OnTurnStart, this);
	}
	
	public void endTurn() {
		this.respondToEvent(TriggeringCondition.OnTurnEnd, this);
	}
	
	public void attackUnit(Unit u) {
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
		return (getCurrentDamage() > 0) & (canAttack | hasQuality(Quality.Charge)) & 
				(attackedAlready <= (qualities & 1)); 
	}
	
	public int getCurrentHealth() {
		return currentHealth + modHealth;
	}
	
	public int getCurrentDamage() {
		return currentDamage + modDmg;
	}
	
	public boolean isDead() {
		return currentHealth <= 0;
	}
	
	public boolean matchesFilter(UnitFilter f) {
		if(f == null) return true;
		
		int value;
		try {
			value = Integer.parseInt(f.value);
		} catch (NumberFormatException e) {
			value = 0;
		}
		
		switch(f.type) {
			case ClassEquals:
				return f.value.equals(myCard.cardClass);
			case DamageLess:
				return getCurrentDamage() < value;
			case DamageMore:
				return getCurrentDamage() > value;
			case HealthLess:
				return getCurrentHealth() < value;
			case HealthMore:
				return getCurrentHealth() > value;
		}
		return false;
	}
	
	public boolean hasQuality(Quality q) {
		return ((qualities | modQualities) & q.getValue()) != 0;
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
	
	/* Input */
	public void setQuality(Quality q) {
		qualities |= q.getValue();
	}
	
	public void setQuality(int i) {
		qualities |= i;
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
		this.respondToEvent(TriggeringCondition.OnDamage, this);
	}
	
	/**
	 * Heals unit. Method calls OnDamage event.
	 * @param d
	 */
	public void heal(int v) {
		currentHealth = Math.min(currentHealth + v, maxHealth);
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
				heal(b.value);
				break;
			case Kill:
				damage(getCurrentHealth());
				break;
			case ModDmg:
				modDmg += b.value;
				break;
			case ModHealth:
				modHealth += b.value;
				break;
			case ModQuality:
				modQualities |= b.value;
			default:
				System.out.println("Unknown buff" + b.type.toString());
				break;
			
		}
	}
	
	/* Events */
	public Stack<UnitPower> powersMatchingCondition(TriggeringCondition c) {
		Stack<UnitPower> s = new Stack<UnitPower>();
		for(UnitPower p : powers) {
			if(p.matchesCondition(c)) 
				s.push(p);
		}
		return s;
	}
	
	public void respondToEvent(TriggeringCondition e, Unit otherU) {
		if(e == TriggeringCondition.OnDamage) {
			if(src.Game.currentGame != null)
				src.Game.currentGame.passEventAboutUnit(this, TriggeringCondition.OnAllyDamage);
		}
		for(UnitPower p : powersMatchingCondition(e)) {
			if(p.filter == null || otherU.matchesFilter(p.filter)) {
				if(e != TriggeringCondition.Always)
					src.Game.currentGame.informAll(String.format("%s invokes his power", 
							myCard.name));
				p.exequte(this, myPlayer);
			}
		}
	}
}
