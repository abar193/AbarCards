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
	
	private int currentHealth;
	private int currentDamage;
	private int qualities = 0;
	
	private boolean canAttack, attackedAlready;
	
	public Unit(UnitCard card) {
		currentHealth = card.getHealth();
		currentDamage = card.getDamage();
		myCard = card;
		
		if(card instanceof cards.QualityUnitCard) {
			qualities = ((cards.QualityUnitCard)card).qualities;
		}
		
		canAttack = false;
		attackedAlready = false;
	}
	
	public Unit(UnitCard card, int qualities) {
		currentHealth = card.getHealth();
		currentDamage = card.getDamage();
		myCard = card;
		this.qualities = qualities;
		
		canAttack = ((this.qualities & Quality.Charge.value) == 1);
		attackedAlready = false;
	}
	
	public void endTurn() {
		canAttack = true;
		attackedAlready = false;
	}
	
	public boolean canAttack() {
		return (currentDamage > 0) & (canAttack | hasQuality(Quality.Charge)) & !attackedAlready; 
	}
	
	public int getCurrentHealth() {
		return currentHealth;
	}
	
	public int getCurrentDamage() {
		return currentDamage;
	}
	
	public boolean hasQuality(Quality q) {
		return (qualities & q.getValue()) != 0;
	}
	
	public void setQuality(Quality q) {
		qualities |= q.getValue();
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
		case Damage:
			currentDamage += b.value;
			break;
		case Health:
			currentHealth += b.value;
			break;
		case Quality:
			break;
		case Silence:
			currentHealth = myCard.getHealth();
			currentDamage = myCard.getDamage();
			qualities = 0;
			break;
		default:
			break;
			
		}
	}
	
	public void attackUnit(Unit u) {
		if(canAttack()) {
			u.damage(getCurrentDamage());
			damage(u.getCurrentDamage());
		}
	}
	
}
