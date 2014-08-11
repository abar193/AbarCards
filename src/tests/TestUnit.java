package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import units.Unit;
import cards.UnitCard;
import units.Unit.Quality;
import cards.QualityUnitCard;

public class TestUnit {

	@Test
	public void testInit() {
		UnitCard uc = new UnitCard(4, 4, 4, "", "");
		assertNotNull(new Unit(uc));
		assertNotNull(new Unit(uc, units.Unit.Quality.Battlecry.getValue()));
	}

	@Test
	public void testBuffs() {
		UnitCard uc = new UnitCard(4, 5, 4, "", "");
		Unit u = new Unit(uc);
		assertEquals(false, u.hasQuality(Quality.Battlecry));
		assertEquals(false, u.hasQuality(Quality.Charge));
		assertEquals(false, u.hasQuality(Quality.Stealth));
		assertEquals(false, u.hasQuality(Quality.Taunt));
		u.setQuality(Quality.Battlecry);
		assertEquals(true, u.hasQuality(Quality.Battlecry));
		u.setQuality(Quality.Charge);
		assertEquals(true, u.hasQuality(Quality.Charge));
		u.setQuality(Quality.Stealth);
		assertEquals(true, u.hasQuality(Quality.Stealth));
		u.setQuality(Quality.Taunt);
		assertEquals(true, u.hasQuality(Quality.Taunt));
		assertEquals(true, u.hasQuality(Quality.Stealth));
		assertEquals(true, u.hasQuality(Quality.Charge));
		assertEquals(true, u.hasQuality(Quality.Battlecry));
		u.removeQuality(Quality.Charge);
		assertEquals(false, u.hasQuality(Quality.Charge));
		
		assertEquals(u.getCurrentDamage(), 5);
		assertEquals(u.getCurrentHealth(), 4);
		u.applyBuff(new effects.Buff(effects.BuffType.Damage, 5));
		assertEquals(u.getCurrentDamage(), 10);
		u.applyBuff(new effects.Buff(effects.BuffType.Health, 2));
		assertEquals(u.getCurrentHealth(), 6);
		u.applyBuff(new effects.Buff(effects.BuffType.Damage, -1));
		assertEquals(u.getCurrentDamage(), 9);
		u.applyBuff(new effects.Buff(effects.BuffType.Health, -3));
		assertEquals(u.getCurrentHealth(), 3);
		
		u.applyBuff(new effects.Buff(effects.BuffType.Silence, 0));
		assertEquals(false, u.hasQuality(Quality.Battlecry));
		assertEquals(false, u.hasQuality(Quality.Charge));
		assertEquals(false, u.hasQuality(Quality.Stealth));
		assertEquals(false, u.hasQuality(Quality.Taunt));
		assertEquals(5, u.getCurrentDamage());
		assertEquals(3, u.getCurrentHealth());
	}
	
	@Test
	public void testNewBuffs() {
		UnitCard uc = new UnitCard(1, 2, 3, "", "");
		Unit u = new Unit(uc);
		u.applyBuff(new effects.Buff(effects.BuffType.DamageSetTo, 15));
		assertEquals(15, u.getCurrentDamage());
		u.applyBuff(new effects.Buff(effects.BuffType.HealthSetTo, 7));
		assertEquals(7, u.getCurrentHealth());
	}
	
	@Test 
	public void testBuffedUnit() {
		int[] arr = {Quality.Battlecry.getValue(), 
				Quality.Charge.getValue(), 
				Quality.Stealth.getValue(), 
				Quality.Taunt.getValue(), 
				Quality.Battlecry.getValue() | Quality.Charge.getValue(), //  
				Quality.Stealth.getValue() | Quality.Taunt.getValue(), //
				Quality.Battlecry.getValue() | Quality.Taunt.getValue(), //
				Quality.Stealth.getValue() | Quality.Charge.getValue(),//
				Quality.Battlecry.getValue() | Quality.Stealth.getValue(), //
				Quality.Taunt.getValue() | Quality.Charge.getValue(), //
				Quality.Battlecry.getValue() | Quality.Charge.getValue() | Quality.Taunt.getValue()};
		Quality[][] controll = { {Quality.Battlecry}, 
				{Quality.Charge}, 
				{Quality.Stealth},
				{Quality.Taunt}, 
				{Quality.Battlecry, Quality.Charge}, 
				{Quality.Stealth, Quality.Taunt},
				{Quality.Battlecry, Quality.Taunt},
				{Quality.Stealth, Quality.Charge},
				{Quality.Battlecry, Quality.Stealth},
				{Quality.Taunt, Quality.Charge},
				{Quality.Battlecry, Quality.Charge, Quality.Taunt}
			};
		
		for(int i = 0; i < arr.length; i++) {
			QualityUnitCard qc = new QualityUnitCard(1, 1, 1, "", "", arr[i]);
			Unit u = new Unit(qc);
			for(Quality q:controll[i]) {
				assertEquals(true, u.hasQuality(q));
			}
		}
	}
	
}	


	
