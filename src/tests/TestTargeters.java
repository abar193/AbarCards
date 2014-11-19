package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import src.Game;
import src.FieldSituation;
import effects.*;
import cards.UnitCard;
import units.FieldObject;
import units.Unit;

public class TestTargeters {

	@Test
	public void testRadnom() {
		FieldSituation fs = new FieldSituation(null);
		UnitCard c1 = new UnitCard(1, 1, 0, "", "");
		Unit u1 = new Unit(c1, 0, null);
		UnitCard c2 = new UnitCard(2, 1, 0, "", "");
		Unit u2 = new Unit(c2, 0, null);
		UnitCard c3 = new UnitCard(1, 3, 0, "", "");
		Unit u3 = new Unit(c3, 1, null);
		fs.addObject(u1, 0);
		fs.addObject(u2, 0);
		fs.addObject(u3, 1);
		Game currentGame = new Game();
		currentGame.applyFieldSituation(fs);
		
		{
			RandomTargeter rt = new RandomTargeter(-1, 3, false, false);
			assertEquals(true, rt.hasTargets(0, null, currentGame));
			ArrayList<FieldObject> ual = rt.selectTargets(0, null, currentGame);
			Unit[] u = ual.toArray(new Unit[ual.size()]);
			assertEquals(3, u.length);
			assertNotEquals(u[0], u[1]);
			assertNotEquals(u[0], u[2]);
			assertNotEquals(u[1], u[2]);
		}
		
		{
			RandomTargeter rt = new RandomTargeter(0, 3, false, false);
			assertEquals(true, rt.hasTargets(0, null, currentGame));
			ArrayList<FieldObject> ual = rt.selectTargets(0, null, currentGame);
			Unit[] u = ual.toArray(new Unit[ual.size()]);
			assertEquals(2, u.length);
			assertNotEquals(u[0], u[1]);
		}
		
		{
			RandomTargeter rt = new RandomTargeter(0, 3, true, false);
			assertEquals(true, rt.hasTargets(0, null, currentGame));
			ArrayList<FieldObject> ual = rt.selectTargets(0, null, currentGame);
			Unit[] u = ual.toArray(new Unit[ual.size()]);
			assertEquals(3, u.length);
			assertEquals(true, u[0].equals(u[1]) | u[1].equals(u[2]) | u[0].equals(u[2]));
		}
		
		{
			RandomTargeter rt = new RandomTargeter(1, 5, true, false);
			assertEquals(true, rt.hasTargets(0, null, currentGame));
			ArrayList<FieldObject> ual = rt.selectTargets(0, null, currentGame);
			Unit[] u = ual.toArray(new Unit[ual.size()]);
			assertEquals(5, u.length);
			for(Unit un : u) {
				assertEquals(un, u3);
			}
		}
	}
	
	@Test
	public void testAllUnits() {
		FieldSituation fs = new FieldSituation(null);
		UnitCard c1 = new UnitCard(1, 1, 0, "", "");
		Unit u1 = new Unit(c1, 0, null);
		UnitCard c2 = new UnitCard(2, 1, 0, "", "");
		Unit u2 = new Unit(c2, 0, null);
		UnitCard c3 = new UnitCard(1, 3, 0, "", "");
		Unit u3 = new Unit(c3, 1, null);
		fs.addObject(u1, 0);
		fs.addObject(u2, 0);
		fs.addObject(u3, 1);
		
		Game currentGame = new Game();
		currentGame.applyFieldSituation(fs);
		
		{
			AllUnitsTargeter at = new AllUnitsTargeter(0, false);
			assertEquals(true, at.hasTargets(0, null, currentGame));
			ArrayList<FieldObject> u = at.selectTargets(0, null, currentGame);
			assertEquals(2, u.size());
			assertNotEquals(u.get(0), u.get(1));
		}
		
		{
			AllUnitsTargeter at = new AllUnitsTargeter(1, false);
			assertEquals(true, at.hasTargets(0, null, currentGame));
			ArrayList<FieldObject> u = at.selectTargets(0, null, currentGame);
			assertEquals(1, u.size());
		}
		
		{
			AllUnitsTargeter at = new AllUnitsTargeter(-1, false);
			assertEquals(true, at.hasTargets(0, null, currentGame));
			ArrayList<FieldObject> u = at.selectTargets(0, null, currentGame);
			assertEquals(3, u.size());
			assertNotEquals(u.get(0), u.get(1));
			assertNotEquals(u.get(2), u.get(1));
			assertNotEquals(u.get(0), u.get(2));
		}
	}
	
	@Test
	public void testSelf() {
		Unit u = new Unit(new UnitCard(0, 5, 0, "", ""), 0, null);
		
		SelfTargeter st = new SelfTargeter();
		assertEquals(true, st.hasTargets(0, u, null));
		assertEquals(true, st.selectTargets(0, u, null).get(0).equals(u));
		assertNull(st.selectTargets(1, null, null));
	}
	
	@Test
	public void testNeighbors() {
		FieldSituation fs = new FieldSituation(null);
		UnitCard c1 = new UnitCard(1, 1, 0, "", "");
		Unit u1 = new Unit(c1, 0, null);
		UnitCard c2 = new UnitCard(2, 1, 0, "", "");
		Unit u2 = new Unit(c2, 0, null);
		UnitCard c3 = new UnitCard(1, 3, 0, "", "");
		Unit u3 = new Unit(c3, 1, null);
		
		fs.addObject(u1, 0);
		fs.addObject(u2, 0);
		fs.addObject(u3, 1);
		/* u1 u2 
		 * ----
		 * u3
		 */
		Game currentGame = new Game();
		currentGame.applyFieldSituation(fs);
		
		{
			NeighborTargeter nt = new NeighborTargeter(-1);
			assertEquals(u1, nt.selectTargets(0, u2, currentGame).get(0));
			assertNull(nt.selectTargets(0, u1, currentGame));
			assertNull(nt.selectTargets(1, u3, currentGame));
			nt = new NeighborTargeter(1);
			assertEquals(u2, nt.selectTargets(0, u1, currentGame).get(0));
			assertNull(nt.selectTargets(0, u2, currentGame));
			assertNull(nt.selectTargets(1, u3, currentGame));
			nt = new NeighborTargeter(0);
			assertEquals(u3, nt.selectTargets(0, u3, currentGame).get(0));
		}
		
		UnitCard c4 = new UnitCard(1, 4, 0, "", "");
		Unit u4 = new Unit(c4, 1, null);
		UnitCard c5 = new UnitCard(1, 5, 0, "", "");
		Unit u5 = new Unit(c5, 1, null);
		UnitCard c6 = new UnitCard(1, 6, 0, "", "");
		Unit u6 = new Unit(c6, 1, null);
		
		fs.addObject(u4, 1);
		fs.addObject(u5, 1);
		fs.addObject(u6, 1);
		// u3 u4 u5 u6
		
		{
			NeighborTargeter nt = new NeighborTargeter(1);
			assertEquals(u4, nt.selectTargets(0, u3, currentGame).get(0));
			assertEquals(u6, nt.selectTargets(1, u5, currentGame).get(0));
			nt = new NeighborTargeter(2);
			assertEquals(u6, nt.selectTargets(1, u4, currentGame).get(0));
			assertEquals(u5, nt.selectTargets(0, u3, currentGame).get(0));
			assertNull(nt.selectTargets(1, u5, currentGame));
			assertNull(nt.selectTargets(0, u6, currentGame));
		}
	}
	

}
