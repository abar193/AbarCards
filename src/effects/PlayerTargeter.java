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
	public ArrayList<Unit> selectTargets(int p, Unit t, src.ProviderGameInterface currentGame) {
		Unit u = currentGame.askPlayerForTarget(p);
		if(u == null) return null;
		if(!u.matchesFilter(filter)) {
			while(!u.matchesFilter(filter)) {
				u = currentGame.askPlayerForTarget(p);
			}
		}
		ArrayList<Unit> arr = new ArrayList<Unit>(1);
		if(u.matchesFilter(new UnitFilter(units.UnitFilterType.IsHero, "0")) && !aceptHeroes) 
            return null;
		arr.add(u);
		return arr;
	}

	@Override
	public boolean hasTargets(int player, Unit u, src.ProviderGameInterface currentGame) {
		FieldSituation fs = currentGame.provideFieldSituation();
		if(filter != null) {
			return fs.countUnitsMatchingFilter(-1, filter, aceptHeroes) > 0;
		}
		return fs.allUnits(aceptHeroes).size() > 0;
	}

	@Override
	public void setFilter(UnitFilter f) {
		filter = f;
	}
	
	public String toString() {    
        return "PlayerTargeter"; 
    }
}
