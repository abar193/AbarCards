package effects;

import cards.UnitCard;
import units.Unit;
import units.UnitFactory;
import src.Game;
import src.FieldSituation;

public class CreateUnitSpell extends AbstractSpell {

	public UnitCard myUnit;
	String count;
	int side;
	
	public CreateUnitSpell(UnitCard myUnit, String count, int side) {
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
		int value = CustomValueDecoder.decodeValue(count, player, target);
		for(int i = 0; i < value; i++) {
			Game.currentGame.createUnit(myUnit, (player + side) % 2);
		}
		
	}

}
