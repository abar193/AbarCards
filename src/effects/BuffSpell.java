package effects;

public class BuffSpell extends AbstractSpell {

	public Buff buff;
	
	public BuffSpell(Buff b) {
		buff = b;
	}

	@Override
	public boolean validate(int player) {
		return target != null;
	}

	@Override
	public void exequte(int playerNum) {
		this.playerNum = playerNum;
		buff.value = CustomValueDecoder.decodeValue(buff.svalue, playerNum, target);
		target.applyBuff(buff);
	}

}
