package ui;

import cards.SpellCard;
import players.*;
import src.FieldSituation;
import units.TriggeringCondition;
import units.Unit;

public interface VisualSystemInterface {
	
	public void displayError(String m);
	public void displayMessage(String m);
	
	public void displayFieldState(PlayerData p1, FieldSituation field, PlayerOpenData p2);
	
	public void displayAttack(Unit u1, Unit u2, boolean died1, boolean died2);
	public void displayPower(Unit u, TriggeringCondition e);
	public void dispaySpell(SpellCard s, int player);
	public void displayUnitDamage(Unit u, int damage);
	
	public void setInputInterface(InputInterface i);
	public void read();
}
