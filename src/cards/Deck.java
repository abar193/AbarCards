package cards;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class contains ArrayList of BasicCard as well as methods to use those. <p>
 * Variable DECK_SIZE is used to limit the amount of cards in it. <p>
 * It is recommended to check with validateCards() if the deck is correct, and call shuffleCards() 
 * method before using it.
 * @author Abar
 *
 */
public class Deck {
	public static final int DECK_SIZE = 15;
	
	private ArrayList<BasicCard> cards = null;
	
	@SuppressWarnings("unchecked")
	/** 
	 * Initialises deck with a list of cards and with default DECK_SIZE.
	 * @param newcards cards to use
	 */
	public Deck(ArrayList<BasicCard> newcards) {
		cards = (ArrayList<BasicCard>)newcards.clone();
	}
	
	/**
	 * Initialises empty deck with default DECK_SIZE.
	 */
	public Deck() {
		cards = new ArrayList<BasicCard>(DECK_SIZE);
	}
	
	/**
	 * Adds card to card list used.
	 * @param bc card to add
	 */
	public void addCard(BasicCard bc) {
		cards.add(bc);
	}
	
	/** 
	 * Puts card to random place to a random place of card's list.
	 * @param bc card to add.
	 */
	public void addSomewhere(BasicCard bc) {
		cards.add((new Random()).nextInt(cards.size()), bc);
	}
	
	/**
	 * Checks if card list matches games rules.
	 * @return true if it matches.
	 */
	public boolean validateCards() {
		if(cards == null || cards.size() == 0 || cards.size() > DECK_SIZE) {
			return false;
		}
		return (cards.size() == DECK_SIZE);
	}
	
	/**
	 * Randomises order of cards in card list.
	 */
	public void shuffleCards() {
		if(cards.size() <= 1) return;
		
		int shuffles = 500;
		Random r = new Random();
		shuffles += r.nextInt(500);
		for(int i = 0; i < shuffles; i++) {
			int a = r.nextInt(cards.size());
			int b = r.nextInt(cards.size());
			b = (a == b) ? (b + 1) % cards.size() : b;
			
			BasicCard c1 = cards.get(a);
			BasicCard c2 = cards.get(b);
			
			cards.remove(a);
			cards.remove(c2);
			
			cards.add(Math.min(a, cards.size()), c2);
			cards.add(Math.min(b, cards.size()), c1);
		}
	}
	
	/**
	 * Pulls card from the top and removes it from the list.
	 * @return card pulled from list
	 */
	public BasicCard removeCard() {
		if(cards.size() > 0) {
			return cards.remove(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Old debug method used for unittests.
	 */
	public String output() { 
		String s = "";
		for(BasicCard bc: cards) {
			s += bc.debugDisplay();
		}
		return s;
	}
	
	/**
	 * @return size of cards list.
	 */
	public int getSize() {
		return cards.size();
	}
}
