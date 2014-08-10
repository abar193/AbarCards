package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import src.FieldSituation;
import effects.*;
import cards.UnitCard;
import units.Unit;

public class TestTargeters {

	@Test
	public void testRadnom() {
		FieldSituation fs = new FieldSituation();
		UnitCard c1 = new UnitCard(1, 1, 0, "", "");
		Unit u1 = new Unit(c1);
		UnitCard c2 = new UnitCard(2, 1, 0, "", "");
		Unit u2 = new Unit(c2);
		UnitCard c3 = new UnitCard(1, 3, 0, "", "");
		Unit u3 = new Unit(c3);
		fs.addUnit(u1, 0);
		fs.addUnit(u2, 0);
		fs.addUnit(u3, 1);
		
		{
			RandomTargeter rt = new RandomTargeter(-1, 3, false);
			assertEquals(true, rt.hasTargets(fs, 0));
			Unit[] u = rt.selectTargets(fs, 0);
			assertEquals(3, u.length);
			assertNotEquals(u[0], u[1]);
			assertNotEquals(u[0], u[2]);
			assertNotEquals(u[1], u[2]);
		}
		
		{
			RandomTargeter rt = new RandomTargeter(0, 3, false);
			assertEquals(true, rt.hasTargets(fs, 0));
			Unit[] u = rt.selectTargets(fs, 0);
			assertEquals(2, u.length);
			assertNotEquals(u[0], u[1]);
		}
		
		{
			RandomTargeter rt = new RandomTargeter(0, 3, true);
			assertEquals(true, rt.hasTargets(fs, 0));
			Unit[] u = rt.selectTargets(fs, 0);
			assertEquals(3, u.length);
			assertEquals(true, u[0].equals(u[1]) | u[1].equals(u[2]) | u[0].equals(u[2]));
		}
		
		{
			RandomTargeter rt = new RandomTargeter(1, 5, true);
			assertEquals(true, rt.hasTargets(fs, 0));
			Unit[] u = rt.selectTargets(fs, 0);
			assertEquals(5, u.length);
			for(Unit un : u) {
				assertEquals(un, u3);
			}
		}
	}
	
	@Test
	public void testAllUnits() {
		FieldSituation fs = new FieldSituation();
		UnitCard c1 = new UnitCard(1, 1, 0, "", "");
		Unit u1 = new Unit(c1);
		UnitCard c2 = new UnitCard(2, 1, 0, "", "");
		Unit u2 = new Unit(c2);
		UnitCard c3 = new UnitCard(1, 3, 0, "", "");
		Unit u3 = new Unit(c3);
		fs.addUnit(u1, 0);
		fs.addUnit(u2, 0);
		fs.addUnit(u3, 1);
		
		{
			AllUnitsTargeter at = new AllUnitsTargeter(0);
			assertEquals(true, at.hasTargets(fs, 0));
			Unit[] u = at.selectTargets(fs, 0);
			assertEquals(2, u.length);
			assertNotEquals(u[0], u[1]);
		}
		
		{
			AllUnitsTargeter at = new AllUnitsTargeter(1);
			assertEquals(true, at.hasTargets(fs, 0));
			Unit[] u = at.selectTargets(fs, 0);
			assertEquals(1, u.length);
		}
		
		{
			AllUnitsTargeter at = new AllUnitsTargeter(-1);
			assertEquals(true, at.hasTargets(fs, 0));
			Unit[] u = at.selectTargets(fs, 0);
			assertEquals(3, u.length);
			assertNotEquals(u[0], u[1]);
			assertNotEquals(u[2], u[1]);
			assertNotEquals(u[0], u[2]);
		}
	}
	

}
