package effects;

import java.util.ArrayList;

import units.FieldObject;
import units.UnitFilter;

public class SelfTargeter implements Targeter {

	public SelfTargeter() {
	}

	@Override
	public ArrayList<FieldObject> selectTargets(int player, FieldObject u, src.ProviderGameInterface currentGame) 
	{
		if(u == null) return null;
		ArrayList<FieldObject> arr = new ArrayList<FieldObject>(1);
		arr.add(u);
		return arr;
	}

	@Override
	public boolean hasTargets(int player, FieldObject u, src.ProviderGameInterface currentGame) {
		return u != null;
	}

	@Override
	public void setFilter(UnitFilter f) {
		System.out.println("Really?");
	}

}
