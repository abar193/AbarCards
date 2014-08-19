package effects;

import units.Unit;

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
	public int playerNum;
	
	public AbstractSpell(Unit t, int p) {
		target = t;
		playerNum = p;
	}
	
	/**
	 * Returns true, if spell can be casted - has enought targets, e.t.c.
	 */
	public abstract boolean validate();
	
	/**
	 * Executes spell. 
	 */
	public abstract void exequte();

}
