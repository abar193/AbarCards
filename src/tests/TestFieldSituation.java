package tests;

import static org.junit.Assert.*;

import org.junit.Test;

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
		/*FieldSituation fs = new FieldSituation(null);
		Unit u1 = new Unit(new UnitCard(1, 1, 1, "L", "a"), 0, null);
		Unit u2 = new Unit(new UnitCard(2, 2, 2, "M", "b"), 0, null);
		Unit u3 = new Unit(new UnitCard(3, 3, 3, "N", "c"), 1, null);
		assertEquals(true, fs.canUnitBeAdded(u1, 0));
		fs.addUnit(u1, 0);
		assertEquals(true, fs.canUnitBeAdded(u2, 0));
		fs.addUnit(u2, 0);
		assertEquals(true, fs.canUnitBeAdded(u3, 1));
		fs.addUnit(u3, 1);
		assertEquals(2, fs.allObjectsFromOneSide(0, false).size());
		assertEquals(1, fs.allObjectsFromOneSide(1, false).size());
		assertEquals(true, fs.removeUnitOfPlayer(u1, 0));
		assertEquals(false, fs.removeUnitOfPlayer(u1, 0));
		assertEquals(1, fs.allObjectsFromOneSide(0, false).size());
		assertEquals(true, fs.removeUnitOfPlayer(u2, 0));
		assertEquals(0, fs.allObjectsFromOneSide(0, false).size());
		assertEquals(false, fs.removeUnitOfPlayer(u3, 0));
		assertEquals(true, fs.removeUnitOfPlayer(u3, 1));
		assertEquals(0, fs.allObjectsFromOneSide(1, false).size());*/
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

