package effects;

import src.FieldSituation;
import units.FieldObject;
import units.Unit;
import units.UnitFilter;

import java.util.ArrayList;

/**
 * Picks all units of the specific player (or all players).
 * @author Abar
 */
public class AllUnitsTargeter implements Targeter {
	
	int acceptablePlayers;
	boolean aceptHeroes;
	boolean excludeSelf;
	UnitFilter filter;
	
	public AllUnitsTargeter(int aceptPlayers, boolean aceptHeroes) {
		this.acceptablePlayers = aceptPlayers;
		this.aceptHeroes = aceptHeroes;
		excludeSelf = false; 
	}

	@Override
	public ArrayList<FieldObject> selectTargets(int p, FieldObject u, src.ProviderGameInterface currentGame) {
		FieldSituation fs = currentGame.provideFieldSituation();
		if(acceptablePlayers >= 0) {
		    ArrayList<FieldObject> tmp;
		    if(filter == null) {
		        tmp = fs.allObjectsFromOneSide((acceptablePlayers + p) % 2, aceptHeroes);
		    } else {
		        tmp = fs.getObjectsMatchingFilter((acceptablePlayers + p) % 2, filter, aceptHeroes); 
		    }
				
			if(excludeSelf) tmp.remove(u);
			return tmp;
		} else {
			ArrayList<FieldObject> tmp;
			if(filter == null) {
                tmp = fs.allObjects(aceptHeroes);
            } else {
                tmp = fs.getObjectsMatchingFilter(-1, filter, aceptHeroes); 
            }
			
			if(excludeSelf) tmp.remove(u);
			return tmp;
		}
	}
	
	@Override
	public boolean hasTargets(int player, FieldObject u, src.ProviderGameInterface currentGame) {
		FieldSituation fs = currentGame.provideFieldSituation();
		if(acceptablePlayers >= 0) {
			if(filter == null) {
				return fs.allObjectsFromOneSide((acceptablePlayers + player) % 2, aceptHeroes).size() 
						> 0;
			} else {
				return fs.countObjectsMatchingFilter((acceptablePlayers + player) % 2,
						filter, aceptHeroes) > 0;
			}
				
		} else {
			if(filter == null) {
				return fs.allObjects(aceptHeroes).size() > 0; 
			} else {
				return fs.countObjectsMatchingFilter(-1, filter, aceptHeroes) > 0;
			}
		}
	}

	@Override
	public void setFilter(UnitFilter f) {
		filter = f;
	}
	
	public String toString() {    
        return "AllUnitsTargeter"; 
    }

}
