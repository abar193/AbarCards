package effects;

import src.FieldSituation;
import src.Game;
import units.FieldObject;
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
	boolean repeats, aceptBuildings;
	/**
	 * Initialises targeter, who selects random targets from field
	 * @param acceptablePlayers 0 for the player, 1 for his opponent or -1 for both players
	 * @param maxCount at least 1 at most maxCount
	 * @param repeatsAllowed true to allow repeating
	 * @param aceptHeroes true, if heroes may be targeted as well
	 */
	public RandomTargeter(int acceptablePlayers, int maxCount, boolean repeatsAllowed, boolean aceptBuildings) {
		aceptPlayers = acceptablePlayers;
		this.maxCount = maxCount;
		repeats = repeatsAllowed;
		this.aceptBuildings = aceptBuildings;
	}

	@Override
	public ArrayList<FieldObject> selectTargets(int p, FieldObject targ, 
	        src.ProviderGameInterface currentGame) 
	{
		FieldSituation fs = currentGame.provideFieldSituation(); 
		player = p;
		Random r = new Random();
		ArrayList<FieldObject> retValue = new ArrayList<FieldObject>(maxCount);
		
		if(!repeats) {
			if(aceptPlayers != -1) {
				if(fs.allObjectsFromOneSide((player + aceptPlayers) % 2, aceptBuildings).size()
				        <= maxCount) 
				{
					return fs.allObjectsFromOneSide((player + aceptPlayers) % 2, aceptBuildings);
				}
			} else {
				if(fs.allObjects(aceptBuildings).size() <= maxCount) {
					return fs.allObjects(aceptBuildings);
				}
			}
		}
		
		ArrayList<FieldObject> arr;
		if(aceptPlayers == -1) arr = fs.allObjects(aceptBuildings);
		else arr = fs.allObjectsFromOneSide((player + aceptPlayers) % 2, aceptBuildings);
		
		if(arr.size() <= 0) return null;
		
		for(int i = 0; i < maxCount; ) {
		    FieldObject u = arr.get(r.nextInt(arr.size()));
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
	public boolean hasTargets(int player, FieldObject u, src.ProviderGameInterface currentGame) {
		FieldSituation fs = currentGame.provideFieldSituation();
		int targetsCount = (repeats) ? 0 : this.maxCount - 1;
		if(aceptPlayers == -1)
			return fs.allObjects(aceptBuildings).size() > targetsCount;
		else {
		    
			return fs.allObjectsFromOneSide((player + aceptPlayers) % 2, aceptBuildings).size() > targetsCount;
		}
	}

	@Override
	public void setFilter(UnitFilter f) {
		System.out.println("Not implemented!");
	}

	public String toString() {    
        return "RandomTargeter"; 
    }
}
