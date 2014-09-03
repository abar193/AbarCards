package players;

import src.FieldSituation;

import src.Game;
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
	 * Sets reference to an actual Game.
	 * @param g Game
	 */
	public void setParentGame(Game g);
	
	/**
	 * Here should player make his decisions and call methods like playCard or commitAttack on Game. 
	 */
	public void run();
	
	public Unit selectTarget();
	
	public ui.VisualSystemInterface visual();
}
