package ui;

import cards.SpellCard;

/**
 * Interface for all possible player-side inputs
 * @author Abar
 *
 */
public interface InputInterface {
	public void playSpellCard(SpellCard sc);
	public void playUnitCard(int uc);
	public void playUnitCardAt(int uc, int i);
	public void makeUnitsAttack(int u1, int u2);
}
