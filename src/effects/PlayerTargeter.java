package effects;

import java.util.ArrayList;

import src.FieldSituation;
import units.FieldObject;
import units.UnitFilter;

public class PlayerTargeter implements Targeter {

	boolean aceptBuildings;
	UnitFilter filter;
	
	public PlayerTargeter(boolean aceptHeroes) {
		this.aceptBuildings = aceptHeroes;
	}
	
	@Override
	public ArrayList<FieldObject> selectTargets(int p, FieldObject t, src.ProviderGameInterface currentGame) {
	    FieldObject u = currentGame.askPlayerForTarget(p);
		if(u == null) return null;
		if(!u.matchesFilter(filter)) {
			while(!u.matchesFilter(filter)) {
				u = currentGame.askPlayerForTarget(p);
			}
		}
		ArrayList<FieldObject> arr = new ArrayList<FieldObject>(1);
		if(u.matchesFilter(new UnitFilter(units.UnitFilterType.IsHero, "0")) && !aceptBuildings) 
            return null;
		arr.add(u);
		return arr;
	}

	@Override
	public boolean hasTargets(int player, FieldObject u, src.ProviderGameInterface currentGame) {
		FieldSituation fs = currentGame.provideFieldSituation();
		if(filter != null) {
			return fs.countObjectsMatchingFilter(-1, filter, aceptBuildings) > 0;
		}
		return fs.allObjects(aceptBuildings).size() > 0;
	}

	@Override
	public void setFilter(UnitFilter f) {
		filter = f;
	}
	
	public String toString() {    
        return "PlayerTargeter"; 
    }
}
