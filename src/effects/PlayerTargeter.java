package effects;

import java.util.ArrayList;

import src.FieldSituation;
import units.Unit;
import units.UnitFilter;

public class PlayerTargeter implements Targeter {

	boolean aceptHeroes;
	UnitFilter filter;
	
	public PlayerTargeter(boolean aceptHeroes) {
		this.aceptHeroes = aceptHeroes;
	}
	
	@Override
	public ArrayList<Unit> selectTargets(int p, Unit t) {
		Unit u = src.Game.currentGame.askPlayerForTarget(p);
		if(!u.matchesFilter(filter)) {
			while(!u.matchesFilter(filter)) {
				u = src.Game.currentGame.askPlayerForTarget(p);
			}
		}
		ArrayList<Unit> arr = new ArrayList<Unit>(1);
		arr.add(u);
		return arr;
	}

	@Override
	public boolean hasTargets(int player, Unit u) {
		FieldSituation fs = src.Game.currentGame.provideFieldSituation();
		if(filter != null) {
			return fs.countUnitsMatchingFilter(-1, filter, aceptHeroes) > 0;
		}
		return fs.allUnits(aceptHeroes).size() > 0;
	}

	@Override
	public void setFilter(UnitFilter f) {
		filter = f;
	}

}
