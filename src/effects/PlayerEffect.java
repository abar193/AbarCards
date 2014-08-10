package effects;

import players.PlayerData;

/**
 * Effect changing some player's properties.
 * @author Abar
 *
 */
public abstract class PlayerEffect extends AbstractEffect {

	public PlayerEffect() {
		super();
		this.type = EffectType.PlayerEffect;
	}
	
	public abstract void applyEffect(PlayerData pd);
}
