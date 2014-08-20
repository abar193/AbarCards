package effects;

/**
 * Enum storing possible BuffTypes.
 * @author Abar
 *
 */
public enum BuffType {
	AddHealth, AddDamage, AddQuality, Silence, HealthSetTo, DamageSetTo, Hurt, Heal, Kill;
	
	public static BuffType fromString(String s) {
		switch (s) {
		case "AddHealth":
			return AddHealth;
		case "AddDamage":
			return AddDamage;
		case "AddQuality":
			return AddQuality;
		case "Silence":
			return Silence;
		case "HealthSetTo":
			return HealthSetTo;
		case "DamageSetTo":
			return DamageSetTo;
		case "Hurt":
			return Hurt;
		case "Heal":
			return Heal;
		case "Kill":
			return Kill;
		default:
			System.out.println("Error! Unknown buff: " + s);
			break;
		}
		return null;
	}
}
