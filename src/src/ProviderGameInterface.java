package src;

import java.util.ArrayList;

import units.TriggeringCondition;
import units.FieldObject;
import units.Unit;
import cards.BasicCard;
import cards.UnitCard;
import effects.AbstractSpell;
import effects.AuraEffect;

/**
 * Interface for inner use for all classes, that require current Game instance.
 * @author Abar
 *
 */
public interface ProviderGameInterface {

    public FieldSituation provideFieldSituation();
    public FieldObject createObject(BasicCard uc, int player, boolean fromSpell);
    public FieldObject askPlayerForTarget(int player);
    public void applySpellToPlayer(int player, AbstractSpell spell);
    public void informAll(String m);
    public void addAuraForPlayer(int player, AuraEffect ae);
    public void informLostCards(ArrayList<BasicCard> cards, int player);
    public void passEventAboutUnit(FieldObject u, TriggeringCondition e);
}
