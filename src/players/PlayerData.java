package players;

import cards.Deck;
import cards.BasicCard;
import cards.EnergyCard;
import players.PlayerInterface;
import units.PlayerUnit;
import effects.AbstractSpell;
import effects.PlayerValueSpell;
import effects.PlayerValueModifier;

import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.*;
import org.json.*;

/**
 * Stores all the important information about player's state like his deck,
 * hand or health/mana values.
 * <p>
 *  Available for the player himself, his opponent should receive 
 * 	PlayerOpenData built with createOpenData().
 * @author Abar
 */
public class PlayerData {
	/** How many damage should player take, if he has no card to pull */
	public static final int NOCARDPENALTY = 1;
	/** Max cards in hand (if exceeded, then the card is dropped */
	public static int MAXHANDLIMIT = 10;
	public static int MAXENERGY = 5;
	/** How many cards may user pull, until double pulling is enabled */
	public static int DOUBLEPULL = 4;
	
	private Deck myDeck;
	private int myActionDeckSize, myBaseDeckSize;
	private ArrayList<BasicCard> myHand;
	
	private int totalMana, totalEnergy;
	private int availableMana;
	private float availableEnergy;
	private int pulledBaseCards = 0, pulledBaseCardsTurn = 0;
	
	
	public PlayerUnit representingUnit;
    public int playerNumber;
    public AuraStorage auras;
    
	private PlayerInterface myPlayer;
	private src.ProviderGameInterface currentGame;
	
	private boolean devModeMana = false;
	
	public void devModeMana() {
	    devModeMana = true;
	    availableMana = 10;
	    totalMana = 10;
	}
	
	/**
	 * Creates new PlayerData instance with default MAXHANDLIMIT value.
	 */
	public PlayerData(Deck d, int startHealth, PlayerInterface player, 
	        src.ProviderGameInterface currentGame) 
	{
		totalMana = 0;
		availableMana = 0;
		representingUnit = new PlayerUnit(new cards.UnitCard(0, startHealth, 0, "Hero", ""), 0, 
		        currentGame);
		myHand = new ArrayList<BasicCard>(10);
		myDeck = d;
		myPlayer = player;
		auras = new AuraStorage();
		this.currentGame = currentGame;
		totalEnergy = 1;
		availableEnergy = 1.0f;
	}
	
	/**
	 * Creates new PlayerData with MAXHANDLIMIT set to maxHandCards.
	 */
	public PlayerData(Deck d, int startHealth, PlayerInterface player, int maxHandCards,
	        src.ProviderGameInterface currentGame) 
	{
		totalMana = 0;
		availableMana = 0;
		representingUnit = new PlayerUnit(new cards.UnitCard(0, startHealth, 0, "Hero", ""), 0,
		        currentGame);
		myHand = new ArrayList<BasicCard>(10);
		myDeck = d;
		MAXHANDLIMIT = maxHandCards;
		myPlayer = player;
		auras = new AuraStorage(); 
		totalEnergy = 1;
        availableEnergy = 1.0f;
	}
	
	/**
	 * Adds 1 totalMana and restores availableMana.
	 */
	public void newTurn() {
		if(myPlayer != null) 
			myPlayer.reciveAction("Your turn");
		pulledBaseCardsTurn = 0;
		if(!devModeMana) {
    		totalMana = Math.min(10, totalMana + 1);
    		availableMana = totalMana;
            availableEnergy = Math.min(totalEnergy, availableEnergy + totalEnergy / 2);
		}
	}
	
	/**
	 * Removes outdated auras.
	 */
	public void endTurn() {
		auras.removeOutdatedAuras();
		availableEnergy = Math.min(totalEnergy, availableEnergy + totalEnergy / 2);
	}
	
