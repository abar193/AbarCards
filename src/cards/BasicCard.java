package cards;

/**
 * Prototype for all cards.
 * @author Abar
 *
 */
public abstract class BasicCard {
	
	public int cost;
	public CardType type;
	public String name; 
	public String description;
	
	/**
	 * Creates new instance of BasicCard
	 */
	public BasicCard(String name, String desc) {
		this.name = name;
		this.description = desc;
		cost = 0;
	}

	public BasicCard(String name, String desc, int cost) {
		this.name = name;
		this.description = desc;
		this.cost = cost;
	}
	
	public BasicCard(String name, String desc, int cost, CardType type) {
		this.name = name;
		this.description = desc;
		this.cost = cost;
		this.type = type;
	}
	
	public int getBasicCost() {
		return cost;
	}
	
	public CardType getCardType() {
		return type;
	}
	
	public String debugDisplay() {
		return "|BC|";
	}
}
