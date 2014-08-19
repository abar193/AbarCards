package effects;

import units.Unit;
import java.util.ArrayList;

public class SpellContainer extends AbstractSpell {
	
	public ArrayList<AbstractSpell> spells;
	
	public SpellContainer(Unit t, int p, ArrayList<AbstractSpell> spells) {
		super(t, p);
		this.spells = spells;
	}

	public SpellContainer(Unit t, int p, AbstractSpell spell) {
		super(t, p);
		this.spells = new ArrayList<>();
		spells.add(spell);
	}
	
	public void add(AbstractSpell spell) {
		spells.add(spell);
	}
	
	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public void exequte() {
		for(AbstractSpell s : spells) {
			s.target = target;
			s.exequte();
		}

	}

}
