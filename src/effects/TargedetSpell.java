package effects;

import units.Unit;

public class TargedetSpell extends AbstractSpell {

	Targeter targeter;
	public AbstractSpell spell;
	
	public TargedetSpell(Unit t, int p, Targeter targ, AbstractSpell s) {
		super(t, p);
		targeter = targ;
		spell = s;
	}

	@Override
	public boolean validate() {
		return targeter.hasTargets(playerNum, target);
	}

	@Override
	public void exequte() {
		Unit[] units = targeter.selectTargets(playerNum, target);
		for(Unit u : units) {
			spell.target = u;
			spell.exequte();
		}
	}

}
