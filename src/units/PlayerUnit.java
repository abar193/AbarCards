package units;

import java.util.ArrayList;

import cards.UnitCard;

public class PlayerUnit extends Unit {

	public PlayerUnit(UnitCard card, int player) {
		super(card, player);
		
	}	
	
	
	
	@Override
	public void applyBuff(effects.Buff b) {
		switch (b.type) {
		case AddDamage:
			break;
		case AddHealth:
			currentHealth += b.value;
			maxHealth += b.value;
			break;
		case AddQuality:
			setQuality(b.value);
			break;
		case Silence:
			break;
		case DamageSetTo:
			break;
		case HealthSetTo:
			currentHealth = b.value;
			break;
		case Hurt: 
			damage(b.value);
			break;
		case Heal:
			currentHealth = Math.min(maxHealth, currentHealth + b.value);
			break;
		case Kill:
			break;
		default:
			break;
			
		}
	}

}
