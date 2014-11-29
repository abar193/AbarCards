package effects;

/**
 * Class used to change unit's properties like health, damage add qualities, or restore unit 
 * to it's default settings. 
 * @author Abar
 */
public class Buff {
	public BuffType type;
	public String svalue;
	public int value;
	
	public Buff(BuffType bt, String v) {
		type = bt;
		svalue = v;
	}
	
	public boolean isBuildingBuff() {
	    switch (type) {
            case AddDamage:
            case AddHealth:
            case AddQuality:
            case Damage:
            case DamageSetTo:
            case ModDmg:
            case ModHealth:
            case ModQuality:
            case HealthSetTo:
                
            case Demolish:
            case Sabotage:
            case Ram:
            case Repair:
                return true;
            case Kill:
            case Silence:
            case Heal:
            case Hurt:
                return false;
            default:
                return false;
	        
	    }
	}
	
	public boolean isUnitBuff() {
	    switch (type) {
            case AddDamage:
            case AddHealth:
            case AddQuality:
            case Damage:
            case DamageSetTo:
            case ModDmg:
            case ModHealth:
            case ModQuality:
            case HealthSetTo:
                
            case Kill:
            case Silence:
            case Heal:
            case Hurt:
                return true;
                
            case Demolish:
            case Sabotage:
            case Ram:
            case Repair:
                return false;
            default:
                return false;
            
        }
	}
	
	public boolean isHeroBuff() {
	    switch(type) {
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
	
	// For tests
	public Buff(BuffType bt, int v) {
		type = bt;
		value = v;
	}
	
	public Buff(String s, String v) {
		type = BuffType.fromString(s);
		svalue = v;
	}
	
	public String toString() {    
        return "(" + type.toString() + ", " + svalue + ")"; 
    }
}