	public void reciveSpell(AbstractSpell spell) {
		if(spell instanceof PlayerValueSpell) {
			PlayerValueSpell ps = (PlayerValueSpell)spell;
			switch(ps.modifier) {
				case AddAvailMana: 
					this.totalMana += ps.value;
					this.availableMana += ps.value;
					break;
				case AddUnavailMana:
					this.totalMana += ps.value;
					break;
				case AddHealth: 
					this.representingUnit.heal(ps.value);
					break;
				case RemoveHealth:
					this.representingUnit.damage(ps.value);
					break;
				case PullCard: {
					ArrayList<BasicCard> cards = this.pullCard(ps.value, false);
					if(cards != null && currentGame != null) 
						currentGame.informLostCards(cards, this.playerNumber);
					break;
				}
				default:
			}
		}
	}
	
	public void recieveEnergySpell(BasicCard es) {
	    myHand.remove(es);
	    if(totalEnergy >= MAXENERGY) {
	        availableEnergy = totalEnergy;
	    } else {
	        totalEnergy += 1;
	        availableEnergy += 1;
	    }
	}
	
	/**
	 * Pulls n cards from myDeck, and places them to myHand. 
	 * Hurts himself for NOCARDPENALITY damage, if there are no more cards in deck.
	 * If there are more cards than MAXHANDLIMIT, the card pulled is not added to myHand.
	 * @param n how many cards should be pulled
	 * @return null if card was added or there were no cards, card, if MAXHANDLIMIT is reached 
	 */
	public ArrayList<BasicCard> pullCard(int n, boolean isBaseCard) {
	    if(isBaseCard) {
	        if((pulledBaseCards > PlayerData.DOUBLEPULL) && (pulledBaseCardsTurn > 0)) { 
	            return null;
	        } else if((pulledBaseCards < DOUBLEPULL) && pulledBaseCardsTurn > 1) {
	            return null;
	        }
	    }
	    
		if(myPlayer != null) 
			myPlayer.reciveAction("You pull " + n + " card(s)");
		boolean cantPullInformed = false;
		ArrayList<BasicCard> ar = new ArrayList<BasicCard>(n);
		for(int i = 0; i < n; i++) {
			BasicCard bc = (isBaseCard) ? myDeck.removeBaseCard() : myDeck.removeUnitCard();
			if(bc == null) {
				this.representingUnit.damage(NOCARDPENALTY);
				if(!cantPullInformed) {
					if(myPlayer != null) 
						myPlayer.reciveAction("No cards left, taking damage instead");
					cantPullInformed = true;
				}
			} else {
				if(myHand.size() < MAXHANDLIMIT) { 
					myHand.add(bc);
				} else {
					ar.add(bc);
				}
				
				if(isBaseCard) {
				    pulledBaseCards++;
				    pulledBaseCardsTurn++;
				}
			}
		}
		return (ar.size() == 0) ? null : ar;
	}
	
	public void recieveCard(BasicCard c) {
	    if(myHand.size() < MAXHANDLIMIT) {
	        myHand.add(c);
	    } else {
	        System.err.println("Hand is too full!");
	    }
	}
	
	/** For playing buildings' card, or in any other situation, when card will be 
	 * removed from hand before user gets a chance to see it. 
	 * @param c card to insert.
	 */
	public void forceReceive(BasicCard c) {
	    myHand.add(c);
	}
	
	/**
	 * Drains mana, and removes card from player's hand.
	 * @throws IllegalArgumentException if the card does not match the canPlayCard criteria
	 * @param bc card to be played
	 */
	public void playCard(BasicCard bc) {
		if(canPlayCard(bc)) {
			if(myHand.remove(bc)) {
			    if(!devModeMana) {
			        availableMana -= bc.cost;
			        availableEnergy -= bc.energyCost;
			    }
				return;
			}
		}
		throw new IllegalArgumentException();
	}
	
	public void takeDamage(int n) {
		this.representingUnit.damage(n);
	}
	
	/**
	 * Checks, if card can be played (sufficient mana, and player hands that card). 
	 * @return true if card can be played
	 */	
	public boolean canPlayCard(BasicCard bc) {
		if(availableMana + auras.getModifiers()[0] >= bc.cost
		        && availableEnergy >= bc.energyCost) 
		{
			if(myHand.contains(bc)) {
				return true;
			}
		}
		return false;
	}
	
