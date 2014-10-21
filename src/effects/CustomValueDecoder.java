package effects;

import src.Game;
import src.FieldSituation;

import units.Unit;

/**
 * Class for decoding custom values like "EnemyUnitsCount" to use in Buffs and 
 * other spells.
 * @author Abar
 */
public class CustomValueDecoder {

	public static int decodeValue(String value, int player, Unit target, 
	        src.ProviderGameInterface currentGame) 
	{
		try {
			int i = Integer.parseInt(value);
			return i;
		} catch (NumberFormatException e) {
			FieldSituation fs = currentGame.provideFieldSituation();
			switch(value) {
				case "EnemyUnitsCount":
				case "EnemyUnitCount":
					return fs.countUnitsForSide((player + 1) % 2, false);
				case "AllyUnitsCount":
				case "AllyUnitCount":
					return fs.countUnitsForSide(player, false);
				case "AllyUnitsCount-1":
				case "AllyUnitCount-1":
					return fs.countUnitsForSide(player, false) - 1;
				case "UnitHealth":
				    return target.getCurrentDamage();
				default:
					System.out.println("!Unknown value " + value);
					return 0;
			}
		}
	}
}
