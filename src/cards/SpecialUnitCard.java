package cards;

/**
 * Card, representing special type of pre-defined unit, with special abilities. 
 * @author Abar
 *
 */
public class SpecialUnitCard extends UnitCard {
	public enum SpecialUnit {
		Other(0), DmgEqHealth(1);
		private final int value;

	    private SpecialUnit(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	    
	    public static SpecialUnit fromInteger(int a) {
	        switch(a) {
		        case 0: return Other;
		        case 1: return DmgEqHealth;
		        default: return Other;
	        }
	    }
	}
	public SpecialUnit specialUnitRef;
	
	public SpecialUnitCard(int health, int damage, int cost, String name,
			String desc) 
	{
		super(health, damage, cost, name, desc);
		// TODO Auto-generated constructor stub
	}

}
