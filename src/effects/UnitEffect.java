package effects;

import units.Unit;

/**
 * Effect changing some unit's properties.
 * @author Abar
 *
 */
public abstract class UnitEffect extends AbstractEffect {

	public UnitEffect() {
		super();
		this.type = EffectType.UnitEffect;
	}
	
	public abstract void applyEffect(Unit u);
}
