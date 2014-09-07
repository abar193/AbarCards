package ui;

import cards.SpellCard;

public interface InputInterface {
	public void playSpellCard(SpellCard sc);
	public void playUnitCard(int uc);
	public void playUnitCardAt(int uc, int i);
	public void makeUnitsAttack(int u1, int u2);
}
