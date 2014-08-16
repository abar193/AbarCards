package cards;



public class SpecialUnitClass extends UnitCard {
	public enum SpecialUnit {
		DmgEqHealth, Other
	}
	public SpecialUnit specialUnitRef;
	
	public SpecialUnitClass(int health, int damage, int cost, String name,
			String desc) 
	{
		super(health, damage, cost, name, desc);
		// TODO Auto-generated constructor stub
	}

}
