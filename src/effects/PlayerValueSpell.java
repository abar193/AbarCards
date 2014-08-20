package effects;

import src.Game;

public class PlayerValueSpell extends AbstractSpell {
	
	public PlayerValueModifier modifier;
	public int value;
	public int filter;
	
	public PlayerValueSpell(PlayerValueModifier mod, int value, int filter) {
		this.value = value;
		this.modifier = mod;
		this.filter = filter;
	}
	
	public PlayerValueSpell(String type, int value, int filter) {
		modifier = PlayerValueModifier.fromString(type);
		this.value = value;
		this.filter = filter;
	}
	
	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public void exequte(int playerNum) {
		this.playerNum = playerNum;
		Game.currentGame.applySpellToPlayer((playerNum + filter) % 2, this);
	}

}
