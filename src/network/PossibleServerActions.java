package network;

public enum PossibleServerActions {
	play, canPlayCard, playCard, attackIsValid, commitAttack, deck, endTurn, err;
	
	public static PossibleServerActions fromString(String s) {
		switch(s) {
			case "play":
				return play;
			case "canPlayCard":
				return canPlayCard;
			case "playCard":
				return playCard;
			case "attackIsValid":
				return attackIsValid;
			case "commitAttack":
				return commitAttack;
			case "deck":
				return deck;
			case "endTurn":
				return endTurn;
			default:
				return err;
		}
	}
}
