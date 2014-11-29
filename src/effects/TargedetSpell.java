package effects;

import java.util.ArrayList;

import units.FieldObject;
import units.UnitFilter;

public class TargedetSpell extends AbstractSpell {

	Targeter targeter;
	public AbstractSpell spell;
	
	public TargedetSpell(Targeter targ, AbstractSpell s) {
		targeter = targ;
		spell = s;
	}

	@Override
	public boolean validate(int player, src.ProviderGameInterface currentGame) {
		this.playerNum = player;
		if(targeter.hasTargets(playerNum, target, currentGame)) {
		    if(spell.required) {
    		    Targeter mytarg;
    		    if(targeter instanceof RandomTargeter) {
    		        mytarg = new AllUnitsTargeter(((RandomTargeter) targeter).aceptPlayers, 
    		                ((RandomTargeter) targeter).aceptBuildings);
    		    } else if(targeter instanceof PlayerTargeter) {
    		        return targeter.hasTargets(player, target, currentGame);
    		    } else mytarg = targeter;
    		    for(FieldObject u : mytarg.selectTargets(player, target, currentGame)) {
    		        spell.target = u;
    		        if(spell.validate(player, currentGame)) return true;
    		    }
		    } else return true;
		} 
		return !required;
	}

	@Override
	public void exequte(int playerNum, src.ProviderGameInterface currentGame) {
		this.playerNum = playerNum;
		if(!targeter.hasTargets(playerNum, target, currentGame)) {
		    return;
		}
		ArrayList<FieldObject> units = targeter.selectTargets(playerNum, target, currentGame);
		if(units != null) {
			for(FieldObject u : units) {
				spell.target = u;
				spell.exequte(playerNum, currentGame);
			}
		}
	}
	
	@Override
	public void setFilter(UnitFilter f) {
		targeter.setFilter(f);
		super.setFilter(f);
	}
	
	public String toString() {    
        return "TargedetSpell > " + targeter.toString() + " : " + spell.toString(); 
    }
}
