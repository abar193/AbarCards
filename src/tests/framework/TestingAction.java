package tests.framework;

/** Records information about players' single action. */
public class TestingAction {

    public TestingActionType type;
    public String sourceName;
    public String targetName;
    public int sourceNumber;
    public int targetNumber;
    public int targetSide;
    
    public TestingAction() {
        
    }
    
    public static TestingAction PlayCardAction(String sourceName) {
        TestingAction ta = new TestingAction();
        ta.type = TestingActionType.PlayCard;
        ta.sourceName = sourceName;
        return ta;
    }
    
    public static TestingAction AttackAction(String attackerName, int attackerId, 
            String targetName, int targetId) 
    {
        TestingAction ta = new TestingAction();
        ta.type = TestingActionType.Attack;
        ta.sourceName = attackerName;
        ta.targetName = targetName;
        ta.sourceNumber = attackerId;
        ta.targetNumber = attackerId;
        return ta;
    }
    
    public static TestingAction CheckAction() {
        TestingAction ta = new TestingAction();
        ta.type = TestingActionType.CheckConditions;
        return ta;
    }
    
    public static TestingAction EndAction() {
        TestingAction ta = new TestingAction();
        ta.type = TestingActionType.EndTurn;
        return ta;
    }
    
    public static TestingAction TargetAction(String unitName, int unitSide, int unitId) {
        TestingAction ta = new TestingAction();
        ta.type = TestingActionType.TargetUnit;
        ta.sourceName = unitName;
        ta.sourceNumber = unitId;
        ta.targetSide = unitSide;
        return ta;
    }
    
}
