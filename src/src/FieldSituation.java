package src;

import java.util.ArrayList;
import java.util.Iterator;

import units.TriggeringCondition;
import units.Unit;
import units.UnitFilter;

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
	public static final int MAXFIELDUNITS = 8; 
	
	private ArrayList<ArrayList<Unit>> playerUnits;
	private ArrayList<ArrayList<Unit>> playerBuildings;
	public ArrayList<Unit> heroes;
	
	public FieldSituation() {
		playerUnits = new ArrayList<ArrayList<Unit>>(2){{
			add(new ArrayList<Unit>(MAXFIELDUNITS));
			add(new ArrayList<Unit>(MAXFIELDUNITS));
		}};
		
		playerBuildings = new ArrayList<ArrayList<Unit>>(2){{
			add(new ArrayList<Unit>(MAXFIELDUNITS));
			add(new ArrayList<Unit>(MAXFIELDUNITS));
		}};
		
		heroes = new ArrayList<Unit>(2);
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
		if(unit == -1) return true;
		if((player == 0 || player == 1) && unit >= 0 && player >= 0)
			return playerUnits.get(player).size() > unit;
		else 
			return false;
	}
	
	/** Returns playerUnits.get(player).iterator() */
	public Iterator<Unit> provideIteratorForSide(int player) {
		return playerUnits.get(player).iterator();
	}
	
	/**
	 * Gets unit for player (if that exists).
	 * @param u unit number, or -1 for hero
	 * @return unit or null, if that does not exists.
	 */
	public Unit unitForPlayer(int u, int p) {
		if(u == -1) {
			return heroes.get(p);
		}
		if(unitExist(u, p)) 
			return playerUnits.get(p).get(u);
		return null;
	}
	
	/** Returns all unit for both players  */
	public ArrayList<Unit> allUnits(boolean includeHeroes) {
		ArrayList<Unit> al = new ArrayList<Unit>(playerUnits.get(0));
		al.addAll(playerUnits.get(1));
		if(includeHeroes) {
			al.addAll(heroes);
		}
		return al;
	}
	
	/** 
	 * Counts units matching specific filter
	 * @param player -1 for both players, or 0-1 for player number
	 * @param f filter to be used
	 * @param includeHero should heroes be included?
	 * @return count of matching units
	 */
	public int countUnitsMatchingFilter(int player, UnitFilter f, boolean includeHero) {
		int matches = 0;
		ArrayList<Unit> units = (player == -1) ? allUnits(includeHero) : allUnitFromOneSide(player, includeHero);
		for(Unit u : units) {
			if(u.matchesFilter(f)) matches++;
		}
		return matches;
	}
	
	/**
	 * Gets units matching specific filter
	 * @param player -1 for both players, or 0-1 for player number
	 * @param f filter to be used
	 * @param includeHero should heroes be included?
	 * @return list of units matching filter
	 */
	public ArrayList<Unit> getUnitsMatchingFilter(int player, UnitFilter f, boolean includeHero) {
		ArrayList<Unit> units = (player == -1) ? 
				allUnits(includeHero) : allUnitFromOneSide(player, includeHero);
		Iterator<Unit> i = units.iterator();
		while(i.hasNext()) {
			Unit u = i.next();
			if(!u.matchesFilter(f)) i.remove();
		}
		return units;
	}
	
	public int countUnitsForSide(int side, boolean includeHero) {
		if(side == -1) {
			return playerUnits.get(0).size() + playerUnits.get(1).size() 
					+ ((includeHero) ? 2 : 0); 
		} else {
			return playerUnits.get(side).size() + ((includeHero) ? 1 : 0);
		}
	}
	
	/**
	 * Returns all units from one players side */
	public ArrayList<Unit> allUnitFromOneSide(int player, boolean includeHero) {
		ArrayList<Unit> al = new ArrayList<Unit>(playerUnits.get(player));
		if(includeHero)
			al.add(heroes.get(player));
		return al;
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
			if((u.hasQuality(units.Unit.Quality.Taunt)) && (!u.hasQuality(units.Unit.Quality.Stealth))) 
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
	 * Passes event for all unit of u's side, except u himself.
	 */
	public void passEventAboutUnit(Unit u, TriggeringCondition e) {
		ArrayList<Unit> side = null;
		if(playerUnits.get(0).contains(u)) 
			side = playerUnits.get(0);
		else if(playerUnits.get(1).contains(u))
			side = playerUnits.get(1);
		
		if(side != null) {
			for(Unit i : side) {
				if(!i.equals(u)) {
					i.respondToEvent(e, u);
				}
			}
		}
	}
	
	public void passEventAboutRemovedUnitFromSide(int side, Unit u, TriggeringCondition e) {
		ArrayList<Unit> army = playerUnits.get(side);
		
		if(army != null) {
			for(Unit i : army) {
				i.respondToEvent(e, u);	
			}
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
