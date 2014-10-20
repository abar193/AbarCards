package effects;

import java.util.ArrayList;

import src.ProviderGameInterface;

public class ConditionSpell extends AbstractSpell {

    private Condition condition;
    private ArrayList<AbstractSpell>spells = new ArrayList<AbstractSpell>(2);
    
    public ConditionSpell() {
        
    }
    
    public void addCondition(Condition c) {
        condition = c;
    }
    
    public void addSpell(AbstractSpell s) {
        spells.add(s);
    }

    @Override
    public boolean validate(int player, ProviderGameInterface currentGame) {
        if(condition.validate(player, currentGame)) {
            return spells.get(0).validate(player, currentGame);
        } else {
            return spells.get(1).validate(player, currentGame);
        }
    }

    @Override
    public void exequte(int player, ProviderGameInterface currentGame) {
        if(condition.validate(player, currentGame)) {
            spells.get(0).target = target;
            spells.get(0).exequte(player, currentGame);
        } else {
            spells.get(1).target = target;
            spells.get(1).exequte(player, currentGame);
        }
    }

}
