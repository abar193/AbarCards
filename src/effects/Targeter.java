package effects;

import java.util.ArrayList;

import units.FieldObject;
import units.UnitFilter;

/**
 * Targeters are used with TargedetSpells to choose targets. 
 * 
 * Targeters may require game.provideFieldSituation(), askPlayerForTarget() or field.getNeighbors()
 * @author Abar
 */
public interface Targeter {
	
	public void setFilter(UnitFilter f);
	/**
	 * Selects target units and returns them as array. 
	 * @param player player's number, who activated spell
	 * @param u source object
	 * @return array of targets
	 */
	public ArrayList<FieldObject> selectTargets(int player, FieldObject u, src.ProviderGameInterface currentGame);
	/**
	 * Checks if there are enough units, matching required criteria
	 * @return true, if spell associated with targeter can be casted.
	 */
	public boolean hasTargets(int player, FieldObject u, src.ProviderGameInterface currentGame);
}
