package effects;

import src.FieldSituation;
import units.Unit;
import java.util.Arrays;
/**
 * Picks all units of the specific player (or all players).
 * @author Abar
 */
public class AllUnitsTargeter implements Targeter {
	
	int acceptablePlayers;
	
	public AllUnitsTargeter(int aceptPlayers) {
		this.acceptablePlayers = aceptPlayers;
	}

	@Override
	public Unit[] selectTargets(FieldSituation fs, int p) {
		if(acceptablePlayers >= 0) {
			return fs.playerUnits.get((acceptablePlayers + p) % 2).
					toArray(new Unit[fs.playerUnits.
					                 get((acceptablePlayers + p) % 2).size()]);
		} else {
			Unit[] u1 = fs.playerUnits.get(0).
					toArray(new Unit[fs.playerUnits.get(0).size()]);
			Unit[] u2 = fs.playerUnits.get(1).
					toArray(new Unit[fs.playerUnits.get(1).size()]);
			Unit[] result = Arrays.copyOf(u1, u1.length + u2.length);
			System.arraycopy(u2, 0, result, u1.length, u2.length);
			return result;
		}
	}

	@Override
	public boolean hasTargets(FieldSituation fs, int player) {
		if(acceptablePlayers >= 0) {
			return fs.playerUnits.get((acceptablePlayers + player) % 2).size() > 0;
		} else {
			return fs.playerUnits.get(0).size() > 0 | 
					fs.playerUnits.get(1).size() > 0;
		}
	}

}
