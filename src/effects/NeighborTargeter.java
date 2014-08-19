package effects;

import units.Unit;

import src.Game;
import src.FieldSituation;

public class NeighborTargeter implements Targeter {

	int offset;
	public NeighborTargeter(int offset) {
		this.offset = offset; 
	}

	@Override
	public Unit[] selectTargets(int player, Unit u) {
		Unit n = Game.currentGame.provideFieldSituation().neighborUnit(offset, u);
		if(n == null) 
			return null;
		Unit[] arr = new Unit[1];
		arr[0] = n; 
		return arr;
	}

	@Override
	public boolean hasTargets(int player, Unit u) {
		return Game.currentGame.provideFieldSituation().neighborUnit(offset, u) != null;
	}

}
