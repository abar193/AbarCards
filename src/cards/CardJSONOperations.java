package cards;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import decks.DeckPackReader;

public class CardJSONOperations {

	private static final String[] names = {"Neutrals", "Machines"};
	private static final String[] links = {"NeutralsDeck.xml", "MachinesDeck.xml"};
	
	public static CardJSONOperations instance = new CardJSONOperations();
	
	public BasicCard cardFromMap(Map<String, String> m) {
		try { 
			String name = m.get("Name");
			int cost = Integer.parseInt(m.get("Cost"));
			int deck = Integer.parseInt(m.get("Deck"));
			ArrayList<BasicCard> myDeck = singleAllDeck().get(deck);
			java.util.Iterator<BasicCard> i = myDeck.iterator();
			while(i.hasNext()) {
				BasicCard c = i.next();
				if(c.name.equals(name)) {
					if(c.cost == cost) 
						return c;
					else {
						System.err.format("Card %s has wrong cost. Expected %d, got %d", name, c.cost, cost);
					}
				}
			}
		} catch (NumberFormatException | NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Map<String, String> mapFromCard(BasicCard c) {
		Map<String, String> m = new LinkedHashMap<String, String>();
		m.put("Name", c.name);
		m.put("Cost", Integer.toString(c.cost));
		m.put("Deck", Integer.toString(c.deckNum));
		return m;
	}

	
	static ArrayList<ArrayList<BasicCard>> allDecks = null;
	public static synchronized ArrayList<ArrayList<BasicCard>> singleAllDeck() {
		if(allDecks == null) {
			allDecks = new ArrayList<ArrayList<BasicCard>>(links.length);
			DeckPackReader dpr = new DeckPackReader();
			for(String s : links) {
				allDecks.add(dpr.parseFile(s));
			}
		}
		
		return allDecks;
	}
}