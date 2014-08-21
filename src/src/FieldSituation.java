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
		if((player == 0 || player == 1) && unit >= 0 && player >= 0)
			return playerUnits.get(player).size() > unit;
		else 
			return false;
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
	
	public boolean containsOnDifferentSides(Unit u1, Unit u2) {
		return playerUnits.get(u1.myPlayer).contains(u1) &&
				playerUnits.get(u2.myPlayer).contains(u2) &&
				u1.myPlayer != u2.myPlayer;
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
	 * Returns unit from the same arrayList with u, who's position = u.index + i.
	 * @param i offset from source unit
	 * @param u source unit
	 * @return unit or null, if u.index + i is outside array bounds
	 */
	public Unit neighborUnit(int i, Unit u) {
		ArrayList<Unit> units = (playerUnits.get(0).contains(u)) ? 
				playerUnits.get(0) : playerUnits.get(1);
		
		for(int j = 0; j < units.size(); j++) {
			if(units.get(j).equals(u)) {
				int r = j + i;
				if(r < 0 || r >= units.size()) {
					return null;
				} else {
					return units.get(r);
				}
			}
		}
		return null;
	}
	
	/**
	 * Removes unit u of player p.
	 * @return true if unit has been found and removed
	 */
	public boolean removeUnitOfPlayer(Unit u, int p) {
		return playerUnits.get(p).remove(u);
	}
}
