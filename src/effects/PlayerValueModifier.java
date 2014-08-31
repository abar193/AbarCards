package effects;

public enum PlayerValueModifier {
	AddAvailMana, AddUnavailMana, AddHealth, RemoveHealth, PullCard;
	
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
		case "PullCard":
			return PullCard;
		default:
			return null;
		}
	}
}
