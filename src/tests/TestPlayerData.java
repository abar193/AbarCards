package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import players.PlayerData;

import java.util.ArrayList;

import cards.Deck;
import cards.TestCard;
import cards.BasicCard;
import cards.UnitCard;

import units.Unit;

public class TestPlayerData {

	@Test
	public void testInit() {
		PlayerData player1 = new PlayerData(new Deck(), 30, null);
		PlayerData player2 = new PlayerData(new Deck(), 30, null);
		assertNotNull(player1);
		assertNotNull(player2);
	}
	
	@Test 
	public void testPull() {
		{
			Deck d = new Deck();
			TestCard c0 = new TestCard(0);
			c0.cost = 0; 
			d.addCard(c0);
			TestCard c1 = new TestCard(1);
			c1.cost = 1; 
			d.addCard(c1);
			TestCard c2 = new TestCard(2);
			c2.cost = 1; 
			d.addCard(c2);
			PlayerData p = new PlayerData(d, 30, null);
			
			assertNull(p.pullCard(3));
			assertEquals(30, p.getHealth());
			p.pullCard(1);
			assertEquals(29, p.getHealth());
		}
		
		{
			Deck d = new Deck();
			TestCard c0 = new TestCard(0);
			c0.cost = 0; 
			d.addCard(c0);
			TestCard c1 = new TestCard(1);
			c1.cost = 1; 
			d.addCard(c1);
			TestCard c2 = new TestCard(2);
			c2.cost = 1; 
			d.addCard(c2);
			PlayerData p = new PlayerData(d, 30, null, 2);
			ArrayList<BasicCard> al = p.pullCard(3);
			assertEquals(1, al.size());
			assertEquals(c2, al.get(0));
			assertEquals(30, p.getHealth());
		}
	}
	
	@Test
	public void testCards() {
		Deck d = new Deck();
		TestCard c0 = new TestCard(0);
		c0.cost = 0; 
		d.addCard(c0);
		TestCard c1 = new TestCard(1);
		c1.cost = 1; 
		d.addCard(c1);
		TestCard c2 = new TestCard(2);
		c2.cost = 2; 
		d.addCard(c2);
		PlayerData p = new PlayerData(d, 30, null);
		p.pullCard(3);
		assertEquals(p.getAvailableMana(), 0);
		assertEquals(p.getTotalMana(), 0);
		assertEquals(true, p.canPlayCard(c0));
		assertEquals(false, p.canPlayCard(c1));
		
		p.newTurn();
		assertEquals(p.getAvailableMana(), 1);
		assertEquals(p.getTotalMana(), 1);
		assertEquals(true, p.canPlayCard(c0));
		assertEquals(true, p.canPlayCard(c1));
		assertEquals(false, p.canPlayCard(c2));
		
		try {
			p.playCard(c0);
			p.playCard(c1);
			assertEquals(false, p.canPlayCard(c0));
			assertEquals(false, p.canPlayCard(c1));
		} catch (IllegalArgumentException e) {
			fail("Exception thrown");
		}
	}
	
	@Test
	public void testAura() {
		PlayerData pd = new PlayerData(null, 30, null);
		pd.auras.addAura(new effects.AuraEffect(effects.AuraType.UnitDamage, 5, 3));
		assertEquals(5, pd.auras.modifiersForType(effects.AuraType.UnitDamage));
		Unit u = new Unit(new UnitCard(1, 1, 1, "", ""), 0);
		pd.auras.addAura(new effects.AuraEffect(effects.AuraType.UnitHealth, 4, u));
		assertEquals(2, pd.auras.aurasCount());
		pd.auras.unitDies(u);
		assertEquals(1, pd.auras.aurasCount());
	}
}
