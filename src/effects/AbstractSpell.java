package effects;

import units.Unit;
import units.UnitFilter;

/**
 * Parent of all spell effects. Used in spells from cards, or 
 * for unit special "powers". 
 * 
 * When effect comes from card, target should be null, when from unit - target 
 * is the unit activated. Some examples for hierarchy should follow. 
 * @author Abar
 */
public abstract class AbstractSpell {

	public Unit target;
	int playerNum;
	private UnitFilter filter;
	
	public AbstractSpell() {
	}
	
	/**
	 * Returns true, if spell can be casted - has enought targets, e.t.c.
	 */
	public abstract boolean validate(int player);
	
	/**
	 * Executes spell. 
	 */
	public abstract void exequte(int player);
	
	public void setFilter(UnitFilter f) {
		filter = f;
	}

}
