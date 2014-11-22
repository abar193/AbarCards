package cards;

/**
 * Card used for unit tests.
 * @author Abar
 */
public class TestCard extends BasicCard {
	public int value;
	
	public TestCard(int v) {
		super("T"+v, "C");
		value = v;
		this.cost = 5;
		this.type = CardType.Spell;
		
	}
	
	@Override
	public String debugDisplay() {
		return "|TC" + Integer.toString(value) + "|";
	}
}
