package effects;

import units.Unit;

public class SelfTargeter implements Targeter {

	public SelfTargeter() {
	}

	@Override
	public Unit[] selectTargets(int player, Unit u) {
		if(u == null) return null;
		Unit[] un = new Unit[1];
		un[0] = u;
		return un;
	}

	@Override
	public boolean hasTargets(int player, Unit u) {
		return u != null;
	}

}
