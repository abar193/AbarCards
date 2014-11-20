package src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import cards.BasicCard;
import units.Building;
import units.FieldObject;
import units.PlayerUnit;
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
	private ArrayList<ArrayList<FieldObject>> playerBuildings;
	private ArrayList<Unit> heroes;
	
	private src.ProviderGameInterface currentGame;
	
	public FieldSituation(src.ProviderGameInterface cG) {
		playerUnits = new ArrayList<ArrayList<Unit>>(2){{
			add(new ArrayList<Unit>(MAXFIELDUNITS));
			add(new ArrayList<Unit>(MAXFIELDUNITS));
		}};
		
		playerBuildings = new ArrayList<ArrayList<FieldObject>>(2){{
			add(new ArrayList<FieldObject>(MAXFIELDUNITS));
			add(new ArrayList<FieldObject>(MAXFIELDUNITS));
		}};
		
		heroes = new ArrayList<Unit>(2);
		currentGame = cG;
	}
	
	public void addHeroForSide(int s, Unit u) {
	    heroes.add(s, u);
	    playerBuildings.get(s).add(u);
	}
	
	public boolean heroDead(int s) {
	    return heroes.get(s).isDead();
	}

	@SuppressWarnings("unchecked")
    public Map toMap() {
		Map m = new LinkedHashMap();
		for(int j = 0; j < 2; j++) {
			JSONArray jarr = new JSONArray();
			Iterator<Unit> i = playerUnits.get(j).iterator();
			while(i.hasNext()) {
				jarr.add(i.next().toMap());
			}
			// TODO rework
			m.put(Integer.toString(j), jarr);
			m.put("Hero" + Integer.toString(j), (heroes.get(j).toMap()));
		}
		return m;
	}
	
	@SuppressWarnings("unchecked")
	public FieldSituation(Map m, ProviderGameInterface cG) {
		
		playerUnits = new ArrayList<ArrayList<Unit>>(2);
		heroes = new ArrayList<Unit>(2);
		for(int j = 0; j < 2; j++) {
			JSONArray jarr;
			try { 
				jarr = (JSONArray) m.get(Integer.toString(j));
			} catch(ClassCastException e) {
				jarr = (JSONArray) JSONValue.parse((String) m.get(Integer.toString(j)));
			}
			
			ArrayList<Unit> arr = new ArrayList<Unit>(jarr.size());
			playerUnits.add(arr);
			java.util.Iterator<Map> i = jarr.iterator();
			while(i.hasNext()) {
				arr.add(new Unit(i.next(), cG));
			}
			Unit h;
			try { 
				h = new Unit((Map)m.get("Hero" + Integer.toString(j)), cG);
			} catch(ClassCastException e) {
				h = new Unit((Map)JSONValue.parse((String) m.get("Hero" + Integer.toString(j))), cG);
			}
			
			heroes.add(h);
		}	
	}
	
	/**
	 * Checks if unit can be added by player p.
	 * @param u unit to be added
	 * @param p player's number
	 * @return true if unit can be added
	 */
	public boolean canObjectBeAdded(FieldObject u, int p) {
	    if(u instanceof Unit) {
    		if(playerUnits.get(p).size() < MAXFIELDUNITS) 
    			return true;
    		else
    			return false;
	    } else if(u instanceof Building) {
	        if(playerBuildings.get(p).size() < MAXFIELDUNITS) 
                return true;
            else
                return false;
	    }
	    return false;
	}
	
	/**
     * Checks if there is enough space to place new unit/building. 
     * @param side side of a player
     * @param building is it building?
     * @return true if 
     */
	public boolean isSpaceAvailable(int side, boolean building) {
	    if(building) return playerBuildings.get(side).size() < MAXFIELDUNITS;
	    else return playerUnits.get(side).size() < MAXFIELDUNITS;
	}
	
	/**
	 * Returns unit's position on field. Used by netGame + testing framework.
	 * @param u unit to be found
	 * @return unit's position, or -1 if that unit does not belongs to u.myPlayer side 
	 */
	public int objectPosition(FieldObject u) {
		int i = 0;
		if(u instanceof Building || u instanceof PlayerUnit) {
		    i = -1;
		    for(FieldObject o : this.allBuildingsFromOneSide(u.player)) {
                if(o.equals(u)) return i;
                else i--;
            }
		} else {
    		for(FieldObject o : this.allObjectsFromOneSide(u.player, false)) {
    			if(o.equals(u)) return i;
    			else i++;
    		}
		}
		return -100;
	}
	
	/**
	 * Adds unit if it's allowed by canUnitBeAdded, throws IllegalArgumentException otherwise.
	 * Does not work for PlayerUnits!
	 * @param u unit to be added
	 * @param p player's number
	 */
	public void addObject(FieldObject u, int p) {
		if(canObjectBeAdded(u, p)) {
		    if(u instanceof Unit) {
    			playerUnits.get(p).add((Unit)u);
    			return;
		    } else if(u instanceof Building) {
		        playerBuildings.get(p).add(u);
                return;
		    }
		}
		
		throw new IllegalArgumentException();
	}
	
	/**
	 * Checks if unit exists for player
	 */
	public boolean objectExist(int unit, int player) {
		if(unit < 0) {
		    return playerBuildings.get(player).size() > (Math.abs(unit) - 1); 
		} else {
		    return playerUnits.get(player).size() > unit;
		}
	}
	
	/** Returns iterator for side. */
	public Iterator<FieldObject> provideIteratorForSide(int player, boolean includeBuildings) {
		return allObjectsFromOneSide(player, includeBuildings).iterator();
	}
	
	/**
	 * Gets unit for player (if that exists).
	 * @param u positive value starting from 0 for unit, or negative for building. First building has 
	 * number -1, second: -2, e.t.c.
	 * @return unit or null, if that does not exists.
	 */
	public FieldObject objectForPlayer(int u, int p) {
		if(u < 0) {
		    return playerBuildings.get(p).get(Math.abs(u) - 1);
		} else {
		    return playerUnits.get(p).get(u);
		}
	}
	
	/** Returns all unit for both players  */
	public ArrayList<FieldObject> allObjects(boolean includeBuildings) {
		ArrayList<FieldObject> al = new ArrayList<FieldObject>(playerUnits.get(0));
		al.addAll(playerUnits.get(1));
		if(includeBuildings) {
			al.addAll(playerBuildings.get(0));
			al.addAll(playerBuildings.get(1));
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
	public int countObjectsMatchingFilter(int player, UnitFilter f, boolean includeBuildings) {
		return getObjectsMatchingFilter(player, f, includeBuildings).size();
	}
	
	/**
	 * Gets units matching specific filter
	 * @param player -1 for both players, or 0-1 for player number
	 * @param f filter to be used
	 * @param includeHero should heroes be included?
	 * @return list of units matching filter
	 */
	public ArrayList<FieldObject> getObjectsMatchingFilter(int player, UnitFilter f, boolean includeBuildings) {
		ArrayList<FieldObject> units = (player == -1) ? 
				allObjects(includeBuildings) : allObjectsFromOneSide(player, includeBuildings);
		Iterator<FieldObject> i = units.iterator();
		while(i.hasNext()) {
			FieldObject o = i.next();
			if(!o.matchesFilter(f)) i.remove();
		}
		return units;
	}
	
	public int countObjectsForSide(int side, boolean includeBuildings) {
		if(side == -1) {
			return allObjects(includeBuildings).size();
		} else {
			return allObjectsFromOneSide(side, includeBuildings).size();
		}
	}
	
	/**
	 * Returns all units from one players side */
	public ArrayList<FieldObject> allObjectsFromOneSide(int player, boolean includeBuildings) {
		ArrayList<FieldObject> al = new ArrayList<FieldObject>(playerUnits.get(player));
		if(includeBuildings)
				al.addAll(playerBuildings.get(player));
		return al;
	}
	
	public ArrayList<FieldObject> allBuildingsFromOneSide(int player) {
	    return playerBuildings.get(player);
	}
	
	public boolean containsOnDifferentSides(Unit u1, FieldObject o2) {
		return playerUnits.get(u1.player).contains(u1) &&
				allObjectsFromOneSide(o2.player, true).contains(o2) &&
				u1.player != o2.player;
	}
	
	/**
	 * Return amount of units with "taunt" quality for specific player.
	 * @param p player number (0-1)
	 * @return count of those units
	 */
	public int tauntObjectsForPlayerCount(int p) {
		int r = 0;
		for(FieldObject o : allObjectsFromOneSide(p, true)) {
			if((o.hasQuality(units.Quality.Taunt)) && (!o.hasQuality(units.Quality.Stealth))) 
				r++;
		}
		return r;
	}
	
	/**
     * Return taunt units for specific player.
     * @param p player number (0-1)
     * @return count of those units
     */
    public ArrayList<FieldObject> tauntObjectsForPlayer(int p) {
        ArrayList<FieldObject> res = allObjectsFromOneSide(p, true);
        Iterator<FieldObject> i = res.iterator();
        while(i.hasNext()) {
            FieldObject o = i.next();
            if(!((o.hasQuality(units.Quality.Taunt)) && (!o.hasQuality(units.Quality.Stealth)))) 
                i.remove();
        }
        return res;
    }
	
	/**
	 * Calls endTurn() for all objects on field.
	 */
	public void refreshObjects() {
		for(FieldObject u : allObjects(true)) {
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
	public void passEventAboutObject(FieldObject u, TriggeringCondition e) {
		ArrayList<FieldObject> side = allObjectsFromOneSide(u.player, true);
		
		if(side != null) {
			for(FieldObject i : side) {
				if(!i.equals(u)) {
					i.respondToEvent(e, u);
				}
			}
		} else {
		    System.err.println("No side found for unit: " + u.toString());
		}
	}
	
	public void passEventAboutRemovedObjectFromSide(FieldObject u, TriggeringCondition e) {
		ArrayList<FieldObject> army = allObjectsFromOneSide(u.player, true);
		
		if(army != null) {
			for(FieldObject i : army) {
				i.respondToEvent(e, u);	
			}
		}
	} 
	
	/**
	 * Removes unit u of player p.
	 * @return true if unit has been found and removed
	 */
	public boolean removeObjectOfPlayer(FieldObject u, int p) {
	    if(u instanceof PlayerUnit) {
	        playerBuildings.get(p).remove(u);
	        return heroes.remove(u);
	    } else if(u instanceof Building) {
	        return playerBuildings.remove(u);
	    } else {
	        return playerUnits.get(p).remove(u);
	    }
	}
}
