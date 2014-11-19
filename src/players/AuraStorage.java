package players;

import java.util.ArrayList;
import java.util.Iterator;

import effects.AuraEffect;
import effects.AuraType;
import units.FieldObject;
import units.Unit;

/**
 * Helper-class, who stores aura effects from PlayerData. Written to decrease PlayerData size.
 * @author Abar
 *
 */
public class AuraStorage {

	private ArrayList<AuraEffect> auras;
	private int[] modifiers;
	
	
	public AuraStorage() {
		auras = new ArrayList<AuraEffect>();
		modifiers = new int[3]; 
	}
	
	/**
	 * Decreases turn-based auras duration by 1, and removes those, whos duration <= 0
	 */
	public void removeOutdatedAuras() {
		for(int i = 0; i < auras.size(); i++) {
			if(auras.get(i).shouldBeRemovedIfTurnBased()) {
				auras.remove(i);
				i--;
			}
		}
	}
	
	/** 
	 * Adds aura to list of auras. 
	 * @param ae
	 */
	public void addAura(AuraEffect ae) {
		auras.add(ae);
	}
	
	/**
	 * Called when unit dies, to check if any unit-based auras should be removed 
	 * @param u
	 * @return true, if values has been changed
	 */
	public boolean unitDies(FieldObject u) {
		Iterator<AuraEffect> i = auras.iterator();
		boolean b = false;
		while(i.hasNext()) {
			if(u.equals(i.next().unit)) {
				i.remove();
				b = true;
			}
		}
		return b;
	}
	
	public int modifiersForType(AuraType t) {
		int r = 0;
		for(AuraEffect ae : auras) {
			if(ae.type.equals(t)) {
				r += ae.value;
			}
		}
		return r;
	}
	
	public void calculateModifiers() {
		modifiers = new int[3];
		modifiers[0] = modifiersForType(AuraType.UnitCost);
		modifiers[1] = modifiersForType(AuraType.UnitDamage);
		modifiers[2] = modifiersForType(AuraType.UnitHealth);
	}
	
	/**
	 * Returns calculated modifiers - calculateModifiers should be called first.
	 * @return Array of 3 int, where [0] - Cost, [1] - Damage, [2] - Health
	 */
	public int[] getModifiers() {
		return modifiers.clone();
	}
	
	/** 
	 * Returns size of auras array
	 * @return integer value.
	 */
	public int aurasCount() {
		return auras.size();
	}
}
