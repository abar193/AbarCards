package units;

import java.util.ArrayList;
import java.util.Stack;

import cards.UnitCard;

/**
 * This class represents single object from the field, which can be attacked and take damage.
 * @author Abar
 *
 */
public class FieldObject {

    public UnitCard myCard;
    public int myPlayer;
    
    protected int qualities = 0;
    public int modDmg, modHealth, modQualities;
    protected int currentHealth;
    protected int maxHealth;
    protected int currentDamage;
    
    protected src.ProviderGameInterface currentGame;
    protected ArrayList<UnitPower> powers;
    
    public FieldObject() {
        
    }

    public void appyQualities(int q) {
        qualities = q;
    }
    
    public int getCurrentHealth() {
        return currentHealth + modHealth;
    }
    
    public int getCurrentDamage() {
        return currentDamage + modDmg;
    }
    
    /**
     * Heals unit. Method calls OnDamage event.
     * @param v value of the heal
     */
    public void heal(int v) {
        currentHealth = Math.min(currentHealth + v, maxHealth);
    }
    
    public void removeQuality(Quality q) {
        qualities = (0xFFFF ^ q.getValue()) & qualities;
    }
    
    /* Input */
    public void setQuality(Quality q) {
        qualities |= q.getValue();
    }
    
    public void setQuality(int i) {
        qualities |= i;
    }
    
    public boolean hasQuality(Quality q) {
        return ((qualities | modQualities) & q.getValue()) != 0;
    }
    
    /**
     * Applies damage to unit. Method calls OnDamage event.
     * @param d
     */
    public void damage(int d) {
        if(hasQuality(Quality.Shield)) {
            removeQuality(Quality.Shield);
        } else {
            currentHealth -= d;
            if(currentGame != null)
                currentGame.informAll(String.format("%s takes %d damage", 
                    "TODO", d));
            this.respondToEvent(TriggeringCondition.OnDamage, this);
        }
    }
    
    public void addPower(UnitPower p) {
        powers.add(p);
    }
    
    public boolean isDead() {
        return currentHealth <= 0;
    }
    
    public Stack<UnitPower> powersMatchingCondition(TriggeringCondition c) {
        Stack<UnitPower> s = new Stack<UnitPower>();
        for(UnitPower p : powers) {
            if(p.matchesCondition(c)) 
                s.push(p);
        }
        return s;
    }
    
    public boolean matchesFilter(UnitFilter f) {
        if(f == null) return true;
        
        int value;
        try {
            value = Integer.parseInt(f.value);
        } catch (NumberFormatException e) {
            value = 0;
        }
        
        switch(f.type) {
            case ClassEquals:
                return f.value.equals(myCard.cardClass);
            case DamageLess:
                return getCurrentDamage() < value;
            case DamageMore:
                return getCurrentDamage() > value;
            case HealthLess:
                return getCurrentHealth() < value;
            case HealthMore:
                return getCurrentHealth() > value;
            case IsHero:
                return false;
        }
        return false;
    }
    
    public void respondToEvent(TriggeringCondition e, FieldObject otherU) {
        if(e == TriggeringCondition.OnDamage) {
            if(currentGame != null)
                currentGame.passEventAboutUnit(this, TriggeringCondition.OnAllyDamage);
        }
        for(UnitPower p : powersMatchingCondition(e)) {
            if(p.filter == null || otherU.matchesFilter(p.filter)) {
                if(e != TriggeringCondition.Always)
                    currentGame.informAll(String.format("%s invokes his power", 
                            myCard.name));
                p.exequte(this, myPlayer, currentGame);
            }
        }
    }
}
