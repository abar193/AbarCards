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
	public static final int UNIT_DECK_SIZE = 16;
	public static final int BASE_DECK_SIZE = 10;
	public static final int ENERGY_CARDS = 5;
	
	private ArrayList<BasicCard> unitCards = null;
	private ArrayList<BasicCard> baseCards = null;
	@SuppressWarnings("unchecked")
	/** 
	 * Initialises deck with a list of cards and with default DECK_SIZE.
	 */
	public Deck(ArrayList<BasicCard> newUcards, ArrayList<BasicCard> newBcards) {
		unitCards = (ArrayList<BasicCard>)newUcards.clone();
		if(newBcards == null) return;
		baseCards = (ArrayList<BasicCard>)newBcards.clone();
	}
	
	/**
	 * Initialises empty deck with default DECK_SIZE.
	 */
	public Deck() {
		unitCards = new ArrayList<BasicCard>(UNIT_DECK_SIZE);
		unitCards = new ArrayList<BasicCard>(BASE_DECK_SIZE);
	}
	
	/**
	 * Adds card to unit cards list.
	 * @param bc card to add
	 */
	public void addUnitCard(BasicCard bc) {
		unitCards.add(bc);
	}
	
	/**
     * Adds card to base cards list.
     * @param bc card to add
     */
    public void addBaseCard(BasicCard bc) {
        unitCards.add(bc);
    }
	
	/** 
	 * Puts card to random place to a random place of card's list.
	 * @param bc card to add.
	 */
	public void addSomewhereUnitCard(BasicCard bc) {
		unitCards.add((new Random()).nextInt(unitCards.size()), bc);
	}
	
	/** 
     * Puts card to random place to a random place of card's list.
     * @param bc card to add.
     */
    public void addSomewhereBaseCard(BasicCard bc) {
        baseCards.add((new Random()).nextInt(baseCards.size()), bc);
    }
	
	/**
	 * Checks if card list matches games rules.
	 * @return true if it matches.
	 */
	public boolean validateCards(boolean appendEnergyCards) {
		if(unitCards == null || unitCards.size() != UNIT_DECK_SIZE) {
			return false;
		}
		if(baseCards == null || baseCards.size() != BASE_DECK_SIZE) {
            return false;
        }
		
		if(appendEnergyCards) {
		    for(int i = 0; i < ENERGY_CARDS; i++) {
		        baseCards.add(new EnergyCard("", ""));
		    }
		}
		return true;
	}
	
	/**
	 * Randomises order of cards in card list.
	 */
	public void shuffleCards() {
		java.util.Collections.shuffle(unitCards);
		if(baseCards == null) return;
		java.util.Collections.shuffle(baseCards);
	}
	
	/**
	 * Pulls card from the top and removes it from the list.
	 * @return card pulled from list
	 */
	public BasicCard removeUnitCard() {
		if(unitCards.size() > 0) {
			return unitCards.remove(0);
		} else {
			return null;
		}
	}
	
	/**
     * Pulls card from the top and removes it from the list.
     * @return card pulled from list
     */
    public BasicCard removeBaseCard() {
        if(baseCards.size() > 0) {
            return baseCards.remove(0);
        } else {
            return null;
        }
    }
	
	/**
	 * Old debug method used for unittests.
	 */
	public String output() { 
		String s = "";
		for(BasicCard bc: unitCards) {
			s += bc.debugDisplay();
		}
		return s;
	}
	
	/**
	 * @return size of cards list.
	 */
	public int getSize(boolean baseCard) {
		return (baseCard) ? baseCards.size() : unitCards.size();
	}
	
	/**
	 * Returns probability of pulling some card.
	 * @param baseCard
	 * @return
	 */
	public ArrayList<String> probabilities(boolean baseCard) {
	    ArrayList<String> outp = new ArrayList<String>();
	    ArrayList<BasicCard> cards = (baseCard) ? baseCards : unitCards;
	    String[] names = new String[cards.size()];
	    int[] hits = new int[cards.size()];
	    int s = 0;
	    for(BasicCard c : cards) {
	        boolean hit = false;
	        for(int i = 0; i < s; i++) {
	            if(names[i].equals(c.name)) {
	                hits[i]++;
	                hit = true;
	                break;
	            }
	        }
	        if(!hit) {
	            names[s] = c.name;
	            hits[s] = 1;
	            s++;
	        }
	    }
	    for(int i = 0; i < s; i++) {
	        outp.add(String.format("%s: %.02f%%", names[i], (float)(hits[i]) / (float)(s)));
	    }
	    return outp;
	}
}
