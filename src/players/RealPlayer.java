package players;

import src.FieldSituation;
import src.Game;
import units.Unit;

import java.util.ArrayList;
import java.util.List;

import cards.*;

/**
 * Class for real player, who sits in front of his PC and plays that game (basically 
 * 		it's only me). Outputs information to console and recives input from there. 
 * @author Abar
 *
 */
public class RealPlayer implements PlayerInterface {

	private Game parent;
	
	private FieldSituation latestSituation;
	private PlayerData me;
	private PlayerOpenData opponent;
	private boolean redrawNeeded;
	private boolean inProgress;
	
	private ArrayList<String> messages; 
	private boolean myTurn;
	
	public RealPlayer() {
		messages = new ArrayList<String>();
		myTurn = false;
	}
	
	@Override
	public void reciveInfo(PlayerData you, FieldSituation field, PlayerOpenData enemy) {
		me = you;
		latestSituation = field;
		opponent = enemy;
		
		if(inProgress) {
			redrawNeeded = true;
			return;
		} else {
			redrawNeeded = false;
		}
		
		System.out.println(String.format("Enemy has %d/%d $ and %d health", 
				enemy.availableMana, enemy.totalMana, enemy.health));
		System.out.println(String.format("Enemy hand has %d cards, deck %d cards.",  
				enemy.handSize, enemy.deckSize));
		System.out.println("Field: ");
		for(int i = 0; i < 79; i++) {
			System.out.print("=");
		}
		System.out.println();
		displayFieldSide(field.allUnitFromOneSide(enemy.playerNumber, false));
		for(int i = 0; i < 39; i++) {
			System.out.print("- ");
		}
		System.out.println();
		displayFieldSide(field.allUnitFromOneSide(you.playerNumber, false));
		for(int i = 0; i < 79; i++) {
			System.out.print("=");
		}
		
		System.out.println(String.format("\nYou have %d/%d $ and %d health", 
				you.getAvailableMana(), you.getTotalMana(), you.getHealth()));
		System.out.println(String.format("Your deck has %d cards, your hand: ", you.getDeckSize()));
		
		
		ArrayList<BasicCard> cards = you.getHand();
		printCards(cards.subList(0, Math.min(5, cards.size())), 0);
		if(cards.size() > 5) {
			printCards(cards.subList(5, cards.size()), 5);
		}
	}
	
	/**
	 * Displays single side of a FieldSituation (draws ArrayList<Unit> like cards).
	 * @param arr array to display
	 */
	private void displayFieldSide(ArrayList<Unit> arr) {
		for(int i = 0; i < arr.size(); i++) {
			System.out.print(String.format("%d%10s|", i, arr.get(i).myCard.name));
		}
		System.out.println();
		for(int i = 0; i < arr.size(); i++) {
			String s;
			String d = arr.get(i).myCard.fullDescription;
			if(d != null && d != "") {
				s = String.format("i%10s|", arr.get(i).descriptionString());
			} else {
				s = String.format("|%10s|", arr.get(i).descriptionString());
			}
			System.out.print(s);
		}
		System.out.println();
		for(int i = 0; i < arr.size(); i++) {
			System.out.print(String.format("|Cost: %4d|", arr.get(i).myCard.cost));
		}
		System.out.println();
		for(int i = 0; i < arr.size(); i++) {
			BasicCard bc = arr.get(i).myCard;  
			if(bc.type == CardType.Unit) {
				System.out.print(String.format("|%2d/%3d d/h|", arr.get(i).getCurrentDamage(),
						arr.get(i).getCurrentHealth()));
			}
		}
		System.out.println();
	}

	
	private void printCards(List<BasicCard> cards, int k) {
		for(int i = 0; i < cards.size(); i++) {
			System.out.print(String.format("%c%10s|", qwerty[i+k], cards.get(i).name));
		}
		System.out.println();
		for(int i = 0; i < cards.size(); i++) {
			String s;
			String d = cards.get(i).fullDescription;
			if(d != null && d != "") {
				s = String.format("i%10s|", cards.get(i).description);
			} else {
				s = String.format("|%10s|", cards.get(i).description);
			}
			System.out.print(s);
		}
		System.out.println();
		for(int i = 0; i < cards.size(); i++) {
			System.out.print(String.format("|Cost: %3d$|", cards.get(i).cost));
		}
		System.out.println();
		for(int i = 0; i < cards.size(); i++) {
			BasicCard bc = cards.get(i);  
			if(bc.type == CardType.Unit) {
				System.out.print(String.format("|%2d/%3d d/h|", ((UnitCard) bc).getDamage(),
						((UnitCard) bc).getHealth()));
			} else if(bc.type == CardType.Spell) {
				System.out.print("|     Spell|");
			}
		}
		System.out.println();
	}
	
	/**
	 * Prints message to console.
	 */
	@Override
	public void reciveAction(String m) {
		if(!myTurn) {
			messages.add("> " + m);
		}
		System.out.println("> " + m);
	}

	// For keyboard console input.
	private static final char[] qwerty = { 'q', 'w', 'e', 'r', 't', 'a', 's', 'd', 'f', 'g' };
	
