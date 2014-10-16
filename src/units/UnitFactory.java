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
		
		if(card.qualities != 0) {
			u.appyQualities(card.qualities);
		}
		
		if(card.auraEffects != null) {
			for(AuraEffect ae : card.auraEffects) {
				ae.unit = u;
				cG.addAuraForPlayer(player, ae);
			}
		}
		
		if(card.power != null) {
			u.addPower(card.power);
		}
		
		return u;
	}

}
