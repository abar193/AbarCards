package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import effects.*;
import units.Unit;
import src.FieldSituation;
import units.Unit.Quality;

public class TestSpellEffects {

	FieldSituation fs;
	Unit[] units;
	
	@Before
    public void setUp() {
		fs = new FieldSituation();
		units = new Unit[5];
		for(int i = 0; i < 5; i++) {
			 units[0] = new Unit(new cards.UnitCard(i + 2, i + 2, 0, "", ""), 0);
			 fs.addUnit(units[0], 0);
		}
		src.Game.currentGame = new src.Game();
		src.Game.currentGame.applyFieldSituation(fs);
    }
	 
	@Test
	public void testAll() {
		BuffSpell bs = new BuffSpell(new Buff(BuffType.Hurt, 1));
		TargedetSpell ts = new TargedetSpell(new effects.AllUnitsTargeter(1, false), bs);
		ts.exequte(1);
		for(int i = 0; i < 5; i++) {
			assertEquals(i + 1, fs.allUnitFromOneSide(0, false).get(i).getCurrentHealth());
		}
	}
	
	@Test
	public void testSpellContainer() {
		BuffSpell bs0 = new BuffSpell(new Buff(BuffType.AddHealth, 1));
		BuffSpell bs1 = new BuffSpell(new Buff(BuffType.AddQuality, Quality.Taunt.getValue()));
		SpellContainer sc = new SpellContainer(bs0);
		sc.add(bs1);
		TargedetSpell ts = new TargedetSpell(new effects.AllUnitsTargeter(0, false), sc);
		ts.exequte(0);
		for(int i = 0; i < 5; i++) {
			assertEquals(i + 3, fs.allUnitFromOneSide(0, false).get(i).getCurrentHealth());
			assertEquals(true, fs.allUnitFromOneSide(0, false).get(i).hasQuality(Quality.Taunt));
		}
	}

}
