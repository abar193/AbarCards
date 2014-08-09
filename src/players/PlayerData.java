package players;

import cards.Deck;
import cards.BasicCard;

import players.PlayerInterface;

import java.util.ArrayList;

/**
 * Stores all the important information about player's state like his deck,
 * hand or health/mana state. Avaailable for the player himself, his opponent should receive 
 * 	PlayerOpenData built with createOpenData().
 * @author Abar
 */
public class PlayerData {
	/** How many damage should player take, if he has no card to pull */
	public static final int NOCARDPENALTY = 1;
	/** Max cards in hand (if exeeded, then the card is dropped */
	public static int MAXHANDLIMIT = 10;
	
	private Deck myDeck;
	private ArrayList<BasicCard> myHand;
	
	private int health;
	private int totalMana;
	private int availableMana;
	
	private PlayerInterface myPlayer;
	
	public int playerNumber;
	
	/**
	 * Creates new PlayerData instance with default MAXHANDLIMIT value.
	 */
	public PlayerData(Deck d, int startHealth, PlayerInterface player) {
		totalMana = 0;
		availableMana = 0;
		health = startHealth;
		myHand = new ArrayList<BasicCard>(10);
		myDeck = d;
		myPlayer = player;
	}
	
	/**
	 * Creates new PlayerData with MAXHANDLIMIT set to maxHandCards.
	 */
	public PlayerData(Deck d, int startHealth, PlayerInterface player, int maxHandCards) {
		totalMana = 0;
		availableMana = 0;
		health = startHealth;
		myHand = new ArrayList<BasicCard>(10);
		myDeck = d;
		MAXHANDLIMIT = maxHandCards;
		myPlayer = player;
	}
	
	/**
	 * Adds 1 totalMana and restores availableMana.
	 */
	public void newTurn() {
		if(myPlayer != null) 
			myPlayer.reciveAction("Your turn");
		totalMana = Math.min(10, totalMana + 1);
		availableMana = totalMana;
	}
	
	/**
	 * Pulls n cards from myDeck, and places them to myHand. 
	 * Hurts himself for NOCARDPENALITY damage, if there are no more cards in deck.
	 * If there are more cards than MAXHANDLIMIT, the card pulled is not added to myHand.
	 * @param n how many cards should be pulled
	 * @return null if card was added or there were no cards, card, if MAXHANDLIMIT is reached 
	 */
	public ArrayList<BasicCard> pullCard(int n) {
		if(myPlayer != null) 
			myPlayer.reciveAction("You pull " + n + " cards");
		boolean cantPullInformed = false;
		ArrayList<BasicCard> ar = new ArrayList<BasicCard>(n);
		for(int i = 0; i < n; i++) {
			BasicCard bc = myDeck.removeCard();
			if(bc == null) {
				health -= NOCARDPENALTY;
				if(!cantPullInformed) {
					myPlayer.reciveAction("No cards left, taking damage instead");
					cantPullInformed = true;
				}
			} else {
				if(myHand.size() < MAXHANDLIMIT) 
					myHand.add(bc);
				else 
					ar.add(bc);
			}
		}
		return (ar.size() == 0) ? null : ar;
	}
	
	/**
	 * Drains mana, and removes card from player's hand.
	 * @throws IllegalArgumentException if the card does not match the canPlayCard criteria
	 * @param card to be played
	 */
	public void playCard(BasicCard bc) {
		if(canPlayCard(bc)) {
			if(myHand.remove(bc)) {
				availableMana -= bc.cost;
				if(myPlayer != null) 
					myPlayer.reciveAction("You play " + bc.name + " card");
				return;
			}
		}
		throw new IllegalArgumentException();
	}
	
	/**
	 * Checks, if card can be played (sufficient mana, and player hands that card). 
	 * @return true if card can be played
	 */	
	public boolean canPlayCard(BasicCard bc) {
		if(availableMana >= bc.cost) {
			if(myHand.contains(bc)) {
				return true;
			}
		}
		return false;
	}
	
	public void setPlayerNumber(int pn) {
		playerNumber = pn;
		switch(pn) {
			case 0:
				myPlayer.reciveAction("You go first");
				break;
			case 1: 
				myPlayer.reciveAction("You go second");
				break;
			default: 
				myPlayer.reciveAction("You are #" + Integer.toString(pn-1)); // unreachable
		}
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getAvailableMana() {
		return availableMana;
	}
	
	public int getTotalMana() {
		return totalMana;
	}
	
	public int getDeckSize() {
		return myDeck.getSize();
	}
	
	public int getHandSize() {
		return myHand.size();
	}
	
	public ArrayList<BasicCard> getHand() {
		return myHand;
	}
	
	/** 
	 * Generates PlayerOpenData class, without any "dangerous" information, like hand array, or 
	 * deck reference.
	 * @return PlayerOpenData created
	 */
	public PlayerOpenData craeteOpenData() {
		PlayerOpenData pod = new PlayerOpenData();
		updateOpenData(pod);
		return pod;
	}
	
	/**
	 * Fills PlayerOpenData with actual information
	 * @param pod
	 */
	public void updateOpenData(PlayerOpenData pod) {
		pod.health = this.health;
		pod.totalMana = this.totalMana;
		pod.availableMana = this.availableMana;
		pod.deckSize = this.getDeckSize();
		pod.handSize = this.getHandSize();
		pod.playerNumber = this.playerNumber;
	}
}
