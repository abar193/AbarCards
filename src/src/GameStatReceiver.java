package src;

/**
 * Class for collecting game statistics.
 * @author Abar
 */
public interface GameStatReceiver {
    public void gameEnded(Game g, int winner);
}
