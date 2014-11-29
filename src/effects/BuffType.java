package effects;

/**
 * Enum storing possible BuffTypes.
 * @author Abar
 *
 */
public enum BuffType {
    Silence, Sabotage, Heal, Repair, Hurt, Ram, Damage, Kill, Demolish, 
	AddHealth, AddDamage, AddQuality, HealthSetTo, DamageSetTo, 
	ModDmg, ModHealth, ModQuality;
	
	public static BuffType fromString(String s) {
	    try{
	        return BuffType.valueOf(s);
	    } catch (IllegalArgumentException e) {
	        System.err.println("No buffType with name: " + s);
	        return null;
	    }
	}
}
