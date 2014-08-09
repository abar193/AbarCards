package effects;

import src.FieldSituation;
import units.Unit;

import java.util.Random;
import java.util.ArrayList;

import src.Game;

/**
 * Targeter, who chooses some random targets on field to apply effect to.
 * May pick up to maxCount targets, repeating or not units.
 * @author Abar
 *
 */
public class RandomTargeter implements Targeter {

	int aceptPlayers, maxCount, player;
	boolean repeats;
	/**
	 * Initialises targeter, who selects random targets from field
	 * @param player player-owner of the card
	 * @param acceptablePlayers 0 for the player, 1 for his opponent or -1 for both players
	 * @param maxCount at least 1 at most maxCount
	 * @param repeatsAllowed true to allow repeating
	 */
	public RandomTargeter(int player, int acceptablePlayers, int maxCount, boolean repeatsAllowed) {
		aceptPlayers = acceptablePlayers;
		this.maxCount = maxCount;
		repeats = repeatsAllowed;
		this.player = player;
	}

	@Override
	public Unit[] selectTargets(FieldSituation fs, Game g) {
		Random r = new Random();
		ArrayList<Unit> retValue = new ArrayList<Unit>(maxCount);
		
		if(!repeats) {
			if(aceptPlayers != -1) {
				if(fs.playerUnits.get(player + aceptPlayers).size() <= maxCount) {
					return (Unit[])fs.playerUnits.get(player + aceptPlayers).
							toArray(new Unit[retValue.size()]);
				}
			} else {
				if(fs.playerUnits.get(0).size() + fs.playerUnits.get(1).size() <= maxCount) {
					ArrayList<Unit> ret = fs.playerUnits.get(0);
					ret.addAll(fs.playerUnits.get(1));
					return (Unit[])ret.toArray(new Unit[retValue.size()]);
				}
			}
		}
		
		for(int i = 0; i < maxCount; ) {
			ArrayList<Unit> arr;
			if(aceptPlayers == -1) arr = pickRandomArray(fs, r);
			else arr = fs.playerUnits.get((player + aceptPlayers) % 2);
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

		return (retValue.toArray(new Unit[retValue.size()]));
	}
	
	public ArrayList<Unit> pickRandomArray(FieldSituation fs, Random r) {
		ArrayList<Unit> arr;
		if(r.nextBoolean()) {
			arr = fs.playerUnits.get(0);
			if(arr.size() == 0) arr = fs.playerUnits.get(1);
		} else {
			arr = fs.playerUnits.get(1);
			if(arr.size() == 0) arr = fs.playerUnits.get(0);
		}
		return arr;
	}

	@Override
	public boolean hasTargets(FieldSituation fs) {
		if(aceptPlayers == -1)
			return fs.playerUnits.get(0).size() > 0 || fs.playerUnits.get(1).size() > 0;
		else 
			return fs.playerUnits.get((player + aceptPlayers) % 2).size() > 0;
	}

}
