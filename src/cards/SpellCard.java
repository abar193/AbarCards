package cards;

import units.UnitFilter;
import effects.AbstractSpell;
import effects.Targeter;

/**
 * Card used to cast spells. Spells are subclasses of AbstractSpell  
 * @author Abar
 *
 */
public class SpellCard extends BasicCard {

	public AbstractSpell spell;
	private UnitFilter filter; 
	
	public SpellCard(String name, String desc) {
		super(name, desc);
		this.type = CardType.Spell;
	}

	public SpellCard(String name, String desc, int cost, AbstractSpell as) {
		super(name, desc, cost);
		this.type = CardType.Spell;
		this.spell = as;
	}
	
	public boolean validate(int player, src.ProviderGameInterface cG) {
		return spell.validate(player, cG);
	}
	
	public void exequte(int player, src.ProviderGameInterface cG) {
		spell.exequte(player, cG);
	}
	
	public void setFilter(UnitFilter f) {
	    this.filter = f;
	    System.err.println("Filter in spell " + this.name + " is not used!");
	}
	
	public String toString() {	   
	    return "Spell \"" + name + "\""; 
	}
}
