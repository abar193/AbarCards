package effects;

import src.FieldSituation;
import units.Unit;

public class PlayerTargeter implements Targeter {

	public PlayerTargeter() {
		
	}
	
	@Override
	public Unit[] selectTargets(int p, Unit t) {
		Unit u = src.Game.currentGame.askPlayerForTarget(p);
		Unit[] a = {u};
		return a;
	}

	@Override
	public boolean hasTargets(int player, Unit u) {
		FieldSituation fs = src.Game.currentGame.provideFieldSituation();
		return fs.playerUnits.get(0).size() > 0 | fs.playerUnits.get(1).size() > 0;
	}

}
