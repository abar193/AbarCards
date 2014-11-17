package effects;

import org.w3c.dom.Element;
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
    		case "player": {
    			boolean aceptHero = (att.getValue("hero") == null) ? false : 
    				att.getValue("hero").equals("1");
    			return new PlayerTargeter(aceptHero);
    		}
    		case "neighbor":
    			int offset = Integer.parseInt(att.getValue("o"));
    			return new NeighborTargeter(offset);
    		case "random": {
    			int acept = Integer.parseInt(att.getValue("acept"));
    			boolean aceptHero = (att.getValue("hero") == null) ? false : 
    				att.getValue("hero").equals("1");
    			int targets = Integer.parseInt(att.getValue("targets"));
    			int repeats = Integer.parseInt(att.getValue("repeats"));
    			return new RandomTargeter(acept, targets, (repeats == 1), aceptHero);
    		}
    		case "all": {
    			int acept = Integer.parseInt(att.getValue("acept"));
    			boolean aceptHero = (att.getValue("hero") == null) ? false : 
    				att.getValue("hero").equals("1"); 
    			boolean excludeSelf = (att.getValue("excludeSelf") == null) ? false : 
    				att.getValue("excludeSelf").equals("1");
    			AllUnitsTargeter tmp = new AllUnitsTargeter(acept, aceptHero);
    			tmp.excludeSelf = excludeSelf;
    			return tmp;
    		}
    		default: 
    			return null;
		}
	}
	
	public static Targeter fromDOMElements(String s, Element e) {
        switch(s) {
            case "player": {
                boolean aceptHero = (e.getAttribute("hero") == null) ? false : 
                    e.getAttribute("hero").equals("1");
                return new PlayerTargeter(aceptHero);
            }
            case "neighbor":
                int offset = Integer.parseInt(e.getAttribute("o"));
                return new NeighborTargeter(offset);
            case "random": {
                int acept = Integer.parseInt(e.getAttribute("acept"));
                boolean aceptHero = (e.getAttribute("hero") == null) ? false : 
                    e.getAttribute("hero").equals("1");
                int targets = Integer.parseInt(e.getAttribute("targets"));
                int repeats = Integer.parseInt(e.getAttribute("repeats"));
                return new RandomTargeter(acept, targets, (repeats == 1), aceptHero);
            }
            case "all": {
                int acept = Integer.parseInt(e.getAttribute("acept"));
                boolean aceptHero = (e.getAttribute("hero") == null) ? false : 
                    e.getAttribute("hero").equals("1"); 
                boolean excludeSelf = (e.getAttribute("excludeSelf") == null) ? false : 
                    e.getAttribute("excludeSelf").equals("1");
                AllUnitsTargeter tmp = new AllUnitsTargeter(acept, aceptHero);
                tmp.excludeSelf = excludeSelf;
                return tmp;
            }
            default: 
                return null;
        }
    }

}
