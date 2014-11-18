package cards;

public class BuildingCard extends UnitCard {

    /**
     * Shows how many turns it takes to finish product.
     */
    public int productionTime;
    /**
     * Shows how much does production advances with each turn.
     */
    public int turnProgress;
    /**
     * Building's product card.
     */
    public BasicCard product;
    
    public BuildingCard() {
        
    }

    public BuildingCard(int damage, int health, int cost, String name,
            String desc) 
    {
        super(damage, health, cost, name, desc);
        
    }
    
    public String toString() {
        return "Building \"" + name + "\" -> " + product.toString(); 
    }

}
