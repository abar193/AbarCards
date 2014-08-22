package effects;

import src.FieldSituation;
import src.Game;

import units.Unit;

import java.util.Arrays;
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
	boolean repeats;
	/**
	 * Initialises targeter, who selects random targets from field
	 * @param player player-owner of the card
	 * @param acceptablePlayers 0 for the player, 1 for his opponent or -1 for both players
	 * @param maxCount at least 1 at most maxCount
	 * @param repeatsAllowed true to allow repeating
	 */
	public RandomTargeter(int acceptablePlayers, int maxCount, boolean repeatsAllowed) {
		aceptPlayers = acceptablePlayers;
		this.maxCount = maxCount;
		repeats = repeatsAllowed;
	}

	@Override
	public Unit[] selectTargets(int p, Unit targ) {
		FieldSituation fs = Game.currentGame.provideFieldSituation(); 
		player = p;
		Random r = new Random();
		ArrayList<Unit> retValue = new ArrayList<Unit>(maxCount);
		
		if(!repeats) {
			if(aceptPlayers != -1) {
				if(fs.playerUnits.get((player + aceptPlayers) % 2).size() <= maxCount) {
					return (Unit[])fs.playerUnits.get((player + aceptPlayers) % 2).
							toArray(new Unit[retValue.size()]);
				}
			} else {
				if(fs.playerUnits.get(0).size() + fs.playerUnits.get(1).size() <= maxCount) {
					Unit[] u1 = fs.playerUnits.get(0).
							toArray(new Unit[fs.playerUnits.get(0).size()]);
					Unit[] u2 = fs.playerUnits.get(1).
							toArray(new Unit[fs.playerUnits.get(1).size()]);
					Unit[] result = Arrays.copyOf(u1, u1.length + u2.length);
					System.arraycopy(u2, 0, result, u1.length, u2.length);
					return result;
				}
			}
		}
		
		ArrayList<Unit> arr;
		if(aceptPlayers == -1) arr = pickRandomArray(fs, r);
		else arr = fs.playerUnits.get((player + aceptPlayers) % 2);
		
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

		return (retValue.toArray(new Unit[retValue.size()]));
	}
	
	public ArrayList<Unit> pickRandomArray(FieldSituation fs, Random r) {
		ArrayList<Unit> arr = new ArrayList<Unit>(fs.playerUnits.get(0));
		arr.addAll(fs.playerUnits.get(1));
		return arr;
	}

	@Override
	public boolean hasTargets(int player, Unit u) {
		FieldSituation fs = Game.currentGame.provideFieldSituation();
		
		if(aceptPlayers == -1)
			return fs.playerUnits.get(0).size() + fs.playerUnits.get(1).size() > 0;
		else 
			return fs.playerUnits.get((player + aceptPlayers) % 2).size() > 0;
	}

}
