package players;

import src.FieldSituation;
import src.Game;
import ui.VisualSystemInterface;
import units.Unit;

/**
 * Just simple bot, doing nothing
 * @author Abar
 */
public class PassiveBot implements PlayerInterface {

	@Override
	public void reciveInfo(PlayerData yourData, FieldSituation field,
			PlayerOpenData enemyData) {
		
	}

	@Override
	public void reciveAction(String m) {

	}

	@Override
	public void setParentGame(Game g) {

	}

	@Override
	public void run() {

	}

	@Override
	public Unit selectTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VisualSystemInterface visual() {
		// TODO Auto-generated method stub
		return null;
	}

}
