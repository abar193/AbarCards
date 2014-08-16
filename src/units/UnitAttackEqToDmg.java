package units;

import cards.UnitCard;

public class UnitAttackEqToDmg extends Unit {

	public UnitAttackEqToDmg(UnitCard card) {
		super(card);
	}

	public UnitAttackEqToDmg(UnitCard card, int qualities) {
		super(card, qualities);
	}

	@Override
	public int getCurrentDamage() {
		return getCurrentHealth();
	}
}
