package effects;

import src.FieldSituation;
import src.Game;
import units.Unit;
import units.UnitFilter;

import java.util.Random;
import java.util.ArrayList;

/**
 * Targeter, who chooses some random targets on field to apply effect to.
 * May pick up to maxCount targets, repeating or not units.
 * @author Abar
 *
 */
public class RandomTargeter implements Targeter {

	int aceptPlayers, maxCount, player;
	boolean repeats, aceptHeroes;
	/**
	 * Initialises targeter, who selects random targets from field
	 * @param acceptablePlayers 0 for the player, 1 for his opponent or -1 for both players
	 * @param maxCount at least 1 at most maxCount
	 * @param repeatsAllowed true to allow repeating
	 * @param aceptHeroes true, if heroes may be targeted as well
	 */
	public RandomTargeter(int acceptablePlayers, int maxCount, boolean repeatsAllowed, boolean aceptHeroes) {
		aceptPlayers = acceptablePlayers;
		this.maxCount = maxCount;
		repeats = repeatsAllowed;
		this.aceptHeroes = aceptHeroes;
	}

	@Override
	public ArrayList<Unit> selectTargets(int p, Unit targ, src.ProviderGameInterface currentGame) {
		FieldSituation fs = currentGame.provideFieldSituation(); 
		player = p;
		Random r = new Random();
		ArrayList<Unit> retValue = new ArrayList<Unit>(maxCount);
		
		if(!repeats) {
			if(aceptPlayers != -1) {
				if(fs.allUnitFromOneSide((player + aceptPlayers) % 2, aceptHeroes).size() <= maxCount) {
					return fs.allUnitFromOneSide((player + aceptPlayers) % 2, aceptHeroes);
				}
			} else {
				if(fs.allUnits(aceptHeroes).size() <= maxCount) {
					return fs.allUnits(aceptHeroes);
				}
			}
		}
		
		ArrayList<Unit> arr;
		if(aceptPlayers == -1) arr = fs.allUnits(aceptHeroes);
		else arr = fs.allUnitFromOneSide((player + aceptPlayers) % 2, aceptHeroes);
		
		if(arr.size() <= 0) return null;
		
		for(int i = 0; i < maxCount; ) {
			Unit u = arr.get(r.nextInt(arr.size()));
			if(repeats) {
				retValue.add(u);
				i++;
			} else {
				if(!retValue.contains(u)) {
					retValue.add(u);
					i++;
				}		
			}
		}

		return retValue;
	}

	@Override
	public boolean hasTargets(int player, Unit u, src.ProviderGameInterface currentGame) {
		FieldSituation fs = currentGame.provideFieldSituation();
		
		if(aceptPlayers == -1)
			return fs.allUnits(aceptHeroes).size() > 0;
		else 
			return fs.allUnitFromOneSide((player + aceptPlayers) % 2, aceptHeroes).size() > 0;
	}

	@Override
	public void setFilter(UnitFilter f) {
		System.out.println("Not implemented!");
	}

	public String toString() {    
        return "RandomTargeter"; 
    }
}
