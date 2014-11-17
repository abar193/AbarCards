package effects;

import units.Unit;

import java.util.ArrayList;

public class SpellContainer extends AbstractSpell {
	
	public ArrayList<AbstractSpell> spells;
	
	public SpellContainer(ArrayList<AbstractSpell> spells) {
		this.spells = spells;
	}

	public SpellContainer(AbstractSpell spell) {
		this.spells = new ArrayList<>();
		spells.add(spell);
	}
	
	public void add(AbstractSpell spell) {
		spells.add(spell);
	}
	
	@Override
	public boolean validate(int player, src.ProviderGameInterface currentGame) {
		return true;
	}

	@Override
	public void exequte(int playerNum, src.ProviderGameInterface currentGame) {
		this.playerNum = playerNum;
		for(AbstractSpell s : spells) {
			s.target = target;
			s.exequte(playerNum, currentGame);
		}

	}
	
	public String toString() {    
        String s = "SpellContainer {";
        for(AbstractSpell sp : spells) {
            s += sp.toString() + ",";
        }
        s += "}";
        return s;
    }
}
