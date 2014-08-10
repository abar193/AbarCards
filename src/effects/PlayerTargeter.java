package effects;

import src.FieldSituation;
import units.Unit;

public class PlayerTargeter implements Targeter {

	public PlayerTargeter() {
		
	}

	@Override
	public Unit[] selectTargets(FieldSituation fs, int p) {
		Unit u = src.Game.currentGame.askPlayerForTarget(p);
		Unit[] a = {u};
		return a;
	}

	@Override
	public boolean hasTargets(FieldSituation fs, int player) {
		return fs.playerUnits.get(0).size() > 0 | fs.playerUnits.get(1).size() > 0;
	}

}
