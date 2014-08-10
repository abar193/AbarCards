package effects;

import units.Unit;
import src.FieldSituation;

/**
 * Targeters should be used with SpellCards to choose spells targets.
 * @author Abar
 *
 */
public interface Targeter {
	public Unit[] selectTargets(FieldSituation fs, int p);
	public boolean hasTargets(FieldSituation fs, int player);
}
