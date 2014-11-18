package units;

/** Represents quality FieldObject may have like Taunt, Stealth, e.t.c. */
public enum Quality {
    Windfury(1), Stealth(2), Taunt(4), Charge(8), NoAttack(16), Shield(32);
    public final int value;

    private Quality(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    
    public String letter() {
        switch(this) {
            case Charge:
                return "C";
            case NoAttack:
                return "N";
            case Shield:
                return "Ds";
            case Stealth:
                return "S";
            case Taunt:
                return "T";
            case Windfury:
                return "W";
            default:
                return "ERR";
        }
        
    }
}
