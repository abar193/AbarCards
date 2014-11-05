package tests.framework;

import static org.junit.Assert.*;

import java.util.ArrayList;

import players.PlayerData;
import players.PlayerInterface;
import players.PlayerOpenData;
import src.FieldSituation;
import src.GameInterface;
import ui.VisualSystemInterface;
import units.Unit;

/**
 * Bot-like player, who perform predefined steps one by one.
 * <P>
 * To "program" bot to do something call methods like addPlayCard(), addAttack(),
 * addTarget(), or addEndTurn(). When run() method is called bot will proceed over 
 * predefined actions, executing those, until mets EndTurn action, or end of the 
 * predefined actions array. TargetAction should be inserted after those orders, 
 * which will lead to the call of selectTarget() method. If there are no TargetAction 
 * in queue, when selectTarget() is called, assert.fail() will be raised.
 * <P>
 * Example of creating simple bot which will play one card and then end his turn:
 * <pre>
 *   new TestingPlayer()
 *           .addPlayCard("Smart Lamp")
 *           .addEndTurn();
 * </pre>
 * @author Abar
 *
 */
public class TestingPlayer implements PlayerInterface {

    private PlayerData myData;
    private FieldSituation field;
    private GameInterface game;
    private ArrayList<TestingAction> actions = new ArrayList<TestingAction>(20);
    private int actionsCounter = 0;
    
    public TestingPlayer() {
        
    }

    public TestingPlayer addPlayCard(String sourceName) {
        actions.add(TestingAction.PlayCardAction(sourceName));
        return this;
    }
    
    public TestingPlayer addAttack(String attackerName, int attackerId, 
            String targetName, int targetId) 
    {
        actions.add(TestingAction.AttackAction(attackerName, attackerId, targetName, targetId));
        return this;
    }
    
    public TestingPlayer addCheck() {
        actions.add(TestingAction.CheckAction());
        return this;
    }
    
    public TestingPlayer addTarget(String unitName, int unitSide, int unitId) {
        actions.add(TestingAction.TargetAction(unitName, unitSide, unitId));
        return this;
    }
    
    public TestingPlayer addEndTurn() {
        actions.add(TestingAction.EndAction());
        return this;
    }
    
    @Override
    public void reciveInfo(PlayerData yourData, FieldSituation field,
            PlayerOpenData enemyData) 
    {
        myData = yourData;
        this.field = field;
    }

    @Override
    public void reciveAction(String m) {
        
    }

    @Override
    public void setParentGameInterface(GameInterface g) {
        game = g;
    }

    @Override
    public void run() {
        for(; actionsCounter < actions.size(); actionsCounter++) {
            TestingAction ta = actions.get(actionsCounter);
            switch(ta.type) {
                case Attack: {
                    int hitCount = 0;
                    Unit attacker = null;
                    Unit target = null;
                    for(Unit u : field.allUnitFromOneSide(myData.playerNumber, false)) {
                        if(u.myCard.name.equals(ta.sourceName)) {
                            if(++hitCount >= ta.sourceNumber) { 
                                attacker = u;
                                break;
                            }
                        }
                    }
                    hitCount = 0;
                    for(Unit u : field.allUnitFromOneSide((myData.playerNumber + 1) % 2, false)) {
                        if(u.myCard.name.equals(ta.targetName)) {
                            if(++hitCount >= ta.targetNumber) { 
                                target = u;
                                break;
                            }
                        }
                    }
                    assertNotNull(attacker);
                    if(target == null) {
                        game.commitAttack(field.unitPosition(attacker), -1, myData.playerNumber);
                    } else {
                        game.commitAttack(attacker, target);
                    }
                    break;
                }
                case CheckConditions:
                    // TODO
                    break;
                case EndTurn:
                    actionsCounter++;
                    game.endTurn(myData.playerNumber);
                    return;
                case PlayCard: {
                    boolean hit = false;
                    for(cards.BasicCard c : myData.getHand()) {
                        if(c.name.equals(ta.sourceName)) {
                            hit = true;
                            if(!game.canPlayCard(c, myData.playerNumber)) {
                                System.out.println("Can't play card " + c.name);
                            }
                            game.playCard(c, myData.playerNumber);
                            break;
                        }
                    }
                    if(!hit) System.out.println("Card " + ta.sourceName + " not found!");
                    break;
                }
                case TargetUnit: {
                    fail("Nothing to target, order " + ta.sourceName);
                    break;
                }
                default: {
                    fail("Unknown action type: " + ta.type.name());
                    break;
                }
            }
        }
    }

    @Override
    public Unit selectTarget() {
        TestingAction ta = actions.get(actionsCounter + 1);
        assertEquals(TestingActionType.TargetUnit, ta.type);
        
        int hitCount = 0;
        for(Unit u : field.allUnitFromOneSide((myData.playerNumber + ta.targetSide) % 2, true)) {
            if(u.myCard.name.equals(ta.sourceName)) {
                if(++hitCount >= ta.sourceNumber) {
                    actionsCounter++;
                    return u;
                }
            }
        }
        
        System.err.format("No target %s at %d found, returning null!", ta.sourceName, actionsCounter + 1);
        return null;
    }

    @Override
    public VisualSystemInterface visual() {
        
        return null;
    }

}
