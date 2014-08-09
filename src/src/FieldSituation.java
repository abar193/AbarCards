package src;

import java.util.ArrayList;

import units.Unit;

/**
 * Stores information about "field" with units and buildings.
 * This class also has methods for adding and removing cards, and some checks.
 * @author Abar
 *
 */
public class FieldSituation {
	/**
	 * Maximum amount of units allowed to stay on a side.
	 */
	public static final int MAXFIELDUNITS = 5; 
	
	public ArrayList<ArrayList<Unit>> playerUnits;
	public ArrayList<ArrayList<Unit>> playerBuildings;
	
	public FieldSituation() {
		playerUnits = new ArrayList<ArrayList<Unit>>(2){{
			add(new ArrayList<Unit>(MAXFIELDUNITS));
			add(new ArrayList<Unit>(MAXFIELDUNITS));
		}};
		
		playerBuildings = new ArrayList<ArrayList<Unit>>(2){{
			add(new ArrayList<Unit>(MAXFIELDUNITS));
			add(new ArrayList<Unit>(MAXFIELDUNITS));
		}};
	}
	
	/**
	 * Checks if unit can be added by player p.
	 * @param u unit to be added
	 * @param p player's number
	 * @return true if unit can be added
	 */
	public boolean canUnitBeAdded(Unit u, int p) {
		if(playerUnits.get(p).size() < MAXFIELDUNITS) 
			return true;
		else
			return false;
	}
	
	/**
	 * Adds unit if it's allowed by canUnitBeAdded, throws IllegalArgumentException otherwise.
	 * @param u unit to be added
	 * @param p player's number
	 */
	public void addUnit(Unit u, int p) {
		if(canUnitBeAdded(u, p)) {
			playerUnits.get(p).add(u);
			return;
		}
		
		throw new IllegalArgumentException();
	}
	
	/**
	 * Checks if unit exists for player
	 */
	public boolean unitExist(int unit, int player) {
		return playerUnits.get(player).size() > unit;
	}
	
	/**
	 * Gets unit for player (if that exists).
	 * @return unit or null, if that does not exists.
	 */
	public Unit unitForPlayer(int u, int p) {
		if(unitExist(u, p)) 
			return playerUnits.get(p).get(u);
		return null;
	}
	
	/**
	 * Return amount of units with "taunt" quality for specific player
	 * @param p player number (0-1)
	 * @return count of those units
	 */
	public int tauntUnitsForPlayer(int p) {
		int r = 0;
		for(Unit u : playerUnits.get(p)) {
			if(u.hasQuality(units.Unit.Quality.Taunt)) 
				r++;
		}
		return r;
	}
	
	/**
	 * Calls endTurn() for all units on field
	 */
	public void refreshUnits() {
		for(Unit u : playerUnits.get(0)) {
			u.endTurn();
		}
		for(Unit u : playerUnits.get(1)) {
			u.endTurn();
		}
	}
	
	/**
	 * Removes unit u of player p.
	 * @return true if unit has been found and removed
	 */
	public boolean removeUnitOfPlayer(Unit u, int p) {
		return playerUnits.get(p).remove(u);
	}
}
