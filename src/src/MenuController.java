package src;

import java.io.File;
import java.util.ArrayList;

import network.ServerGame;
import ui.MainMenu;
import ui.SwingVS;
import cards.BasicCard;
import cards.Deck;
import decks.DeckPackReader;

public class MenuController {

    private static MenuController instance;
    
    /** For use from SwingVS only. */
    public static MenuController instance() {
        return instance;
    }
    
    public enum PossibleOpponents {
        SinglePassiveBot, SingleEasyBot, SocketEasyBot, SocketPlayer;
    }
    
    private final MainMenu parent;
    
    public MenuController(MainMenu menu) {
        parent = menu;
        instance = this;
    }
    
    public DeckBuilder provideDeckBuilder(String race) {
        DeckBuilder db = new DeckBuilder(race, this);
        return db;
    }
    
    public void goBack() {
        parent.goBack();
    }
    
    public void launchGame(String filename, PossibleOpponents opp) {
        ArrayList<BasicCard> cards = DeckBuilder.loadDeck(new File(filename));
        
        if(opp == PossibleOpponents.SinglePassiveBot || opp == PossibleOpponents.SingleEasyBot) {
            singleplayerGame(new Deck(cards), opp);
        } else {
            parent.reciveWaitSignal();
            String opponent = ""; 
            if(opp == PossibleOpponents.SocketEasyBot) opponent = "Terran";
            else if(opp == PossibleOpponents.SocketPlayer) opponent = "Player";
            ServerGame.instance().setMenuController(this);
            ServerGame.instance().validateDeck(cards, opponent);
        }
    }
    
    private void singleplayerGame(Deck d, PossibleOpponents opp) {
        DeckPackReader dpr = new DeckPackReader();
        d.shuffleCards();
        Game g = new Game();
        SwingVS vs = new SwingVS(g);
        players.RealPlayer r = new players.RealPlayer(vs);
        Deck d2 = new Deck(dpr.parseFile("BotImbaDeck.xml"));
        d2.shuffleCards();
        switch(opp) {
            case SingleEasyBot: 
                g.configure(r, new players.SimpleBot(), d, d2, 15, 15);
                break;
            case SinglePassiveBot:
                g.configure(r, new players.PassiveBot(), d, d2, 15, 15);
                break;
            default:        
        }                
        
        parent.reciveVs(vs);
        g.play();
    }
    
    public void quitGame() {
        parent.quitGame();
    }
    
    /* Called by ServerGame */
    
    /** Called, when server has validated deck. */
    public void deckApproved() {
        ServerGame.instance().play();
    }
    
    /** Called, when server has created player game. */
    public void gameCreated() {
        SwingVS vs = new SwingVS(ServerGame.instance());
        players.RealPlayer r = new players.RealPlayer(vs);
        r.setParentGameInterface(ServerGame.instance());
        ServerGame.instance().setPI(r);
        parent.reciveVs(vs);
    }
    
    public void requestSearchCancelation() {
        System.out.println("Cancel");
        if(ServerGame.instance().cancelSearchGame()) 
            parent.cancelWaiting();
    }
    
    public void waitForGame() {
        //parent.reciveWaitSignal();
    }
}