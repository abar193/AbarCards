package units;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

import cards.UnitCard;

/**
 * This class represents single object from the field, which can be attacked and take damage.
 * @author Abar
 *
 */
public abstract class FieldObject {

    public UnitCard card;
    public int player;
    
    protected int qualities = 0;
    public int modDmg, modHealth, modQualities;
    protected int currentHealth;
    protected int maxHealth;
    protected int currentDamage;
    
    protected src.ProviderGameInterface currentGame;
    protected ArrayList<UnitPower> powers;
    
    public FieldObject() {
        
    }

    public abstract void startTurn();
    public abstract void endTurn();
    public abstract Map<String, String> toMap();
    
    public void appyQualities(int q) {
        qualities = q;
    }
    
    /* * * Basics * * */
    /** Returns current health with all modifications. */
    public int getCurrentHealth() {
        return currentHealth + modHealth;
    }

    /** Returns current damage with all modifications. */
    public int getCurrentDamage() {
        return currentDamage + modDmg;
    }

    /** Checks if the object should be removed from the field. */
    public boolean isDead() {
        return currentHealth <= 0;
    }
    
    /* * * Qualities * * */
    public void setQuality(Quality q) {
        qualities |= q.getValue();
    }
    
    public void setQuality(int i) {
        qualities |= i;
    }

    public boolean hasQuality(Quality q) {
        return ((qualities | modQualities) & q.getValue()) != 0;
    }
    
    public void removeQuality(Quality q) {
        qualities = (0xFFFF ^ q.getValue()) & qualities;
    }

    /* * * Powers * * */
    public void addPower(UnitPower p) {
        powers.add(p);
    }
    
    public Stack<UnitPower> powersMatchingCondition(TriggeringCondition c) {
        Stack<UnitPower> s = new Stack<UnitPower>();
        for(UnitPower p : powers) {
            if(p.matchesCondition(c)) 
                s.push(p);
        }
        return s;
    }

    /* * * Input * * */
    public void applyBuff(effects.Buff b) {
        switch (b.type) {
            case AddDamage:
                currentDamage += b.value;
                break;
            case AddHealth:
                currentHealth += b.value;
                maxHealth += b.value;
                break;
            case AddQuality:
                setQuality(b.value);
                break;
            case Silence:
                currentHealth = Math.min(card.getHealth(), currentHealth);
                maxHealth = card.getHealth();
                currentDamage = card.getDamage();
                powers = new ArrayList<UnitPower>();
                qualities = 0;
                break;
            case DamageSetTo:
                currentDamage = b.value;
                break;
            case HealthSetTo:
                currentHealth = b.value;
                break;
            case Hurt: 
                damage(b.value);
                break;
            case Heal:
                heal(b.value);
                break;
            case Kill:
                damage(getCurrentHealth());
                break;
            case ModDmg:
                modDmg += b.value;
                break;
            case ModHealth:
                modHealth += b.value;
                break;
            case ModQuality:
                modQualities |= b.value;
                break;
            default:
                System.out.println("Unit: Unknown buff " + b.type.toString());
                break;
        }
    }
    
    /**
     * Heals unit. Method calls OnDamage event.
     * @param v value of the heal
     */
    public void heal(int v) {
        currentHealth = Math.min(currentHealth + v, maxHealth);
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
                return f.value.equals(card.cardClass);
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
                            card.name));
                p.exequte(this, player, currentGame);
            }
        }
    }
    
    public String descriptionString() {
        String s = "";
        for(Quality q : Quality.values()) {
            if(hasQuality(q)) {
                s += q.letter();
            }
        }
        if(s.equals("")) 
            s = card.description;
        else s = "<" + s + ">";
        return s;

    }

}
