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
    /** Creates and places on field unit, performing all checks, and 
     * triggering spawn-relevant events
     * @param uc unit card for unit
     * @param player player's number
     * @return null if nothing was placed, or created unit 
     */
    public FieldObject createObject(BasicCard uc, int player);
    /**
     * Places FieldObject on field for specific player, perfrorming all checks and triggering 
     * spawn-relevant events.
     * @param o created object
     * @param player player side
     * @return object, or null if object can't be added
     */
    public FieldObject addObject(FieldObject o, int player);
    public FieldObject askPlayerForTarget(int player);
    public void applySpellToPlayer(int player, AbstractSpell spell);
    public void informAll(String m);
    public void addAuraForPlayer(int player, AuraEffect ae);
    public void informLostCards(ArrayList<BasicCard> cards, int player);
    public void triggerUnitEvents(FieldObject u, TriggeringCondition.Condition c);
    public int getResourceForPlayer(int player, boolean energy);
    public boolean drainResourceForPlayer(int player, boolean energy, int value);
    public void removeObjectAura(FieldObject o);
}
