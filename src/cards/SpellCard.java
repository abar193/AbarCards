package cards;

import effects.AbstractEffect;
import effects.Targeter;

/**
 * Card used to cast spells. It has targeter, witch chooses to whom shall be effects applied 
 * and an array of effects to use. 
 * @author Abar
 *
 */
public class SpellCard extends BasicCard {

	public Targeter targeter;
	public AbstractEffect[] effect;
	
	public SpellCard(String name, String desc) {
		super(name, desc);
		this.type = CardType.Spell;
	}

	public SpellCard(String name, String desc, int cost, Targeter t, AbstractEffect ae) {
		super(name, desc, cost);
		this.type = CardType.Spell;
		targeter = t;
		this.effect = new AbstractEffect[1];
		effect[0] = ae;
	}

}
