package effects;

import units.Unit;

import src.Game;

public class PlayerValueSpell extends AbstractSpell {
	
	public PlayerValueModifier modifier;
	public int value;
	public int filter;
	
	public PlayerValueSpell(Unit t, int p, PlayerValueModifier mod, int value, int filter) {
		super(t, p);
		this.value = value;
		this.modifier = mod;
		this.filter = filter;
	}
	
	public PlayerValueSpell(String type, int value, int filter) {
		super(null, 0);
		modifier = PlayerValueModifier.fromString(type);
		this.value = value;
		this.filter = filter;
	}
	
	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public void exequte() {
		Game.currentGame.applySpellToPlayer((playerNum + filter) % 2, this);
	}

}
