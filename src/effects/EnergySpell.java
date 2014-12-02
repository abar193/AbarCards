package effects;

import src.ProviderGameInterface;

public class EnergySpell extends AbstractSpell {

    private int price;
    public AbstractSpell spell;
    
    public EnergySpell(int price) {
        this.price = price;
    }

    @Override
    public boolean validate(int player, ProviderGameInterface currentGame) {
        return currentGame.getResourceForPlayer(player, true) > price && 
                (spell.validate(player, currentGame) || !spell.required);
    }

    @Override
    public void exequte(int player, ProviderGameInterface currentGame) {
        if(currentGame.drainResourceForPlayer(player, true, price)) {
            spell.target = target;
            spell.exequte(player, currentGame);
        }
    }

}
