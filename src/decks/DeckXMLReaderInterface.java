package decks;

import java.util.ArrayList;

import cards.BasicCard;

public interface DeckXMLReaderInterface {
    public ArrayList<BasicCard> parseFile(String filename);
}
