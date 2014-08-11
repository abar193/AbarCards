package players;

import units.Unit;

/**
 * Aura - special modifier, associated with unit, or lasting duration turns, which 
 * may change costs of players units, or their. 
 * @author Abar
 *
 */
public class AuraEffect {
	/** Type of effect */
	public AuraType type;
	/** How much should this modification take */ 
	public int value;
	/** Associated unit - if null, then it's duration-based */
	public Unit unit;
	/** How many turns should modifier last */
	public int duration;
	
	/** Creates unit-based aura */
	public AuraEffect(AuraType type, int value, Unit u) {
		this.duration = -1;
		this.unit = u;
		this.type = type;
		this.value = value;
	}

	/** Creates duration-based aura */
	public AuraEffect(AuraType type, int value, int duration) {
		this.duration = duration;
		this.unit = null;
		this.type = type;
		this.value = value;
	}
	
	/**
	 * Checks if this is duration-based aura, in that case reduces duration by 1 
	 * and returns true if duration <= 0.
	 * Has no effect, if used on unit-based aura.
	 */
	public boolean shouldBeRemovedIfTurnBased() {
		if(unit == null) {
			 if(--duration <= 0) return true;
		}
		return false;
	}

}
