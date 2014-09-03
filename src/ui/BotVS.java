package ui;

import players.PlayerData;
import players.PlayerOpenData;
import src.FieldSituation;
import units.TriggeringCondition;
import units.Unit;
import cards.SpellCard;

public class BotVS implements VisualSystemInterface {

	public BotVS() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void displayError(String m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayMessage(String m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayFieldState(PlayerData p1, FieldSituation field,
			PlayerOpenData p2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayAttack(Unit u1, Unit u2, boolean died1, boolean died2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayPower(Unit u, TriggeringCondition e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispaySpell(SpellCard s, int player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayUnitDamage(Unit u, int damage) {
		// TODO Auto-generated method stub

	}

}
