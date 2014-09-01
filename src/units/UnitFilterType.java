package units;

public enum UnitFilterType {
	ClassEquals, HealthMore, HealthLess, DamageMore, DamageLess;
	
	public static UnitFilterType fromString(String s) {
		switch(s) {
		case "ClassEquals":
			return ClassEquals;
		case "HealthMore":
			return HealthMore;
		case "HealthLess":
			return HealthLess;
		case "DamageMore":
			return DamageMore;
		case "DamageLess":
			return DamageLess;
		default:
			return null;
		}
	}
}