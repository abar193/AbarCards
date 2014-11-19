package effects;

import src.Game;
import src.FieldSituation;
import units.FieldObject;

/**
 * Class for decoding custom values like "EnemyUnitsCount" to use in Buffs and 
 * other spells.
 * @author Abar
 */
public class CustomValueDecoder {

	public static int decodeValue(String value, int player, FieldObject target, 
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
					return fs.countObjectsForSide((player + 1) % 2, false);
				case "EnemyObjectsCount":
				    return fs.countObjectsForSide((player + 1) % 2, true);
				case "AllyUnitsCount":
				case "AllyUnitCount":
					return fs.countObjectsForSide(player, false);
				case "AllyBuildingsCount":
				    return fs.countObjectsForSide(player, true);
				case "AllyUnitsCount-1":
				case "AllyUnitCount-1":
					return fs.countObjectsForSide(player, false) - 1;
				case "UnitHealth":
				    return target.getCurrentDamage();
				default:
					System.out.println("!Unknown value " + value);
					return 0;
			}
		}
	}
}
