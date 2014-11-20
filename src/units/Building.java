package units;

import java.util.ArrayList;
import java.util.Map;

import src.ProviderGameInterface;
import cards.BasicCard;
import cards.BuildingCard;
import cards.UnitCard;

public class Building extends FieldObject {

    private int progress = 0;
    private BasicCard product = null;
    private BuildingCard buildingRef;
    
    public Building(BuildingCard c, int player, ProviderGameInterface currentGame) {
        this.card = c;
        buildingRef = c;
        this.player = player;
        this.currentGame = currentGame;
        
        currentHealth = card.getHealth();
        maxHealth = currentHealth;
        currentDamage = card.getDamage();
        powers = new ArrayList<UnitPower>();
        progress = 0;
        modHealth = 0;
        modDmg = 0;
    }

    @Override
    public void startTurn() {
        progress = Math.min(progress + 1, buildingRef.productionTime);
        if(progress == buildingRef.productionTime && product == null) {
            product = buildingRef.product; // TODO copy?
        }
    }

    @Override
    public void endTurn() {

    }

    @Override
    public Map<String, String> toMap() {
        
        return null;
    }
    
    public int getMaxProgress() {
        return buildingRef.productionTime;
    }
    
    public int getProgress() {
        return progress;
    }
    
    public boolean productAvailable() {
        return product != null;
    }
    
    public BasicCard takeProduct() {
        BasicCard res = product;
        product = null;
        progress = 0;
        return res;
    }

    @Override
    public String toString() {
        return String.format("B[%s, P%d/% D%d/%dH %s]", card.name, getProgress(), 
                getMaxProgress(), getCurrentDamage(), getCurrentHealth(), product.toString());
    }
}
