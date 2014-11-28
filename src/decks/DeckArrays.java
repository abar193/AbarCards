package decks;

import java.util.ArrayList;

import cards.BasicCard;

public class DeckArrays {
    public ArrayList<BasicCard> actionCards;
    public ArrayList<BasicCard> baseCards;
    
    public DeckArrays() {
        actionCards = new ArrayList<BasicCard>(cards.Deck.UNIT_DECK_SIZE);
        baseCards = new ArrayList<BasicCard>(cards.Deck.BASE_DECK_SIZE);
    }
}
