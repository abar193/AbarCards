package effects;

import units.Unit;
import src.FieldSituation;
import src.Game;

/**
 * Targeters should be used with SpellCards to choose spells targets.
 * @author Abar
 *
 */
public interface Targeter {
	public Unit[] selectTargets(FieldSituation fs, Game g);
	public boolean hasTargets(FieldSituation fs);
}
