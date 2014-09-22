package ui;

import cards.SpellCard;

/**
 * Interface for all possible player-side inputs.
 * To be honest: for now that interface declares more, than is currently used.
 * Maybe, I will have to remove something later.
 * @author Abar
 *
 */
public interface InputInterface {
	public void playSpellCard(SpellCard sc); // currently not in use
	/**
	 * Plays card from player's hand
	 * @param uc card's number in player's hand
	 */
	public void playUnitCard(int uc);
	public void playUnitCardAt(int uc, int i); // not in use
	/**
	 * Makes unit u1 on player's side attack unit u2 on enemy side
	 * @param u1
	 * @param u2
	 */
	public void makeUnitsAttack(int u1, int u2);
}
