package effects;

import java.util.ArrayList;

import src.FieldSituation;
import units.Unit;

public class PlayerTargeter implements Targeter {

	boolean aceptHeroes;
	
	public PlayerTargeter(boolean aceptHeroes) {
		this.aceptHeroes = aceptHeroes;
	}
	
	@Override
	public ArrayList<Unit> selectTargets(int p, Unit t) {
		Unit u = src.Game.currentGame.askPlayerForTarget(p);
		ArrayList<Unit> arr = new ArrayList<Unit>(1);
		arr.add(u);
		return arr;
	}

	@Override
	public boolean hasTargets(int player, Unit u) {
		FieldSituation fs = src.Game.currentGame.provideFieldSituation();
		return fs.allUnits(aceptHeroes).size() > 0;
	}

}
