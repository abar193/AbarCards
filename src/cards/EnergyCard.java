package cards;

public class EnergyCard extends BasicCard {

    public EnergyCard(String name, String desc) {
        super("Energy", "    Card");
        type = CardType.Energy;
    }

    public EnergyCard(String name, String desc, int cost) {
        super("Energy", "    Card", cost);
        type = CardType.Energy;
    }

    public EnergyCard(String name, String desc, int cost, int energyCost, CardType type) {
        super("Energy", "    Card", cost, energyCost, type);
        type = CardType.Energy;
    }

}
