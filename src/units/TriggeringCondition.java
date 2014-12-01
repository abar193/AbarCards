package units;

import effects.BuffType;

/**
 * Enum, describing when should UnitPowers be triggered.
 * @author Abar
 *
 */
public class TriggeringCondition {
	
	public enum ConditionType {
	    Global, OtherUnit, You;
	}
	
	public enum Side {
	    Ally, Enemy, ALL;
	}
	
	public enum OType {
	    Unit, Building, Any;
	}
	
	public enum Condition {
	    BeforeSpawn, Spawn, TakeDamage, Healed, Die;
	}
	
	public enum GlobalCondition {
	    TurnStart, TurnEnd, CardPlayed, SpellUsed, /*EnergyGained,*/ Always; 
	}
	
	private ConditionType condType; 
	private Side condSide;
	private OType objType;
	private Condition condition;
	private GlobalCondition globCond;
	
	@Override
	public boolean equals(Object o) {
	    if(o instanceof TriggeringCondition) {
	        TriggeringCondition oo = (TriggeringCondition)o;
	        if(oo.condType == this.condType && this.condType == ConditionType.Global) {
	            return oo.globCond == this.globCond;
	        } else if(oo.condType == this.condType && this.condType == ConditionType.OtherUnit) {
	            boolean b1 = (this.condSide == Side.ALL || oo.condSide == Side.ALL) || (this.condSide == oo.condSide);
	            boolean b2 = this.objType == oo.objType || this.objType == OType.Any || oo.objType == OType.Any;
	            boolean b3 = this.condition == oo.condition;
	            return b1 & b2 & b3;
	        } else if(oo.condType == this.condType && this.condType == ConditionType.You) {
	            return oo.condition == this.condition;
	        }
	        return false;
	    } else return false;
	}
	
	/**
	 * Constructs TriggeringCondition from XML parameter "|"-separated string.
	 * Examples: "You|TakeDamage", "Enemy|Any|TakeDamage", "Global|TurnEnd", "ALL|Building|Die", "Global|Always".
	 * @param str
	 */ 
	public TriggeringCondition(String str) {
	    String[] splits = str.split("\\|");
	    for(String s: splits) {
	        if(s.equals("You"))  condType = ConditionType.You;
	        else if(s.equals("Ally")) {
	            condType = ConditionType.OtherUnit;
	            condSide = Side.Ally;
	        } else if(s.equals("Enemy")) {
	            condType = ConditionType.OtherUnit;
                condSide = Side.Enemy;
	        } else if(s.equals("ALL")) {
	            condType = ConditionType.OtherUnit;
                condSide = Side.ALL;
	        } else if(s.equals("Unit")) objType = OType.Unit;
	        else if(s.equals("Building")) objType = OType.Building;
	        else if(s.equals("Any")) objType = OType.Any;
	        else if(s.equals("Global")) condType = ConditionType.Global;
	        else {
	            switch (s) {
	                case "BeforeSpawn":
	                case "Spawn":
	                case "TakeDamage":
	                case "Healed":
	                case "Die":
	                    try{
	                        condition = Condition.valueOf(s);
	                    } catch (IllegalArgumentException e) {
	                        System.err.println("No condition with name: " + s); // how can it be, lol?
	                    }
	                  break;
	                case "Always":
	                case "TurnStart":
	                case "TurnEnd":
	                case "CardPlayed":
	                case "SpellUsed": 
	                case "EnergyGained":
	                    try{
	                        globCond = GlobalCondition.valueOf(s);
                        } catch (IllegalArgumentException e) {
                            System.err.println("No condition with name: " + s); // how can it be, lol?
                        }
                      break;
                  default:
                      System.err.println("Unknown: " + s);
	            }
	        }
	    } //for
	    
	    if(globCond != null) {
	        if(condType == null) condType = ConditionType.Global;
	        if(condType != ConditionType.Global || condition != null || objType != null)
	            System.err.println("Global condition " + str + " not vaildated.");
	    } else if(condType == ConditionType.You) {
	        if(condition == null || globCond != null || objType != null) {
	            System.err.println("You-condition not validated " + str);
	        }   
	    } else if(condType == ConditionType.OtherUnit) {
	        if(globCond != null || condition == null || objType == null)
	            System.err.println("OtherUnit-Condition not validated " + str);
	    } else {
	        System.err.println("Condition " + str + " not validated ");
	    }
	}
	
	public TriggeringCondition(Side s, OType t, Condition c) {
	    condSide = s;
	    condition = c;
	    objType = t;
	    condType = ConditionType.OtherUnit;
	}
	
	public TriggeringCondition(GlobalCondition gc) {
	    condType = ConditionType.Global;
	    globCond = gc;
	}
	
	public TriggeringCondition(Condition c) {
	    condType = ConditionType.You;
	    condition = c;
	}
	
	/**
	 * Constructs otherUnit-condition from you-condition.  
	 */
	public static TriggeringCondition otherFromYouCondition(TriggeringCondition oc, FieldObject fo) {
	    if(oc.condType != ConditionType.You) return null;
	    if(fo instanceof Building) {
	        return new TriggeringCondition(Side.Ally, OType.Building, oc.condition);
	    } else {
	        return new TriggeringCondition(Side.Ally, OType.Unit, oc.condition);
	    }
	}
	
	public static TriggeringCondition mirrorUnitCondition(TriggeringCondition oc) {
	    if(oc.condType != ConditionType.OtherUnit || oc.condSide == Side.ALL) return null;
	    if(oc.condSide == Side.Ally) 
	        return new TriggeringCondition(Side.Enemy, oc.objType, oc.condition);
	    else 
	        return new TriggeringCondition(Side.Ally, oc.objType, oc.condition);
	}

	public boolean isYouCondition() {
	    return this.condType == ConditionType.You;
	}
	
	public boolean isAlwaysCondition() {
	    return this.globCond == GlobalCondition.Always;
	}
	
	@Override
	public String toString() {
	    switch(this.condType) {
            case Global:
                return "Global|" + globCond.toString();
            case OtherUnit:
                return condSide.toString() + "|" + objType.toString() + "|" + condition.toString();
            case You:
                return "You|" + condition.toString();
            default:
                return "Error!";
	        
	    }
	}
}
