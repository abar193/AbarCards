package spells.premade;

import src.ProviderGameInterface;
import units.Unit;
import units.UnitFactory;
import effects.AbstractSpell;

/**
 * Spell, that destroys building and creates "Machine"-class unit.  
 * @author Abar
 *
 */
public class RobotiseSpell extends AbstractSpell {

    public RobotiseSpell() {
        
    }

    @Override
    public boolean validate(int player, ProviderGameInterface currentGame) {
        return currentGame.provideFieldSituation().isSpaceAvailable(player, false) 
                && (currentGame.provideFieldSituation().countObjectsForSide(player, false) <
                        currentGame.provideFieldSituation().countObjectsForSide(player, true));
    }

    @Override
    public void exequte(int player, ProviderGameInterface currentGame) {
        if(target instanceof units.Building) {
            Unit u = new UnitFactory().createUnit(target.card, player, currentGame);
            u.card.cardClass = "Machine";
            target.applyBuff(new effects.Buff(effects.BuffType.ShadowDmg, 100));
            currentGame.addObject(u, player);
        }
    }

}
