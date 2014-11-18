package players;

import src.FieldSituation;
import src.GameInterface;
import ui.VisualSystemInterface;
import ui.InputInterface;
import units.Unit;

import cards.*;

/**
 * Class for real player, who sits in front of his PC and plays that game (basically 
 * 		it's only me). Outputs information to console and recives input from there. 
 * @author Abar
 *
 */
public class RealPlayer implements PlayerInterface, InputInterface {
	
	private GameInterface parent;
	
	private FieldSituation latestSituation;
	private PlayerData me;
	private PlayerOpenData opponent;
	private boolean inProgress;
	
	public VisualSystemInterface visual;
	
	public RealPlayer(VisualSystemInterface vs) {
		visual = vs;
	}
	
	@Override
	public void reciveInfo(PlayerData you, FieldSituation field, PlayerOpenData enemy) {
		me = you;
		latestSituation = field;
		opponent = enemy;
		
		visual.setInputInterface(this);
		visual.displayFieldState(you, field, enemy);
	}
	
	/**
	 * Prints message to console.
	 */
	@Override
	public void reciveAction(String m) {
		visual.displayMessage(m);
	}

	
	@Override
	public void run() {
		visual.read();
	}

	@Override
	public void setParentGameInterface(GameInterface g) {
		parent = g;
	}

	@Override
	public Unit selectTarget() {
		return visual.provideUnit();
	}

	@Override
	public VisualSystemInterface visual() {
		return visual;
	}

	@Override
	public void playSpellCard(SpellCard sc) {
		return;
	}

	@Override
	public void playUnitCard(int uc) {
		if(me.getHand().size() > uc) {
			if(parent.canPlayCard(me.getHand().get(uc), me.playerNumber)) {
				parent.playCard(me.getHand().get(uc), me.playerNumber);
			} else {
				reciveAction("Can't play that");
			}
		}
	}

	@Override
	public void playUnitCardAt(int uc, int i) {
		//throw new Exception();
		return;
	}

	@Override
	public void makeUnitsAttack(int u1, int u2) {
		if(parent.attackIsValid(u1, u2, me.playerNumber)) {
			parent.commitAttack(u1, u2, me.playerNumber);
		} else {
			reciveAction("Invalid target");
		}
		
	}

}
