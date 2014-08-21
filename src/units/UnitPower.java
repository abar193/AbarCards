package units;

import effects.AbstractSpell;

public class UnitPower {

	private TriggeringCondition tc;
	private AbstractSpell spell;
	
	public UnitPower(TriggeringCondition tc, AbstractSpell spell) {
		this.tc = tc;
		this.spell = spell;
	}
	
	public boolean matchesCondition(TriggeringCondition c) {
		return tc == c;
	}
	
	public void exequte(Unit u, int player) {
		spell.target = u;
		spell.exequte(player);
	}

}
