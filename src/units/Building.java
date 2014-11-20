package units;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONValue;

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
        Map<String, String> m = new LinkedHashMap<String, String>();
        m.put("MyCard", JSONValue.toJSONString(cards.CardJSONOperations.instance.mapFromCard(card)));
        m.put("ModDmg", Integer.toString(modDmg));
        m.put("ModHealth", Integer.toString(modHealth));
        m.put("ModQualities", Integer.toString(modQualities));
        m.put("MyPlayer", Integer.toString(player));
        m.put("CurrentHealth", Integer.toString(currentHealth));
        m.put("MaxHealth", Integer.toString(maxHealth));
        m.put("CurrentDamage", Integer.toString(currentDamage));
        m.put("Qualities", Integer.toString(qualities));
        m.put("Progress", Integer.toString(progress));
        return m;
    }
    
    public Building(Map m, src.ProviderGameInterface currentGame) {
        try { 
            card = (BuildingCard) cards.CardJSONOperations.instance.cardFromMap((Map)m.get("MyCard"));
        } catch(ClassCastException e) {
            card = (BuildingCard) cards.CardJSONOperations.instance.
                    cardFromMap((Map)JSONValue.parse((String)m.get("MyCard")));
        }
        buildingRef = (BuildingCard) card;
        modDmg = Integer.parseInt((String) m.get("ModDmg"));
        modHealth = Integer.parseInt((String) m.get("ModHealth"));
        modQualities = Integer.parseInt((String) m.get("ModQualities"));
        player = Integer.parseInt((String) m.get("MyPlayer"));
        currentHealth = Integer.parseInt((String) m.get("CurrentHealth"));
        maxHealth = Integer.parseInt((String) m.get("MaxHealth"));
        currentDamage = Integer.parseInt((String) m.get("CurrentDamage"));
        qualities = Integer.parseInt((String) m.get("Qualities"));
        progress = Integer.parseInt((String) m.get("Progress"));
        if(progress >= buildingRef.productionTime) 
            product = buildingRef.product;
        this.currentGame = currentGame;
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
    
    public int productCost() {
        return buildingRef.product.cost;
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
