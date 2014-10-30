package players;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores player's open data like his health, mana or card's number. 
 * Safe to show to an opponent, or the entire world. 
 * @author Abar
 */
public class PlayerOpenData {
	public int health, totalMana, availableMana;
	public int deckSize, handSize;
	public int playerNumber;	
	
	public Map<String, String> toMap() {
		Map<String, String> m = new LinkedHashMap<String, String>();
		m.put("TotalMana", Integer.toString(totalMana));
		m.put("AvailableMana", Integer.toString(availableMana));
		m.put("Health", Integer.toString(health));
		m.put("DeckSize", Integer.toString(deckSize));
		m.put("PlayerNumber", Integer.toString(playerNumber));
		m.put("HandSize", Integer.toString(handSize));
		return m;
	}
	
	public PlayerOpenData() {
	}
	
	public PlayerOpenData(Map<String, String> m) {
		handSize 		= Integer.parseInt((String) m.get("HandSize"));
		playerNumber 	= Integer.parseInt((String) m.get("PlayerNumber"));
		deckSize 		= Integer.parseInt((String) m.get("DeckSize"));
		health 			= Integer.parseInt((String) m.get("Health"));
		availableMana 	= Integer.parseInt((String) m.get("AvailableMana"));
		totalMana 		= Integer.parseInt((String) m.get("TotalMana"));	
	}
	
}
