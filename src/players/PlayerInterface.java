package players;

import src.FieldSituation;
import src.GameInterface;
import units.Unit;

/**
 * Basic interface for Player.
 * @author Abar
 */
public interface PlayerInterface extends Runnable {
	/**
	 * Updates info with actual data - called on beginning of the turn, and each
	 * time a card is played.
	 * @param yourData data of the player
	 * @param field field with units
	 * @param enemyData data with player opoonent's information
	 */
	public void reciveInfo(PlayerData yourData, FieldSituation field, PlayerOpenData enemyData);
	
	/**
	 * Recives message from game, like "You go first" or "Opponent is playing card"
	 * @param m message
	 */
	public void reciveAction(String m);
	
	/**
	 * Sets reference to an the parent GameInterface.
	 * @param g Game
	 */
	public void setParentGameInterface(GameInterface g);
	
	/**
	 * Here should player make his decisions and call methods like playCard or commitAttack on Game. 
	 */
	public void run();
	
	/**
	 * Called by game - asks player to provide unit target
	 * 
	 * @return selected unit
	 */
	public Unit selectTarget();
	
	/**
	 * Called by game: provide visual system to display some information, or error message
	 * @return players' VS.
	 */
	public ui.VisualSystemInterface visual();
}
