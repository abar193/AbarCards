package units;

public enum TriggeringCondition {
	OnCreate, OnTurnStart, OnTurnEnd, OnDamage, OnAllySpawn, OnAllyDamage;
	
	public static TriggeringCondition fromString(String c) {
		switch(c) {
		case "OnCreate":
			return OnCreate;
		case "OnTurnStart":
			return OnTurnStart;
		case "OnTurnEnd":
			return OnTurnEnd;
		case "OnDamage":
			return OnDamage;
		case "OnAllySpawn":
			return OnAllySpawn;
		case "OnAllyDamage":
			return OnAllyDamage;
		default: 
			return null;
		}
	}
}
