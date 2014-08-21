package units;

import effects.AbstractSpell;

public class UnitPower {

	private TriggeringCondition tc;
	private AbstractSpell spell;
	
	public UnitPower(TriggeringCondition tc, AbstractSpell spell) {
		this.tc = tc;
		this.spell = spell;
	}
	
	public UnitPower(TriggeringCondition tc) {
		this.tc = tc;
	}
	
	public void applySpell(AbstractSpell s) {
		spell = s;
	}
	
	public boolean matchesCondition(TriggeringCondition c) {
		return tc == c;
	}
	
	public void exequte(Unit u, int player) {
		spell.target = u;
		spell.exequte(player);
	}

}
