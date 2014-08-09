package tests;

import static org.junit.Assert.*;
import org.junit.Test;

import units.Unit;
import cards.UnitCard;
import src.FieldSituation;

public class TestFieldSituation {

	@Test
	public void testInit() {
		FieldSituation fs = new FieldSituation();
		assertEquals(0, fs.playerUnits.get(0).size());
		assertEquals(0, fs.playerUnits.get(1).size());
	}
	
	@Test
	public void testAddRemove() {
		FieldSituation fs = new FieldSituation();
		Unit u1 = new Unit(new UnitCard(1, 1, 1, "L", "a"));
		Unit u2 = new Unit(new UnitCard(2, 2, 2, "M", "b"));
		Unit u3 = new Unit(new UnitCard(3, 3, 3, "N", "c"));
		assertEquals(true, fs.canUnitBeAdded(u1, 0));
		fs.addUnit(u1, 0);
		assertEquals(true, fs.canUnitBeAdded(u2, 0));
		fs.addUnit(u2, 0);
		assertEquals(true, fs.canUnitBeAdded(u3, 1));
		fs.addUnit(u3, 1);
		assertEquals(2, fs.playerUnits.get(0).size());
		assertEquals(1, fs.playerUnits.get(1).size());
		assertEquals(true, fs.removeUnitOfPlayer(u1, 0));
		assertEquals(false, fs.removeUnitOfPlayer(u1, 0));
		assertEquals(1, fs.playerUnits.get(0).size());
		assertEquals(true, fs.removeUnitOfPlayer(u2, 0));
		assertEquals(0, fs.playerUnits.get(0).size());
		assertEquals(false, fs.removeUnitOfPlayer(u3, 0));
		assertEquals(true, fs.removeUnitOfPlayer(u3, 1));
		assertEquals(0, fs.playerUnits.get(1).size());
	}
	
	@Test 
	public void testFullDeck() {
		FieldSituation fs = new FieldSituation();
		for(int i = 0; i < fs.MAXFIELDUNITS; i++) {
			fs.addUnit(new Unit(new UnitCard(i, i, i, "", "")), 0);
			fs.addUnit(new Unit(new UnitCard(i, i, i, "", "")), 1);
		}
		
		assertEquals(false, fs.canUnitBeAdded(new Unit(new UnitCard(0, 0, 0, "", "")), 0));
		assertEquals(false, fs.canUnitBeAdded(new Unit(new UnitCard(0, 0, 0, "", "")), 1));
	}
}
