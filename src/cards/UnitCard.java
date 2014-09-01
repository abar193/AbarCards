package cards;

import units.Unit.Quality;
import effects.AuraEffect;

/**
 * Card used to store unit-relevant information, to create units
 * @author Abar
 */
public class UnitCard extends BasicCard {
	private int baseHealth;
	private int baseDamage;
	public AuraEffect[] auraEffects; 
	public units.UnitPower power = null;
	public int qualities;
	public String cardClass;
	
	public UnitCard(int damage, int health, int cost, String name, String desc) {
		super(name, desc, cost);
		baseHealth = health;
		baseDamage = damage;
		this.type = CardType.Unit;
	}
	
	public int getDamage() {
		return baseDamage;
	}
	
	public int getHealth() {
		return baseHealth;
	}
		
	@Override
	public String debugDisplay() {
		return name + " " + description;
	}
}
