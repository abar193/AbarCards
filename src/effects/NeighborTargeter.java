package effects;

import java.util.ArrayList;

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
	public ArrayList<Unit> selectTargets(int player, Unit u, 
	        src.ProviderGameInterface currentGame) 
	{
		Unit n = currentGame.provideFieldSituation().neighborUnit(offset, u);
		if(n == null) 
			return null;
		ArrayList<Unit> arr = new ArrayList<>(1);
		arr.add(n);
		return arr;
	}

	@Override
	public boolean hasTargets(int player, Unit u, src.ProviderGameInterface currentGame) {
		return currentGame.provideFieldSituation().neighborUnit(offset, u) != null;
	}
	
	@Override
	public void setFilter(UnitFilter f) {
		System.out.println("Not implemented!");
	}

	public String toString() {    
        return "NeighborTargeter"; 
    }

}
