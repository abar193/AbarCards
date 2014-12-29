package units;

import cards.*;
import effects.AuraEffect;
import src.Game;

/** 
 * Class used to create units from cards, and apply all the needed buffs.
 * Calls addAuraForPlayer of the current game as well, if the unit posesses aura.
 * @author Abar
 *
 */
public class UnitFactory {

	public UnitFactory() {
		
	}
	
	private void configureByCard(FieldObject o, UnitCard uc, src.ProviderGameInterface cG) {
	    if(uc.qualities != 0) {
            o.appyQualities(uc.qualities);
        }
        
        if(uc.auraEffects != null) {
            for(AuraEffect ae : uc.auraEffects) {
                AuraEffect e = new AuraEffect(ae.type, ae.value, o);
                cG.addAuraForPlayer(o.player, e);
            }
        }
        
        if(uc.power != null) {
            o.addPower(uc.power);
        }
	}
	
	/**
	 * Creates and returns unit represented by the card.
	 * @param card card - UnitCard or it's descendant
	 * @param player player's number - 0/1, used to call addAuraForPlayer
	 * @return unit created
	 */
	public Unit createUnit(UnitCard card, int player, src.ProviderGameInterface cG) {
		Unit u;
		if(card instanceof cards.SpecialUnitCard) {
			switch(((SpecialUnitCard)card).specialUnitRef) {
				case DmgEqHealth:
					u = new UnitAttackEqToDmg(card, player, cG);
					break;
				case Other:
				default: 
					u = new Unit(card, player, cG);
			}
		} else {
			u = new Unit(card, player, cG);
		}
		
		configureByCard(u, card, cG);
		
		return u;
	}
	
	/**
     * Creates and returns building represented by the card.
     * @param card BuildingCard representing building
     * @param player player's number
     * @return unit created
     */
    public Building createBuilding(BuildingCard card, int player, src.ProviderGameInterface cG) {
        Building b;
        b = new Building(card, player, cG);
        
        configureByCard(b, card, cG);
        
        return b;
    }

}
