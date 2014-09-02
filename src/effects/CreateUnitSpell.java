package effects;

import cards.UnitCard;
import units.Unit;
import units.UnitFactory;
import src.Game;
import src.FieldSituation;

public class CreateUnitSpell extends AbstractSpell {

	public UnitCard myUnit;
	int count;
	int side;
	
	public CreateUnitSpell(UnitCard myUnit, int count, int side) {
		this.myUnit = myUnit;
		this.count = count;
		this.side = side;
	}

	@Override
	public boolean validate(int player) {
		return true;
	}

	@Override
	public void exequte(int player) {
		FieldSituation fs = Game.currentGame.provideFieldSituation();
		for(int i = 0; i < count; i++) {
			Unit u = Game.currentGame.factory.createUnit(myUnit, (player + side) % 2);
			if(fs.canUnitBeAdded(u, (player + side) % 2)) {
				fs.addUnit(u, (player + side) % 2);
			}
		}
		
	}

}
