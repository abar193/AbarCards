package tests.framework;

/** Defines single "rule" or condition from TestingSituation */
public class TestingCondition {
    
    /** Type of rule. */
    public enum ConditionType {
        PlayerIHasAtMostXCards, PlayerIHasAtLeastXCards, 
        PlayerIHasAtMostXUnits, PlayerIHasAtLeastXUnits, PlayerIHasExactXUnits,
        PlayerIHasUnitS, PlayerIHasNoUnitS, 
        UnitSOnSideIHasAtLeastXHealth, UnitSOnSideIHasAtMostXHealth, UnitSOnSideIHasExactXHealth;
    }

    public ConditionType type;
    public int X, I;
    public String S;
    
    public TestingCondition(ConditionType type, int X, int I) {
        this.type = type;
        this.X = X;
        this.I = I;
    }
    
    public TestingCondition(ConditionType type, int I, String S) {
        this.type = type;
        this.S = S;
        this.I = I;
    }
    
    public TestingCondition(ConditionType type, String S, int I, int X) {
        this.type = type; 
        //five
        this.S = S;
        this.I = I;
        this.X = X; 
        // seven.
    }
    
    public String toString() {
        switch(type) {
            case PlayerIHasAtLeastXCards: 
                return String.format("Player %d has at least %d cards", I, X);
            case PlayerIHasAtLeastXUnits: 
                return String.format("Player %d has at least %d units", I, X);
            case PlayerIHasAtMostXCards:
                return String.format("Player %d has at most %d cards", I, X);
            case PlayerIHasAtMostXUnits: 
                return String.format("Player %d has at most %d units", I, X);
            case PlayerIHasExactXUnits:
                return String.format("Player %d has %d units", I, X);
            case PlayerIHasUnitS:
                return String.format("Player %d has unit %s", I, S);
            case PlayerIHasNoUnitS:
                return String.format("Player %d has no unit %s", I, S);
            case UnitSOnSideIHasAtLeastXHealth:
                return String.format("Unit %s side %d has at least %d health", S, I, X);
            case UnitSOnSideIHasAtMostXHealth:
                return String.format("Unit %s side %d has at most %d health", S, I, X);
            case UnitSOnSideIHasExactXHealth:
                return String.format("Unit %s side %d has %d health", S, I, X);
            default: return "Some Condition";
        }
    }
    
}
