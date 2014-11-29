package units;

import effects.AbstractSpell;

public class UnitPower {

	public UnitFilter filter;
	
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
		spell.required = true;
	}
	
	public boolean matchesCondition(TriggeringCondition c) {
		return tc == c;
	}
	
	public boolean validate(int player, src.ProviderGameInterface cG) {
	    return spell.validate(player, cG);
	}
	
	public void exequte(FieldObject u, int player, src.ProviderGameInterface cG) {
		spell.target = (Unit)u;
		spell.exequte(player, cG);
	}
	
	 public String toString() {
	     return "Power " + tc.toString() + " " + spell.toString();
	 }

}
