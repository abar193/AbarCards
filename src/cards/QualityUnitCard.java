package cards;

/**
 * Unit card with some qualities
 * @author Abar
 *
 */
public class QualityUnitCard extends UnitCard {
	
	public int qualities;
	
	public QualityUnitCard(int health, int damage, int cost, String name,
			String desc, int qualities) 
	{
		super(health, damage, cost, name, desc);
		this.qualities = qualities; 
	}

}
