package tests;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cards.BasicCard;
import cards.CardJSONOperations;
import cards.SpellCard;
import cards.UnitCard;

public class TestCardsEncoding {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testObviousMachines() {
        CardJSONOperations inst = CardJSONOperations.instance;
        Map<String, String> m = new LinkedHashMap<String, String>();
        m.put("Name", "Smart Lamp");
        m.put("Cost", "1");
        m.put("Deck", "2");
        BasicCard c = (UnitCard)inst.cardFromMap(m);
        assertNotNull(c);
        assertEquals("Smart Lamp", c.name);
        
        m = new LinkedHashMap<String, String>();
        m.put("Name", "Cyborg");
        m.put("Cost", "4");
        m.put("Deck", "2");
        c = (UnitCard)inst.cardFromMap(m);
        assertNotNull(c);
        assertEquals("Cyborg", c.name);
        
        m = new LinkedHashMap<String, String>();
        m.put("Name", "Overcharge");
        m.put("Cost", "1");
        m.put("Deck", "2");
        c = inst.cardFromMap(m);
        assertNotNull(c);
        assertEquals("Overcharge", c.name);
        
        m = new LinkedHashMap<String, String>();
        m.put("Name", "Tosters!!!");
        m.put("Cost", "3");
        m.put("Deck", "2");
        c = inst.cardFromMap(m);
        assertNotNull(c);
        assertEquals("Tosters!!!", c.name);
    }
    
    @Test
    public void testHiddenMachines() {
        CardJSONOperations inst = CardJSONOperations.instance;
        Map<String, String> m = new LinkedHashMap<String, String>();
        m.put("Name", "Recrut");
        m.put("Cost", "1");
        m.put("Deck", "2");
        BasicCard c = inst.cardFromMap(m);
        assertNotNull(c);
        assertEquals("Recrut", c.name);
        
        m = new LinkedHashMap<String, String>();
        m.put("Name", ":1-1U");
        m.put("Cost", "2");
        m.put("Deck", "2");
        c = inst.cardFromMap(m);
        assertNotNull(c);
        assertEquals(":1-1U", c.name);
        
        m = new LinkedHashMap<String, String>();
        m.put("Name", "Toster");
        m.put("Cost", "0");
        m.put("Deck", "2");
        c = inst.cardFromMap(m);
        assertNotNull(c);
        assertEquals("Toster", c.name);
    }
    

}
