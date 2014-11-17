package effects;

/**
 * Class used to change unit's properties like health, damage add qualities, or restore unit 
 * to it's default settings. 
 * @author Abar
 */
public class Buff {
	public BuffType type;
	public String svalue;
	public int value;
	
	public Buff(BuffType bt, String v) {
		type = bt;
		svalue = v;
	}
	
	// For tests
	public Buff(BuffType bt, int v) {
		type = bt;
		value = v;
	}
	
	public Buff(String s, String v) {
		type = BuffType.fromString(s);
		svalue = v;
	}
	
	public String toString() {    
        return "(" + type.toString() + ", " + svalue + ")"; 
    }
}