	@Override
	public void run() {
		ArrayList<Unit> myArmy = latestSituation.allUnitFromOneSide(me.playerNumber, false);
		//ArrayList<Unit> hisArmy = latestSituation.playerUnits.get(opponent.playerNumber);
		System.out.println("Type 'i' for info about units, or 'h' for help");
		myTurn = true;
		int c = 0;
		boolean targeting = false;
		int selectedUnit = 0;
		while(c != 'z') { 
			try {
				c = System.in.read();
				if(Thread.interrupted()) {
					myTurn = false;
					messages = new ArrayList<String>();
					return;
				}
				myArmy = latestSituation.allUnitFromOneSide(me.playerNumber, false);
				inProgress = true;
				if(!targeting) {
					switch(c) {
						case '0': case '1': case '2': case '3':
						case '4': case '5': case '6': case '7':
						case '8': case '9': 
							int i = c - '0';
							if(myArmy.size() > i && myArmy.get(i).canAttack()) {
								reciveAction("Select target: ");
								targeting = true;
								selectedUnit = i;
							} else {
								reciveAction("Invalid unit selected");
							}
							break;
						case 'q': case 'w': case 'e': case 'r': case 't':
						case 'a': case 's': case 'd': case 'f': case 'g':
							for(int j = 0; j < qwerty.length; j++) {
								if(c == qwerty[j]) {
									if(me.getHand().size() > j) {
										if(parent.canPlayCard(me.getHand().get(j), me.playerNumber)) {
											parent.playCard(me.getHand().get(j), me.playerNumber);
										} else {
											reciveAction("Can't play that");
										}
									}
								}
							}
							break;
						case 'i': 
							exploreUnits(); 
							break;
						case 'l': 
							restoreMessages();break;
						case 'h': 
							displayHelp();
							break;
						case 'o':
							inProgress = false;
							reciveInfo(me, latestSituation, opponent);
							inProgress = true;
						case 10: case 13: 
							inProgress = false;
							break;
						default: break;
					}
				} else {
					switch(c) {
						case '0': case '1': case '2': case '3':
						case '4': case '5': case '6': case '7':
						case '8': case '9': case '-': 
							targeting = false;
							
							int tu, tp = opponent.playerNumber;
							tu = (c == '-') ? -1 : c - '0';
							
							if(parent.attackIsValid(selectedUnit, tu, me.playerNumber, tp)) {
								parent.commitAttack(selectedUnit, tu, me.playerNumber, tp);
							} else {
								reciveAction("Invalid target");
							}
							break;
						case 10: case 13: 
							inProgress = false;
							break;
						default: 
							targeting = false; 
							break;
					}
				}
			} catch (java.io.IOException e) {
				System.out.println("Input error");
				return;
			}
			
			if(redrawNeeded) {
				this.reciveInfo(me, latestSituation, opponent);
			}
		}
		
		myTurn = false; 
		messages = new ArrayList<String>();
	}

	/**
	 * Prints information about every single unit, or handed card with filled fullDescription
	 */
	private void exploreUnits() {
		for(int i = 0; i < 2; i++) {
			for(Unit u: latestSituation.allUnitFromOneSide((opponent.playerNumber + i) % 2, false)) {
				if(u.myCard.fullDescription != null && u.myCard.fullDescription != "") {
					System.out.format("%s - %s \n", u.myCard.name, u.myCard.fullDescription);
				}
			}
		}
		
		for(BasicCard c : me.getHand()) {
			if(c.fullDescription != null && c.fullDescription != "") {
				System.out.format("%s: %s \n", c.name, c.fullDescription);
			}
		}
	}
	
	private void restoreMessages() {
		for(String s: messages) {
			System.out.println(s);
		}
	}
	
	private void displayHelp() {
		System.out.println("Help: \n" +
				"You may enter orders one at a time, or with a single line, but press enter.\n" +
				"Type your unit's number to select it for attack. \n" + 
				"Select target with his unit's number or type '-' to attack hero.\n" + 
				"To play your card press it's key (q..t + a..g)\n" +
				"Type 'i' to see info about all units with 'i' sign, or your cards.\n" +
				"Type 'l' to show messages from last turn.\n" +
				"Type 'o' to redraw field.");
	}

	@Override
	public void setParentGame(Game g) {
		parent = g;
	}

	@Override
	public Unit selectTarget() {
		int p, u;
		System.out.print("Enter player to target (0 - you, 1 - he): ");
		try {
			p = System.in.read();
			while(p != '0' && p != '1') {
				p = System.in.read();
			}
		} catch(java.io.IOException e){
			p = '0'; 
		}
		p -= '0';
		System.out.print("Enter unit to target: ");
		try {
			u = 0;
			while(u < '0' || p > '9') {
				u = System.in.read();
				if(u - '0' >= latestSituation.allUnitFromOneSide((me.playerNumber + p)%2, false).size()) {
					System.out.println("Can't target that! #" + Integer.toString(u-'0'));
					u = 0;
				}
			}
			u -= 48;
		} catch(java.io.IOException e){
			u = 0; 
		}
		
		return latestSituation.allUnitFromOneSide((me.playerNumber + p) % 2, false).get(u);
	}

}
