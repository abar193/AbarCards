package effects;

import cards.UnitCard;
import units.Unit;
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
	public boolean validate(int player, src.ProviderGameInterface currentGame) {
		return !required || currentGame.provideFieldSituation()
		        .isSpaceAvailable(player, myUnit instanceof cards.BuildingCard);
	}

	@Override
	public void exequte(int player, src.ProviderGameInterface currentGame) {
		int value = CustomValueDecoder.decodeValue(count, player, target, currentGame);
		for(int i = 0; i < value; i++) {
			Unit u = (Unit)currentGame.createObject(myUnit, (player + side) % 2);
		}
		
	}
	
	public String toString() {    
        return "(CreateUnitSpell " + myUnit.toString() + ") "; 
    }

}
