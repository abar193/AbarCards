package cards;

public class DevCard extends BasicCard {
    
    public enum DevType {
        InfiniteMana, Draw1Card, Draw5Cards;
    }
    
    public DevType devType;
    
    public void setType(String cardName) {
        type = CardType.Developer;
        switch(cardName) {
            case "Dev:InfiniteMana":
                devType = DevType.InfiniteMana;
                return;
            case "Dev:Draw1Card":
                devType = DevType.Draw1Card;
                return;
            case "Dev:Draw5Cards":
                devType = DevType.Draw5Cards;
                return;
            default:
                System.err.println("Unsupported dev card: " + cardName);
                return;
        }
    }
    
    public DevCard(String name, String desc) {
        super(name, desc);
        setType(name);
    }

    public DevCard(String name, String desc, int cost) {
        super(name, desc, cost);
        setType(name);
    }

    public DevCard(String name, String desc, int cost, int energyCost, CardType type) {
        super(name, desc, cost, energyCost, type);
        setType(name);
    }

}
