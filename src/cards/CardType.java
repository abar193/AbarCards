package cards;

/**
 * Type, used to describe if card is a SpellCard, UnitCard or BaseCard or their successor. 
 * @author Abar
 *
 */
public enum CardType {
	Spell, Unit, Basecard;
	
	public String stringValue() {
		switch(this) {
			case Spell: {
				return "Sp";
			}
			case Unit: {
				return "Un";
			}
			case Basecard: {
				return "Bc";
			}
			default: return "";
		}
	}
}
