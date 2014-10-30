/**
 * This package represents front end of the game.
 * <p>
 * Instances of PlayerInterface are classes, who mey play the game - either bots, or real player. 
 * {@link players.PlayerData} class stores all information about player, and is to be shown for player only.
 * {@link players.PlayerOpenData} class stores only basic information (e.g. health, mana, card's count), 
 * 		and may be shown for anybody.
 * Each player (real, AI, or someone from internet) should be implementation of {@link players.PlayerInterface}.
 */
package players;

