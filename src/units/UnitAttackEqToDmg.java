package units;

import cards.UnitCard;

public class UnitAttackEqToDmg extends Unit {
	
	boolean silenced;
	
	public UnitAttackEqToDmg(UnitCard card) {
		super(card);
		silenced = false;
	}

	public UnitAttackEqToDmg(UnitCard card, int qualities) {
		super(card, qualities);
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
