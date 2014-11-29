package cards;

/**
 * Type, used to describe if card is a SpellCard, UnitCard or BaseCard or their successor. 
 * @author Abar
 *
 */
public enum CardType {
	Spell, Unit, Building, Developer, Energy;
	
	public String toString() {
		switch(this) {
			case Spell: {
				return "Sc";
			}
			case Unit: {
				return "Uc";
			}
			case Building: {
				return "Bc";
			}
			case Energy: {
			    return "En";
			}
			default: return "";
		}
	}
}
