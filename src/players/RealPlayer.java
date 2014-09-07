package players;

import src.FieldSituation;
import src.Game;
import ui.VisualSystemInterface;
import ui.InputInterface;
import units.Unit;
import units.Unit.Quality;

import java.util.ArrayList;
import java.util.List;

import cards.*;

/**
 * Class for real player, who sits in front of his PC and plays that game (basically 
 * 		it's only me). Outputs information to console and recives input from there. 
 * @author Abar
 *
 */
public class RealPlayer implements PlayerInterface, InputInterface {

	private Game parent;
	
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
	public void setParentGame(Game g) {
		parent = g;
	}

	@Override
	public Unit selectTarget() {
		int p, u;
		System.out.print("Enter player to target (0 - you, 1 - he): ");
		try {
			p = System.in.read();
			while(p != '0' && p != '1') {
				p = System.in.read();
			}
		} catch(java.io.IOException e){
			p = '0'; 
		}
		p -= '0';
		System.out.print("Enter unit to target: ");
		try {
			u = 0;
			while(u < '0' || p > '9') {
				u = System.in.read();
				if(u - '0' >= latestSituation.allUnitFromOneSide((me.playerNumber + p)%2, false).size()) {
					System.out.println("Can't target that! #" + Integer.toString(u-'0'));
					u = 0;
				}
			}
			u -= 48;
		} catch(java.io.IOException e){
			u = 0; 
		}
		
		return latestSituation.allUnitFromOneSide((me.playerNumber + p) % 2, false).get(u);
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
		if(parent.attackIsValid(u1, u2, me.playerNumber, opponent.playerNumber)) {
			parent.commitAttack(u1, u2, me.playerNumber, opponent.playerNumber);
		} else {
			reciveAction("Invalid target");
		}
		
	}

}
