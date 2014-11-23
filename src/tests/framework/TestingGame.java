package tests.framework;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;

import players.PlayerData;
import players.ThreadKiller;
import cards.BasicCard;
import cards.CardJSONOperations;
import cards.Deck;
import src.FieldSituation;
import src.Game;
import units.FieldObject;
import units.Unit;
import units.UnitFactory;

/**
 * Extended version of game, with functionality enabling automated unit tests.
 * <P>
 * Class TestingGame extends Game, and overrides method play(). It also has additional way of 
 * configuring players: by calling setDecks(ArrayList, int, ArrayList, int) method first and 
 * configurePlayers(int, int, PlayerInterface, PlayerInterface method later). First one receives 
 * ArrayList of card names, and builds two not shuffled decks. Second one does the remaining 
 * configurations. Configuring game via those methods enables defining way in which players 
 * will get their cards and who will be the first one.
 * <P>
 * After each turn game calls validateSituation() method. These one checks if end-turn situation 
 * matches the one described in TestingSituation. TestingSituations can be easily created and configured
 * by calling addSituation() method, which adds new TestingSituation and returns it. For futher information 
 * see {@link tests.framework.TestingSituation}. 
 * 
 * @author Abar
 *
 */
public class TestingGame extends Game {
    
    private Deck deck1, deck2;
    public PlayerData[] pd; // public link to protected data 
    public int turns = -1;
    private ArrayList<TestingSituation> situations = new ArrayList<TestingSituation>(10);
    private int counter = 0;
    
    public TestingGame() {
        
    }
    
    /**
     * Creates and returns new TestingSituation.
     * @return new TestingSituation, appended to the end of the situations list.
     */
    public TestingSituation addSituation() {
        TestingSituation ts = new TestingSituation();
        situations.add(ts);
        return ts;
    }
    
    /** 
     * Finds card by it's name in race-specific or neutrals deck.
     * @param name cards name
     * @param race player's deck
     * @return corresponding card from CardJSONOperations.singleAllDeck().
     */
    public BasicCard cardFromName(String name, int race) {
        if(name.startsWith("Dev:")) {
            return new cards.DevCard(name, "");
        }
        ArrayList<BasicCard> cards = CardJSONOperations.singleAllDeck().get(race);
        Iterator<BasicCard> i = cards.iterator();
        while(i.hasNext()) {
            BasicCard bc = i.next();
            if(bc.name.equals(name)) {
                return bc;
            }
        }
        cards = CardJSONOperations.singleAllDeck().get(1);
        i = cards.iterator();
        while(i.hasNext()) {
            BasicCard bc = i.next();
            if(bc.name.equals(name)) {
                return bc;
            }
        }
        fail("No card found with name: " + name);
        return null;
    }
    
    /** 
     * Generates decks with cards found by using cardFromName method on the elements of string ArrayLists.
     * @param player1
     * @param player2 Players' decks 
     * @param r1
     * @param r2 Players' races 
     */
    public void setDecks(ArrayList<String> player1, int r1, ArrayList<String> player2, int r2) {
        
        ArrayList<BasicCard> d1 = new ArrayList<BasicCard>(player1.size());
        Iterator<String> i = player1.iterator();
        while(i.hasNext()) {
            d1.add(cardFromName(i.next(), r1));
        }
        
        ArrayList<BasicCard> d2 = new ArrayList<BasicCard>(player2.size());
        i = player2.iterator();
        while(i.hasNext()) {
            d2.add(cardFromName(i.next(), r2));
        }
        
        this.deck1 = new Deck(d1, null);
        this.deck2 = new Deck(d2, null);
    }
    
    /**
     * Initialises players with the given amount of lives.
     * @param i1 
     * @param i2 for automated units tests these should either be TestingPlayers or 
     *   PassivesBots, but for some cases those may even be RealPlayers.
     */
    public void configurePlayers(int h1, int h2, 
            players.PlayerInterface i1, players.PlayerInterface i2) 
    {
        field = new FieldSituation(this);
        assertNotNull(deck1);
        assertNotNull(deck2);
        i1.setParentGameInterface(this);
        i2.setParentGameInterface(this);
        playersData = new PlayerData[2];
        playersData[0] = new PlayerData(deck1, h1, i1, this);
        playersData[1] = new PlayerData(deck2, h2, i2, this);
        playersData[0].playerNumber = 0;
        playersData[1].playerNumber = 1;
        playersData[0].pullCard(2, false);
        playersData[1].pullCard(3, false);
        pd = playersData;
        players = new ArrayList<players.PlayerInterface>(2);
        players.add(i1);
        players.add(i2);
    }
    
    private void validateSituation(int i) {
        if(situations.size() <= counter) {
            System.out.println("No more conditions remain to check.");
            return;
        }
        TestingSituation ts = situations.get(counter);
        int r = ts.validateConditions(this);
        assertEquals("Turn " + i, -1, r);
        counter++;
    }
    
    @Override
    public void play() {
        factory = new UnitFactory();
        
        /* * * Game cycle * * */
        field.refreshObjects();
        int i = 0;
        this.gameRunning = true;
        while(playersData[0].getHealth() > 0 && playersData[1].getHealth() > 0 && gameRunning) {
            /* * * Init * * */
            int player = i % 2;
            int opponent = (i + 1) % 2;
            for(FieldObject u : field.allObjectsFromOneSide(player, false)) {
                u.startTurn();
            }
            recalculateFieldModifiers();
            ArrayList<BasicCard> cards = playersData[player].pullCard(1, false);
            if(cards != null) 
                informLostCards(cards, player);
            
            /* * * Player * * */
            playersData[player].newTurn();
            players.get(player).reciveInfo(playersData[player], field, 
                    playersData[opponent].craeteOpenData());
            players.get(opponent).reciveInfo(playersData[opponent], field, 
                    playersData[player].craeteOpenData());
            playingCard = false;
            
            playerTurn = player;
            players.get(player).run();
            /* * * End turn * * */
            
            playingCard = false;
            playersData[player].auras.removeOutdatedAuras();
            for(FieldObject u : field.allObjectsFromOneSide(i % 2, false)) {
                u.endTurn();
            }
            
            validateSituation(i);
            i++;
            if(turns >= 0 && i >= turns) break;  
        }
    }
    
    @Override 
    public void endTurn(int player) {
        
    }  
}
