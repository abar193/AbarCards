package units;

import java.util.ArrayList;

import cards.UnitCard;

public class PlayerUnit extends Unit {
    
	public PlayerUnit(UnitCard card, int player, src.ProviderGameInterface cG) {
		super(card, player, cG);
		
	}	
	
	@Override
	public boolean canBeTargetedByBuff(effects.Buff b) {
	    switch (b.type) {
            case AddHealth:
            case Heal:
            case Damage:
            case Hurt: 
            case HealthSetTo:
                return true;
            default:
                return false; 
        }
	}
	
	@Override
	public void applyBuff(effects.Buff b) {
		switch (b.type) {
    		case AddHealth:
    		case Heal:
    		case Damage:
    		case Hurt: 
    		case HealthSetTo:
    		    super.applyBuff(b);
    		    break;
    		default:
    			break;		
		}
	}
	
	@Override
	public boolean matchesFilter(UnitFilter f) {
	    if(f == null) return true;
	    if(f.type == UnitFilterType.IsHero) return true;
	    else return super.matchesFilter(f);
	}


}
