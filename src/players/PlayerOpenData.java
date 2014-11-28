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
	public int actionSetSize, baseSetSize, handSize;
	public int playerNumber;	
	
	public Map<String, String> toMap() {
		Map<String, String> m = new LinkedHashMap<String, String>();
		m.put("TotalMana", Integer.toString(totalMana));
		m.put("AvailableMana", Integer.toString(availableMana));
		m.put("Health", Integer.toString(health));
		m.put("ActionSetSize", Integer.toString(actionSetSize));
		m.put("BaseSetSize", Integer.toString(baseSetSize));
		m.put("PlayerNumber", Integer.toString(playerNumber));
		m.put("HandSize", Integer.toString(handSize));
		return m;
	}
	
	public PlayerOpenData() {
	}
	
	public PlayerOpenData(Map<String, String> m) {
		handSize 		= Integer.parseInt((String) m.get("HandSize"));
		playerNumber 	= Integer.parseInt((String) m.get("PlayerNumber"));
		actionSetSize	= Integer.parseInt((String) m.get("ActionSetSize"));
		baseSetSize		= Integer.parseInt((String) m.get("BaseSetSize"));
		health 			= Integer.parseInt((String) m.get("Health"));
		availableMana 	= Integer.parseInt((String) m.get("AvailableMana"));
		totalMana 		= Integer.parseInt((String) m.get("TotalMana"));	
	}
	
}
