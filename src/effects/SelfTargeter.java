package effects;

import java.util.ArrayList;

import units.Unit;

public class SelfTargeter implements Targeter {

	public SelfTargeter() {
	}

	@Override
	public ArrayList<Unit> selectTargets(int player, Unit u) {
		if(u == null) return null;
		ArrayList<Unit> arr = new ArrayList<Unit>(1);
		arr.add(u);
		return arr;
	}

	@Override
	public boolean hasTargets(int player, Unit u) {
		return u != null;
	}

}
