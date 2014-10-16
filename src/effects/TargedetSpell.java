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
	public boolean validate(int player, src.ProviderGameInterface currentGame) {
		this.playerNum = player;
		return targeter.hasTargets(playerNum, target, currentGame);
	}

	@Override
	public void exequte(int playerNum, src.ProviderGameInterface currentGame) {
		this.playerNum = playerNum;
		ArrayList<Unit> units = targeter.selectTargets(playerNum, target, currentGame);
		if(units != null) {
			for(Unit u : units) {
				spell.target = u;
				spell.exequte(playerNum, currentGame);
			}
		}
	}
	
	@Override
	public void setFilter(UnitFilter f) {
		targeter.setFilter(f);
		super.setFilter(f);
	}

}
