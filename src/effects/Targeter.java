package effects;

import java.util.ArrayList;

import units.Unit;

/**
 * Targeters are used with TargedetSpells to choose targets. 
 * 
 * Targeters may require game.provideFieldSituation(), askPlayerForTarget() or field.getNeighbors()
 * @author Abar
 */
public interface Targeter {
	/**
	 * Selects target units and returns them as array. 
	 * @param player player's number, who activated spell
	 * @param u source unit 
	 * @return array of targets
	 */
	public ArrayList<Unit> selectTargets(int player, Unit u);
	/**
	 * Checks if there are enough units, matching required criteria
	 * @return true, if spell associated with targeter can be casted.
	 */
	public boolean hasTargets(int player, Unit u);
}
