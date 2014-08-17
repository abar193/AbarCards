package effects;

/**
 * Describes what type should aura be
 * @author Abar
 */
public enum AuraType {
	UnitCost(0), UnitDamage(1), UnitHealth(2);
	private final int value;

    private AuraType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    
    public static AuraType fromInt(int a) {
    	switch(a) {
    		case 0: return UnitCost;
    		case 1: return UnitDamage;
    		case 2: return UnitHealth;
    		default: return null;
    	}
    }
}
