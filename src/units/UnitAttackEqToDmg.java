package units;

import cards.UnitCard;

public class UnitAttackEqToDmg extends Unit {
	
	boolean silenced;
	
	public UnitAttackEqToDmg(UnitCard card, int player) {
		super(card, player);
		silenced = false;
	}

	public UnitAttackEqToDmg(UnitCard card, int player, int qualities) {
		super(card, player, qualities);
	}

	@Override
	public int getCurrentDamage() {
		if(!silenced) return getCurrentHealth();
		else return modDmg;
	}
	
	@Override 
	public void applyBuff(effects.Buff b) {
		if(b.type == effects.BuffType.Silence) {
			silenced = true;
		} else {
			super.applyBuff(b);
		}
	}
}
