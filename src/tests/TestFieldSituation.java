package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import units.Building;
import units.PlayerUnit;
import units.Unit;
import cards.UnitCard;
import src.FieldSituation;
import src.Game;

public class TestFieldSituation {

	@Test
	public void testInit() {
		FieldSituation fs = new FieldSituation(null);
		assertEquals(0, fs.allObjectsFromOneSide(0, false).size());
		assertEquals(0, fs.allObjectsFromOneSide(1, false).size());
	}
	
	@Test
	public void testAddRemove() {
		FieldSituation fs = new FieldSituation(null);
		fs.addHeroForSide(0, new PlayerUnit(new cards.UnitCard(0, 5, 0, "Hero", ""), 0, 
                null));
		fs.addHeroForSide(1, new PlayerUnit(new cards.UnitCard(0, 6, 0, "Hero", ""), 0, 
                null));
		Unit u1 = new Unit(new UnitCard(1, 1, 1, "TestU", ""), 0, null);
		Unit u2 = new Unit(new UnitCard(2, 2, 1, "TestU", "b"), 0, null);
		Unit u3 = new Unit(new UnitCard(3, 3, 1, "TestP", "c"), 1, null);
		assertEquals(true, fs.canObjectBeAdded(u1, 0));
		fs.addObject(u1, 0);
		assertEquals(true, fs.canObjectBeAdded(u2, 0));
		fs.addObject(u2, 0);
		assertEquals(true, fs.canObjectBeAdded(u3, 1));
		fs.addObject(u3, 1);
		assertEquals(2, fs.allObjectsFromOneSide(0, false).size());
		assertEquals(1, fs.allObjectsFromOneSide(1, false).size());
		
		Building b1 = new Building(new cards.BuildingCard(0, 1, 1, "TestB", "p"), 1, null);
		Building b2 = new Building(new cards.BuildingCard(0, 1, 1, "TestB", "q"), 1, null);
		Building b3 = new Building(new cards.BuildingCard(0, 1, 1, "TestC", "r"), 0, null);
		fs.addObject(b1, 1);
		fs.addObject(b2, 1);
		fs.addObject(b3, 0);
		
		assertEquals(3, fs.allBuildingsFromOneSide(1).size());
		assertEquals(2, fs.allBuildingsFromOneSide(0).size());
		
		FieldSituation fs2 = new FieldSituation(fs.toMap(), null);
		assertEquals(2, fs2.allObjectsFromOneSide(0, false).size());
        assertEquals(1, fs2.allObjectsFromOneSide(1, false).size());
        assertEquals(3, fs.allBuildingsFromOneSide(1).size());
        assertEquals(2, fs.allBuildingsFromOneSide(0).size());
		
	}
	
	@Test 
	public void testFullDeck() {
		/*FieldSituation fs = new FieldSituation(null);
		for(int i = 0; i < fs.MAXFIELDUNITS; i++) {
			fs.addUnit(new Unit(new UnitCard(i, i, i, "", ""), 0, null), 0);
			fs.addUnit(new Unit(new UnitCard(i, i, i, "", ""), 1, null), 1);
		}
		assertEquals(false, fs.canUnitBeAdded(new Unit(new UnitCard(0, 0, 0, "", ""), 0, null), 0));
		assertEquals(false, fs.canUnitBeAdded(new Unit(new UnitCard(0, 0, 0, "", ""), 1, null), 1));*/
	}
	
	@Test
	public void testTaunts() {
	    
	}
	
	public void testUnitPos() {
		FieldSituation fs = new FieldSituation(null);
	}
	
}

