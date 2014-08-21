package effects;

import units.Unit;

public class TargedetSpell extends AbstractSpell {

	Targeter targeter;
	public AbstractSpell spell;
	
	public TargedetSpell(Targeter targ, AbstractSpell s) {
		targeter = targ;
		spell = s;
	}

	@Override
	public boolean validate() {
		return targeter.hasTargets(playerNum, target);
	}

	@Override
	public void exequte(int playerNum) {
		this.playerNum = playerNum;
		Unit[] units = targeter.selectTargets(playerNum, target);
		if(units != null) {
			for(Unit u : units) {
				spell.target = u;
				spell.exequte(playerNum);
			}
		}
	}

}
