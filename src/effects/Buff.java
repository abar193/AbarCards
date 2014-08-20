package effects;

/**
 * Class used to change unit's properties like health, damage add qualities, or restore unit 
 * to it's default settings. 
 * @author Abar
 */
public class Buff {
	public BuffType type;
	public int value;
	
	public Buff(BuffType bt, int v) {
		type = bt;
		value = v;
	}
	
	public Buff(String s, int v) {
		type = BuffType.fromString(s);
		value = v;
	}
}
