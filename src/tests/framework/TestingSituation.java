package tests.framework;

import java.util.ArrayList;

import tests.framework.TestingCondition.ConditionType;
import units.FieldObject;
import units.Unit;

/** Describes end-turn game validation criteria. 
 * <p>
 * Describes how should the game look like, when the turn ends. May contain a set of rules, 
 * a single rule, or no rules at all (when we don't care how the game will look like after 
 * this this turn.
 * <p>
 * Example of adding situation to a game, which will validate, if player has 1 unit, named 
 * "Cleaner" with 5 health at the end of the turn:
 * <pre>
 *     .addSituation()
 *       .addIXCondition(ConditionType.PlayerIHasExactXUnits, 0, 1)
 *       .addSIXCondition(ConditionType.UnitSOnSideIHasExactXHealth, "Cleaner", 0, 5);
 * </pre>
 * @author Abar
 */
public class TestingSituation {

    public ArrayList<TestingCondition>conditions = new ArrayList<TestingCondition>(5);
    
    public TestingSituation() {
        
    }

    /** Adds TestingCondition to the list of rules. */
    public TestingSituation addCondition(TestingCondition c) {
        conditions.add(c);
        return this;
    }
    
    /** Adds condition with type *SomethingAtSide* I *has* X *of something* */
    public TestingSituation addIXCondition(TestingCondition.ConditionType t, 
            int I, int X) 
    {
        TestingCondition c = new TestingCondition(t, X, I);
        conditions.add(c);
        return this;
    }
    
    /** Adds condition with type *Side* I *has something named * S */
    public TestingSituation addISCondition(TestingCondition.ConditionType t, 
            int I, String S) 
    {
        TestingCondition c = new TestingCondition(t, I, S);
        conditions.add(c);
        return this;
    }
    
    /** Adds condition with type *Unit named* S *from side* I *has* X *of something* */
    public TestingSituation addSIXCondition(TestingCondition.ConditionType t, 
            String S, int I, int X) 
    {
        TestingCondition c = new TestingCondition(t, S, I, X);
        conditions.add(c);
        return this;
    }
    
    private boolean validateCondition(TestingGame g, TestingCondition condition) {
        switch(condition.type) {
            case PlayerIHasAtLeastXCards:
                return g.pd[condition.I].getHand().size() >= condition.X;
                
            case PlayerIHasAtLeastXUnits:
                return g.provideFieldSituation().allObjectsFromOneSide(condition.I, false).size()
                        >= condition.X;
                        
            case PlayerIHasAtMostXCards:
                return g.pd[condition.I].getHand().size() <= condition.X;
                
            case PlayerIHasAtMostXUnits:
                return g.provideFieldSituation().allObjectsFromOneSide(condition.I, false).size()
                        <= condition.X;
                
            case PlayerIHasExactXUnits:
                return g.provideFieldSituation().allObjectsFromOneSide(condition.I, false).size()
                        == condition.X;
                
            case PlayerIHasNoUnitS: {
                ArrayList<FieldObject> units = g.provideFieldSituation().allObjectsFromOneSide(condition.I, true);
                for(FieldObject u: units) {
                    if(u.card.name.equals(condition.S)) return false;
                }
                return true;
            }
            case PlayerIHasUnitS: {
                ArrayList<FieldObject> units = g.provideFieldSituation().allObjectsFromOneSide(condition.I, true);
                for(FieldObject u: units) {
                    if(u.card.name.equals(condition.S)) return true;
                }
                return false;
            }
            case UnitSOnSideIHasAtLeastXHealth: {
                ArrayList<FieldObject> units = g.provideFieldSituation().allObjectsFromOneSide(condition.I, true);
                for(FieldObject u: units) {
                    if(u.card.name.equals(condition.S)) {
                        return (u.getCurrentHealth() >= condition.X); 
                    }
                }
                return false;
            }
            case UnitSOnSideIHasAtMostXHealth: {
                ArrayList<FieldObject> units = g.provideFieldSituation().allObjectsFromOneSide(condition.I, true);
                for(FieldObject u: units) {
                    if(u.card.name.equals(condition.S)) {
                        return (u.getCurrentHealth() <= condition.X); 
                    }
                }
                return false;
            }
            case UnitSOnSideIHasExactXHealth: {
                ArrayList<FieldObject> units = g.provideFieldSituation().allObjectsFromOneSide(condition.I, true);
                for(FieldObject u: units) {
                    if(u.card.name.equals(condition.S)) {
                        return (u.getCurrentHealth() == condition.X); 
                    }
                }
                return false;
            }
            
            default:
                return false;
        }
        
    }
    
    /** Validates game, and returns -1 if it matches all the required conditions. 
     * Returns nomber of first "non-valid" condition otherwise.
     */
    public int validateConditions(TestingGame g) {
        int i = 0;
        for(TestingCondition c : conditions) {
            if(!validateCondition(g, c)) {
                System.out.println("Failed condition: " + c.toString());
                return i;
            }
            i++;
        }
        return -1;
    }
    
    public String toString() {
        String s = "[";
        for(TestingCondition c : conditions) {
            s += c.toString() + "; ";
        }
        s += "]";
        return s;
    }
}
