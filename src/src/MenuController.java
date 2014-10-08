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

    public enum PossibleOpponents {
        SinglePassiveBot, SingleEasyBot, SocketEasyBot, SocketPlayer;
    }
    
    private final MainMenu parent;
    
    public MenuController(MainMenu menu) {
        parent = menu;
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
    
    /* Called by ServerGame */
    
    /** Called, when server is ready to play. */
    public void gameApproved() {
        if(ServerGame.instance().play()) {
            players.RealPlayer r = new players.RealPlayer(new SwingVS(ServerGame.instance()));
            r.setParentGameInterface(ServerGame.instance());
            ServerGame.instance().setPI(r);
        }
    }
    
    public void waitForGame() {
        
    }
}
