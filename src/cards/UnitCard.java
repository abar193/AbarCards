package cards;

import players.AuraEffect;

/**
 * Card used to store unit-relevant information, to create units
 * @author Abar
 */
public class UnitCard extends BasicCard {
	private int baseHealth;
	private int baseDamage;
	public AuraEffect[] auraEffects;
	
	public UnitCard(int health, int damage, int cost, String name, String desc) {
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
