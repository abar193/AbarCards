package units;

import effects.BuffType;

/**
 * Enum, describing when should UnitPowers be triggered.
 * @author Abar
 *
 */
public enum TriggeringCondition {
	OnCreate, OnTurnStart, OnTurnEnd, OnDamage, OnAllySpawn, OnAllyDamage, OnDeath,
	OnAllyDeath, BeforeCreate, Always;
	
	public static TriggeringCondition fromString(String c) {
	    try{
            return TriggeringCondition.valueOf(c);
        } catch (IllegalArgumentException e) {
            System.err.println("No triggeringCondition with name: " + c);
            return null;
        }
	}
}
