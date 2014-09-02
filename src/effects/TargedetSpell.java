package effects;

import java.util.ArrayList;

import units.Unit;
import units.UnitFilter;

public class TargedetSpell extends AbstractSpell {

	Targeter targeter;
	public AbstractSpell spell;
	
	public TargedetSpell(Targeter targ, AbstractSpell s) {
		targeter = targ;
		spell = s;
	}

	@Override
	public boolean validate(int player) {
		this.playerNum = player;
		return targeter.hasTargets(playerNum, target);
	}

	@Override
	public void exequte(int playerNum) {
		this.playerNum = playerNum;
		ArrayList<Unit> units = targeter.selectTargets(playerNum, target);
		if(units != null) {
			for(Unit u : units) {
				spell.target = u;
				spell.exequte(playerNum);
			}
		}
	}
	
	@Override
	public void setFilter(UnitFilter f) {
		targeter.setFilter(f);
		super.setFilter(f);
	}

}