	public void setPlayerNumber(int pn) {
		playerNumber = pn;
		/*switch(pn) {
			case 0:
				player.reciveAction("You go first");
				break;
			case 1: 
				player.reciveAction("You go second");
				break;
			default: 
				player.reciveAction("You are #" + Integer.toString(pn-1)); // unreachable
		}*/
	}
	
	public int getHealth() {
		return this.representingUnit.getCurrentHealth();
	}
	
	public int getAvailableMana() {
		return availableMana;
	}
	
	public int getTotalMana() {
		return totalMana;
	}
	
	public int getAvailableEnergy() {
        return (int)availableEnergy;
    }
    
    public int getTotalEnergy() {
        return totalEnergy;
    }
    
    public void drainResource(boolean energy, int value) {
        if(energy) availableEnergy = Math.min(0, availableEnergy - value);
        else availableMana = Math.min(0, availableMana - value); 
    }
	
	public int getDeckSize(boolean isBaseSet) {
		if(myDeck == null) return (isBaseSet) ? myBaseDeckSize : myActionDeckSize;
		else return (isBaseSet) ? myDeck.getSize(true) : myDeck.getSize(false);
	}
	
	public int getHandSize() {
		return myHand.size();
	}
	
	public ArrayList<BasicCard> getHand() {
		return myHand;
	}
	
	public Map<String, String> toMap() {
		Map<String, String> m = new LinkedHashMap<String, String>();
		m.put("TotalMana", Integer.toString(getTotalMana()));
		m.put("AvailableMana", Integer.toString(this.getAvailableMana()));
		m.put("Health", Integer.toString(getHealth()));
		m.put("ActionDeckSize", Integer.toString(this.getDeckSize(false)));
		m.put("BaseDeckSize", Integer.toString(this.getDeckSize(true)));
		m.put("PlayerNumber", Integer.toString(this.playerNumber));
		JSONArray jarr = new JSONArray();
		cards.CardJSONOperations op = new cards.CardJSONOperations();
		for(int i = 0; i < this.myHand.size(); i++) {
			jarr.add(op.mapFromCard(this.myHand.get(i)));
		}
		m.put("Hand", JSONValue.toJSONString(jarr));
		return m;
	}
	
	@SuppressWarnings("unchecked")
	public PlayerData(Map m, src.ProviderGameInterface cG) {
		int pn = Integer.parseInt((String) m.get("PlayerNumber"));
		int ads = Integer.parseInt((String) m.get("ActionDeckSize"));
		int bds = Integer.parseInt((String) m.get("BaseDeckSize"));
		int h  = Integer.parseInt((String) m.get("Health"));
		int am = Integer.parseInt((String) m.get("AvailableMana"));
		int tm = Integer.parseInt((String) m.get("TotalMana"));
		
		JSONArray jarr;
		try { 
			jarr = (JSONArray) m.get("Hand");
		} catch(ClassCastException e) {
			jarr = (JSONArray) JSONValue.parse((String) m.get("Hand"));
		}
		
		myHand = new ArrayList<BasicCard>(jarr.size());
		java.util.Iterator<Map> i = jarr.iterator();
		cards.CardJSONOperations op = new cards.CardJSONOperations();
		while(i.hasNext()) {
			myHand.add(op.cardFromMap(i.next()));
		}
		this.playerNumber = pn;
		this.myActionDeckSize = ads;
		this.myBaseDeckSize = bds;
		representingUnit = new PlayerUnit(new cards.UnitCard(0, h, 0, "Hero", ""), 0, cG);
		this.availableMana = am;
		this.totalMana = tm;
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
		pod.health = this.representingUnit.getCurrentHealth();
		pod.totalMana = this.totalMana;
		pod.availableMana = this.availableMana;
		pod.totalEnergy = this.totalEnergy;
        pod.availableEnergy = (int)this.availableEnergy;
		pod.baseSetSize = this.getDeckSize(true);
		pod.actionSetSize = this.getDeckSize(false);
		pod.handSize = this.getHandSize();
		pod.playerNumber = this.playerNumber;
	}
	
	public String toString() {
        return String.format("Player #%d with %dH %d/%d mana and %d/%d cards", playerNumber,
                this.getHealth(), availableMana, totalMana, myHand.size(), myDeck.getSize(false)); 
    }
}
