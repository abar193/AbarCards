package effects;

import src.FieldSituation;
import units.Unit;

import java.util.ArrayList;

/**
 * Picks all units of the specific player (or all players).
 * @author Abar
 */
public class AllUnitsTargeter implements Targeter {
	
	int acceptablePlayers;
	boolean aceptHeroes;
	
	public AllUnitsTargeter(int aceptPlayers, boolean aceptHeroes) {
		this.acceptablePlayers = aceptPlayers;
		this.aceptHeroes = aceptHeroes;
	}

	@Override
	public ArrayList<Unit> selectTargets(int p, Unit u) {
		FieldSituation fs = src.Game.currentGame.provideFieldSituation();
		if(acceptablePlayers >= 0) {
			return fs.allUnitFromOneSide((acceptablePlayers + p) % 2, aceptHeroes);
		} else {
			return fs.allUnits(aceptHeroes);
		}
	}
	
	@Override
	public boolean hasTargets(int player, Unit u) {
		FieldSituation fs = src.Game.currentGame.provideFieldSituation();
		if(acceptablePlayers >= 0) {
			return fs.allUnitFromOneSide((acceptablePlayers + player) % 2, aceptHeroes).size() > 0;
		} else {
			return fs.allUnits(aceptHeroes).size() > 0; 
		}
	}

}
