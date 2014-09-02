package cards;

import effects.AbstractSpell;
import effects.Targeter;

/**
 * Card used to cast spells. Spells are subclasses of AbstractSpell  
 * @author Abar
 *
 */
public class SpellCard extends BasicCard {

	public AbstractSpell spell;
	
	public SpellCard(String name, String desc) {
		super(name, desc);
		this.type = CardType.Spell;
	}

	public SpellCard(String name, String desc, int cost, AbstractSpell as) {
		super(name, desc, cost);
		this.type = CardType.Spell;
		this.spell = as;
	}
	
	public boolean validate(int player) {
		return spell.validate(player);
	}
	
	public void exequte(int player) {
		spell.exequte(player);
	}
}
