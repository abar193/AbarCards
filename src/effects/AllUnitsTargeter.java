package effects;

import src.FieldSituation;
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
	public ArrayList<Unit> selectTargets(int p, Unit u) {
		FieldSituation fs = src.Game.currentGame.provideFieldSituation();
		if(acceptablePlayers >= 0) {
			ArrayList<Unit> tmp = (filter == null) ? fs.allUnitFromOneSide((acceptablePlayers + p)
					% 2, aceptHeroes) :
				fs.getUnitsMatchingFilter((acceptablePlayers + p) % 2, filter, aceptHeroes);
			if(excludeSelf) tmp.remove(u);
			return tmp;
		} else {
			ArrayList<Unit> tmp = (filter == null) ? fs.allUnits(aceptHeroes) :
				fs.getUnitsMatchingFilter(-1, filter, aceptHeroes);
			
			if(excludeSelf) tmp.remove(u);
			
			return tmp;
		}
	}
	
	@Override
	public boolean hasTargets(int player, Unit u) {
		FieldSituation fs = src.Game.currentGame.provideFieldSituation();
		if(acceptablePlayers >= 0) {
			if(filter == null) {
				return fs.allUnitFromOneSide((acceptablePlayers + player) % 2, aceptHeroes).size() 
						> 0;
			} else {
				return fs.countUnitsMatchingFilter((acceptablePlayers + player) % 2,
						filter, aceptHeroes) > 0;
			}
				
		} else {
			if(filter == null) {
				return fs.allUnits(aceptHeroes).size() > 0; 
			} else {
				return fs.countUnitsMatchingFilter(-1, filter, aceptHeroes) > 0;
			}
		}
	}

	@Override
	public void setFilter(UnitFilter f) {
		filter = f;
	}

}
