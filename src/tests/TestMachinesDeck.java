package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tests.framework.*;
import tests.framework.TestingCondition.ConditionType;

/**
 * Tests Machines deck using tests.framework.
 * @author Abar
 *
 */
public class TestMachinesDeck {

    private static ArrayList<String> unitDeck, spellDeck;
    private static ArrayList<String> opponentDeck;
    @BeforeClass
    public static void setUp() throws Exception {
        unitDeck = new ArrayList<String>() {{
            add("Smart Lamp");  // 1
            add("Provider");    // 2
            add("Cleaner");     // 2
            add("Cyborg");      // 4
            add("Spambot");     // 6
            add("G-Car");       // 9
        }};
        
        spellDeck = new ArrayList<String>() {{
            add("Dev:Draw5Cards");
            add("Dev:InfiniteMana");
            add("Smart Lamp");  // 1
            add("Shock");       // 1
            add("Overcharge");  // 1
            add("Analysis");    // 0
            add("Spark");       // 3
            add("Smart Lamp");
            add("Spark");       // 3
            add("R-Hit");       // 2 ???
            add("Bomb");        // 3 ???
        }};
        
        opponentDeck = new ArrayList<String>() {{
            add("C 1-1");
            add("Solid 3-2");
            add("Solid 2-4");
            add("Solid 4-5");
            add("Solid 4-5");
        }};
    }

    @Test
    public void testOneLamp() {
        TestingPlayer tp = new TestingPlayer()
            .addPlayCard("Smart Lamp")
            .addEndTurn();
        TestingPlayer tp1 = new TestingPlayer().addEndTurn();
        
        TestingGame tg = new TestingGame();
        tg.setDecks(unitDeck, 2, opponentDeck, 2);
        tg.configurePlayers(15, 15, tp, tp1);
        tg.turns = 1;
        tg.addSituation()
            .addISCondition(ConditionType.PlayerIHasUnitS, 0, "Smart Lamp")
            .addIXCondition(ConditionType.PlayerIHasAtMostXUnits, 0, 1)
            .addIXCondition(ConditionType.PlayerIHasAtLeastXUnits, 0, 1);
        tg.play();
    }
    
    @Test
    public void testUnitsDeck() {
        TestingGame tg = new TestingGame();
        tg.setDecks(unitDeck, 2, opponentDeck, 2);
        TestingPlayer p1 = new TestingPlayer();
        TestingPlayer p2 = new TestingPlayer();
        
        p1.addPlayCard("Smart Lamp").addEndTurn();
        tg.addSituation()
            .addISCondition(ConditionType.PlayerIHasUnitS, 0, "Smart Lamp")
            .addIXCondition(ConditionType.PlayerIHasAtMostXUnits, 0, 1)
            .addIXCondition(ConditionType.PlayerIHasAtLeastXUnits, 0, 1);
        
        p2.addPlayCard("C 1-1").addEndTurn();
        tg.addSituation()
            .addISCondition(ConditionType.PlayerIHasUnitS, 1, "C 1-1"); 
        
        p1.addPlayCard("Cleaner")
            .addEndTurn();
        tg.addSituation()
            .addSIXCondition(ConditionType.UnitSOnSideIHasAtMostXHealth, "Cleaner", 0, 2);
        
        p2.addAttack("C 1-1", 0, "Smart Lamp", 0).addEndTurn();
        tg.addSituation()
            .addSIXCondition(ConditionType.UnitSOnSideIHasAtMostXHealth, "Cleaner", 0, 3)
            .addSIXCondition(ConditionType.UnitSOnSideIHasAtLeastXHealth, "Cleaner", 0, 3);
        
        p1.addEndTurn();
        tg.addSituation();
        
        p2.addEndTurn();    
        tg.addSituation();
        
        p1.addPlayCard("Cyborg").addTarget("Cleaner", 0, 0).addEndTurn();
        tg.addSituation()
            .addIXCondition(ConditionType.PlayerIHasExactXUnits, 0, 2)
            .addSIXCondition(ConditionType.UnitSOnSideIHasExactXHealth, "Cleaner", 0, 5);
        tg.configurePlayers(15, 15, p1, p2);
        tg.turns = 7;
        tg.play();
    }

    @Test
    public void testSpellDeck() {
        TestingPlayer p0 = new TestingPlayer();
        TestingPlayer p1 = new TestingPlayer();
        
        TestingGame tg = new TestingGame();
        tg.setDecks(spellDeck, 2, opponentDeck, 2);
        tg.configurePlayers(15, 15, p0, p1);
        tg.setDevGame();
        
        p0.addPlayCard("Dev:InfiniteMana")
            .addPlayCard("Dev:Draw5Cards")
            .addPlayCard("Smart Lamp")
            .addEndTurn();
        tg.addSituation().addIXCondition(TestingCondition.ConditionType.PlayerIHasAtLeastXUnits, 0, 1);
        
        p1.addPlayCard("Solid 3-2").addEndTurn();
        tg.addSituation().addIXCondition(TestingCondition.ConditionType.PlayerIHasAtLeastXUnits, 1, 1);
        
        p0.addPlayCard("Shock").addTarget("Solid 3-2", 1, 0).addEndTurn();
        tg.addSituation().addIXCondition(ConditionType.PlayerIHasAtMostXUnits, 1, 0);
        
        p1.addPlayCard("Solid 2-4").addEndTurn();
        tg.addSituation().addSIXCondition(ConditionType.UnitSOnSideIHasExactXHealth, "Solid 2-4", 1, 4);
        
        p0.addPlayCard("Analysis").addTarget("Solid 2-4", 1, 0).addEndTurn();
        tg.addSituation().addSIXCondition(ConditionType.UnitSOnSideIHasExactXHealth, "Solid 2-4", 1, 1);
        
        p1.addPlayCard("Solid 4-5").addPlayCard("Solid 4-5").addEndTurn();
        tg.addSituation().addIXCondition(ConditionType.PlayerIHasExactXUnits, 1, 3);
        
        p0.addPlayCard("Spark").addTarget("Solid 4-5", 1, 0)
            .addAttack("Smart Lamp", 0, "Solid 2-4", 0)
            .addPlayCard("Spark").addTarget("Solid 4-5", 1, 0).addEndTurn();
        tg.addSituation()
            .addIXCondition(ConditionType.PlayerIHasExactXUnits, 1, 1)
            .addSIXCondition(ConditionType.UnitSOnSideIHasExactXHealth, "Solid 4-5", 1, 2);
        
        tg.turns = 7;
        tg.play();
    }
}
