package units;

import players.AuraEffect;
import cards.*;
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
	public Unit createUnit(UnitCard card, int player) {
		Unit u;
		if(card instanceof cards.SpecialUnitClass) {
			switch(((SpecialUnitClass)card).specialUnitRef) {
				case DmgEqHealth:
					u = new UnitAttackEqToDmg(card);
					break;
				case Other:
				default: 
					u = new Unit(card);
			}
		} else {
			u = new Unit(card);
		}
		
		if(card.qualities != 0) {
			u.appyQualities(card.qualities);
		}
		
		if(card.auraEffects != null) {
			for(AuraEffect ae : card.auraEffects) {
				ae.unit = u;
				Game.currentGame.addAuraForPlayer(player, ae);
			}
		}
		
		return u;
	}

}
