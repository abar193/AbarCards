package network;

public enum PossibleServerActions {
	play, canPlayCard, playCard, attackIsValid, commitAttack, deck, endTurn, err, selectTarget,
	cancelSearchGame, quitGame;
	
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
			case "selectTarget": 
				return selectTarget;
			case "cancelSearchGame":
			    return cancelSearchGame;
			case "quitGame":
			    return quitGame;
			default:
				return err;
		}
	}
}
