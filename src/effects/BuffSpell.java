package effects;

import units.Unit;

public class BuffSpell extends AbstractSpell {

	public Buff buff;
	
	public BuffSpell(Unit t, int p, Buff b) {
		super(t, p);
		buff = b;
	}

	@Override
	public boolean validate() {
		return target != null;
	}

	@Override
	public void exequte() {
		target.applyBuff(buff);
	}

}
