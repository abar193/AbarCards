package units;

import cards.UnitCard;

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
	
	private boolean canAttack, attackedAlready;
	
	public Unit(UnitCard card) {
		currentHealth = card.getHealth();
		maxHealth = currentHealth;
		currentDamage = card.getDamage();
		myCard = card;
		
		canAttack = false;
		attackedAlready = false;
		modHealth = 0;
		modDmg = 0;
	}
	
	public Unit(UnitCard card, int qualities) {
		currentHealth = card.getHealth();
		maxHealth = currentHealth;
		currentDamage = card.getDamage();
		myCard = card;
		this.qualities = qualities;
		
		canAttack = ((this.qualities & Quality.Charge.value) == 1);
		attackedAlready = false;
		modHealth = 0;
		modDmg = 0;
	}
	
	public void appyQualities(int q) {
		qualities = q;
	}
	
	public void endTurn() {
		canAttack = true;
		attackedAlready = false;
	}
	
	public boolean canAttack() {
		return (currentDamage > 0) & (canAttack | hasQuality(Quality.Charge)) & !attackedAlready; 
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
	
	
	public void damage(int d) {
		currentHealth -= d;
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
			qualities = 0;
			break;
		case DamageSetTo:
			currentDamage = b.value;
			break;
		case HealthSetTo:
			currentHealth = b.value;
			break;
		case Hurt: 
			currentHealth -= b.value;
			break;
		case Heal:
			currentHealth = Math.min(maxHealth, currentHealth + b.value);
			break;
		case Kill:
			currentHealth = 0;
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
	
}
