package effects;

import java.util.ArrayList;
import java.util.Iterator;

import src.ProviderGameInterface;
import units.FieldObject;
import units.UnitFilter;

/** 
 * Condition is used by ConditionSpell. It checks if game situation matches 
 * some criteria, and returns boolean result.
 * @author Abar
 *
 */
public class Condition {
    
    public enum ConditionType {
        AllyHasMoreThanXMachines, AllyHasLessThanXMachines;
        public static ConditionType fromString(String s) {
            switch(s) {
                case "AllyHasMoreThanXMachines":
                    return AllyHasMoreThanXMachines;
                case "AllyHasLessThanXMachines":
                    return AllyHasLessThanXMachines;
                default: 
                    return null;
            }
        }
    }
    
    private String count;
    private ConditionType condition;
    
    public String toString() {     
        return " ?\"" + condition.toString() + " v" + count + "\""; 
    }
    
    public Condition(ConditionType condition, String count) {
        this.condition = condition;
        this.count = count;
    }
    
    public Condition(String s, String count) {
        condition = ConditionType.fromString(s);
        this.count = count;
    }
    
    private ArrayList<FieldObject> unitsMatchingFilter(ArrayList<FieldObject> units, UnitFilter f) {
        ArrayList<FieldObject> resp = new ArrayList<FieldObject>();
        Iterator<FieldObject> i = units.iterator();
        while(i.hasNext()) {
            FieldObject u = i.next();
            if(u.matchesFilter(f)) {
                resp.add(u);
            }
        }
        return resp;
    }
    
    public boolean validate(int player, ProviderGameInterface game) {
        switch(condition) {
            case AllyHasLessThanXMachines: {
                return unitsMatchingFilter(
                            game.provideFieldSituation().allObjectsFromOneSide(player, true),
                            new UnitFilter(units.UnitFilterType.ClassEquals, "Machine")
                        ).size() < CustomValueDecoder.decodeValue(count, player, null, game);
            }
            
            case AllyHasMoreThanXMachines: {
                return unitsMatchingFilter(
                            game.provideFieldSituation().allObjectsFromOneSide(player, true),
                            new UnitFilter(units.UnitFilterType.ClassEquals, "Machine")
                        ).size() > CustomValueDecoder.decodeValue(count, player, null, game); 
            }
            
            default: System.err.println("Wrong condition");
        }
        return false;
    }

}
