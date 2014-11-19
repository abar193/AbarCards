package effects;

import java.util.ArrayList;

import units.FieldObject;
import units.Unit;
import units.UnitFilter;
import src.Game;
import src.FieldSituation;

public class NeighborTargeter implements Targeter {

	int offset;
	public NeighborTargeter(int offset) {
		this.offset = offset; 
	}

	@Override
	public ArrayList<FieldObject> selectTargets(int player, FieldObject u, 
	        src.ProviderGameInterface currentGame) 
	{
	    if(!(u instanceof Unit)) return new ArrayList<FieldObject>(1);
	    FieldObject n = currentGame.provideFieldSituation().neighborUnit(offset, (Unit)u);
		if(n == null) 
			return null;
		ArrayList<FieldObject> arr = new ArrayList<>(1);
		arr.add(n);
		return arr;
	}

	@Override
	public boolean hasTargets(int player, FieldObject u, src.ProviderGameInterface currentGame) {
	    if(!(u instanceof Unit)) return false;
		return currentGame.provideFieldSituation().neighborUnit(offset, (Unit)u) != null;
	}
	
	@Override
	public void setFilter(UnitFilter f) {
		System.out.println("Not implemented!");
	}

	public String toString() {    
        return "NeighborTargeter"; 
    }

}
