package effects;

public class BuffSpell extends AbstractSpell {

	public Buff buff;
	
	public BuffSpell(Buff b) {
		buff = b;
	}

	@Override
	public boolean validate(int player, src.ProviderGameInterface currentGame) {
		return target != null;
	}

	@Override
	public void exequte(int playerNum, src.ProviderGameInterface currentGame) {
		this.playerNum = playerNum;
		buff.value = CustomValueDecoder.decodeValue(buff.svalue, playerNum, target, currentGame);
		target.applyBuff(buff);
	}
	
	public String toString() {    
        return "Buff " + buff.toString(); 
    }
}
