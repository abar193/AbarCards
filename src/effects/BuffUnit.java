package effects;

import units.Unit;

/**
 * Effect, applying specific buff to some unit.
 * @author Abar
 *
 */
public class BuffUnit extends UnitEffect {
	
	Buff b;
	public BuffUnit(Buff myBuff) {
		super();
		b = myBuff;
	}

	@Override
	public void applyEffect(Unit u) {
		u.applyBuff(b);
	}

}
