package effects;

import org.xml.sax.Attributes;

/** 
 * Used by SpellXMLBuilder, this class produces targeters from their XML description
 * @author Abar
 *
 */
public class TargeterBuilder {

	/**
	 * Creates and returns appropriate targeter 
	 * @param s getValue("targeter") for TargedetSpell
	 * @param att other attributes
	 * @return created targeter
	 */
	public static Targeter fromName(String s, Attributes att) {
		switch(s) {
		case "player":
			return new PlayerTargeter();
		case "neighbor":
			int offset = Integer.parseInt(att.getValue("o"));
			return new NeighborTargeter(offset);
		case "random": {
			int acept = Integer.parseInt(att.getValue("acept"));
			int targets = Integer.parseInt(att.getValue("targets"));
			int repeats = Integer.parseInt(att.getValue("repeats"));
			return new RandomTargeter(acept, targets, (repeats == 1));
		}
		case "all": {
			int acept = Integer.parseInt(att.getValue("acept"));
			return new AllUnitsTargeter(acept);
		}
		default: 
			return null;
		}
	}

}
