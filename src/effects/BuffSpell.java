package effects;

public class BuffSpell extends AbstractSpell {

	public Buff buff;
	
	public BuffSpell(Buff b) {
		buff = b;
	}

	@Override
	public boolean validate() {
		return target != null;
	}

	@Override
	public void exequte(int playerNum) {
		this.playerNum = playerNum;
		target.applyBuff(buff);
	}

}
