package units;

public class UnitFilter {

	public UnitFilterType type;
	public String value;
	
	public UnitFilter(UnitFilterType type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public UnitFilter(String type, String value) {
		this.type = UnitFilterType.fromString(type);
		this.value = value;
	}

}
