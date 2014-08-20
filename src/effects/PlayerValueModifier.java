package effects;

public enum PlayerValueModifier {
	AddAvailMana, AddUnavailMana, AddHealth, RemoveHealth;
	
	public static PlayerValueModifier fromString(String s) {
		switch (s) {
		case "AddAvailMana":
			return AddAvailMana;
		case "AddUnavailMana":
			return AddUnavailMana;
		case "AddHealth":
			return AddHealth;
		case "RemoveHealth":
			return RemoveHealth;
		default:
			return null;
		}
	}
}
