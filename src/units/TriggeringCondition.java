package units;

/**
 * Enum, describing when should UnitPowers be triggered.
 * @author Abar
 *
 */
public enum TriggeringCondition {
	OnCreate, OnTurnStart, OnTurnEnd, OnDamage, OnAllySpawn, OnAllyDamage, OnDeath,
	OnAllyDeath, Always;
	
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
		case "OnDeath": 
			return OnDeath;
		case "OnAllyDeath":
			return OnAllyDeath;
		case "Always":
			return Always;
		default: 
			return null;
		}
	}
}
