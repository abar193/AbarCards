package src;

import units.Unit;
import units.FieldObject;
import cards.BasicCard;

public interface GameInterface {
	
	/** Asks game if card can be played by specific player */
	public boolean canPlayCard(BasicCard c, int player);
	
	/** Ask game to play card.  */
	public void playCard(BasicCard c, int player);
	
	/**
	 * Checks if unit attacker for player playerA can attack unit target of the opposing player
	 * @return true, if attack is possible
	 */
	public boolean attackIsValid(int attacker, int target, int playerA);
	
	/**
	 * Make two units attack each other
	 * @param attacker
	 * @param target
	 */
	public void commitAttack(Unit attacker, FieldObject target);
	
	/**
	 * If it is players turn, ends it. 
	 * @param player number of player who sends it
	 */
	public void endTurn(int player);
	
	/** Make two unit attack each other by unit's numbers */
	public void commitAttack(int a, int t, int pa);
	
	/**
	 * Called when player leaves current game either by disconnecting, or by 
	 * going to main menu.
	 * @param player
	 */
	public void playerQuits(int player);
}
