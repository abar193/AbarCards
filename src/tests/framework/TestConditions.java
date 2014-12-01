package tests.framework;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import units.TriggeringCondition;
import units.TriggeringCondition.*;

public class TestConditions {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        TriggeringCondition c1 = new TriggeringCondition("You|TakeDamage");
        TriggeringCondition c2 = new TriggeringCondition("You|Die");
        TriggeringCondition c2a = new TriggeringCondition("You|Spawn");
        assertNotEquals(c1, c2);
        assertNotEquals(c2, c2a);
        TriggeringCondition c3 = new TriggeringCondition(Side.Ally, OType.Building, Condition.TakeDamage);
        assertNotEquals(c3, c2);
        assertNotEquals(c3, c2a);
        assertNotEquals(c3, c1);
        TriggeringCondition c3a = new TriggeringCondition("Ally|Building|TakeDamage");
        assertEquals(c3a, c3);
    }

}
