package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;

import cards.BasicCard;
import cards.TestCard;
import cards.Deck;


public class TestDeck {

	@Test
	public void testInitialise() {
		assertNotNull(new Deck());
		ArrayList<BasicCard> al = new ArrayList<>();
		al.add(new TestCard(1));
		al.add(new TestCard(2));
		al.add(new TestCard(3));
		Deck md = new Deck(al);
		assertNotNull(md);
		assertEquals("|TC1||TC2||TC3|", md.output());
	}
	
	@Test
	public void testAddRemoveCard() {
		Deck md = new Deck();
		md.addCard(new TestCard(1));
		md.addCard(new TestCard(2));
		md.addCard(new TestCard(3));
		assertEquals("|TC1||TC2||TC3|", md.output());
		TestCard tc = (TestCard)md.removeCard();
		assertEquals("|TC1|", tc.debugDisplay());
		assertEquals("|TC2|", md.removeCard().debugDisplay());
		assertEquals("|TC3|", md.removeCard().debugDisplay());
		assertNull(md.removeCard());
	}
	
	@Test
	public void testShuffle() {
		Deck md = new Deck();
		md.addCard(new TestCard(1));
		md.addCard(new TestCard(2));
		md.addCard(new TestCard(3));
		md.addCard(new TestCard(4));
		md.addCard(new TestCard(5));
		md.addCard(new TestCard(6));
		md.addCard(new TestCard(7));
		md.addCard(new TestCard(8));
		md.addCard(new TestCard(9));
		md.shuffleCards();
		assertNotEquals("Possible, but unlikely", "|TC1||TC2||TC3||TC4||TC5||TC6||TC7||TC8||TC9|", md.output());
		System.out.println(md.output());
	}

}
