package ui;

import java.util.ArrayList;
import java.util.List;

import cards.BasicCard;
import cards.CardType;
import cards.UnitCard;
import players.PlayerData;
import players.PlayerOpenData;
import src.FieldSituation;
import src.Game;
import units.TriggeringCondition;
import units.Unit;
import units.Unit.Quality;
import cards.SpellCard;

public class ConsoleVS implements VisualSystemInterface {

	private InputInterface input;
	FieldSituation latestSituation;
	PlayerData me;
	PlayerOpenData opponent;
	Game parent;
	
	public ConsoleVS(Game g) {
		parent = g;
	}

	@Override
	public void displayError(String m) {
		System.out.println("Error: " + m);

	}

	@Override
	public void displayMessage(String m) {
		System.out.println("> " + m);

	}

	@Override
	public void displayFieldState(PlayerData you, FieldSituation field,
			PlayerOpenData enemy) 
	{
		latestSituation = field;
		me = you;
		opponent = enemy;
		
		System.out.println(String.format("Enemy has %d/%d $ and %d health", 
				enemy.availableMana, enemy.totalMana, enemy.health));
		System.out.println(String.format("Enemy hand has %d cards, deck %d cards.",  
				enemy.handSize, enemy.deckSize));
		System.out.println("Field: ");
		for(int i = 0; i < 79; i++) {
			System.out.print("=");
		}
		System.out.println();
		int t = field.tauntUnitsForPlayer(enemy.playerNumber);
		
		displayFieldSide(field.allUnitFromOneSide(enemy.playerNumber, false), false, field, t);
		for(int i = 0; i < 39; i++) {
			System.out.print("- ");
		}
		System.out.println();
		displayFieldSide(field.allUnitFromOneSide(you.playerNumber, false), true, field, t);
		for(int i = 0; i < 79; i++) {
			System.out.print("=");
		}
		
		System.out.println(String.format("\nYou have %d/%d $ and %d health", 
				you.getAvailableMana(), you.getTotalMana(), you.getHealth()));
		System.out.println(String.format("Your deck has %d cards, your hand: ", you.getDeckSize()));
		
		
		ArrayList<BasicCard> cards = you.getHand();
		printCards(cards.subList(0, Math.min(5, cards.size())), 0, you);
		if(cards.size() > 5) {
			printCards(cards.subList(5, cards.size()), 5, you);
		}

	}
	
	/**
	 * Displays single side of a FieldSituation (draws ArrayList<Unit> like cards).
	 * @param arr array to display
	 */
	private void displayFieldSide(ArrayList<Unit> arr, boolean mySide, 
			FieldSituation latestSituation, int t) 
	{
		//
		for(int i = 0; i < arr.size(); i++) {
			if((mySide && !arr.get(i).canAttack()) || (!mySide && t > 0 && 
					(!arr.get(i).hasQuality(Quality.Taunt) 
					|| arr.get(i).hasQuality(Quality.Stealth)))) 
			{
				System.out.print(String.format("|%10s|", arr.get(i).myCard.name));
			} else {
				System.out.print(String.format("%d%10s|", i, arr.get(i).myCard.name));
			}
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
			BasicCard bc = arr.get(i).myCard;  
			if(bc.type == CardType.Unit) {
				System.out.print(String.format("|%2dd/%2dh%2d$|", arr.get(i).getCurrentDamage(),
						arr.get(i).getCurrentHealth(), arr.get(i).myCard.cost));
			}
		}
		System.out.println();
	}

	// For keyboard console input.
		private static final char[] qwerty = { 'q', 'w', 'e', 'r', 't', 'a', 's', 'd', 'f', 'g' };
	
	private void printCards(List<BasicCard> cards, int k, PlayerData me) {
		for(int i = 0; i < cards.size(); i++) {
			if(cards.get(i).cost <= me.getAvailableMana()) 
				System.out.print(String.format("%c%10s|", qwerty[i+k], cards.get(i).name));
			else 
				System.out.print(String.format("|%10s|", cards.get(i).name));
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
			BasicCard bc = cards.get(i);  
			if(bc.type == CardType.Unit) {
				System.out.print(String.format("|%2dd/%2dh%2d$|", ((UnitCard) bc).getDamage(),
						((UnitCard) bc).getHealth(), bc.cost));
			} else if(bc.type == CardType.Spell) {
				System.out.format("|Spell  %2d$|", bc.cost);
			}
		}
		System.out.println();
	}
	
	@Override
	public void displayAttack(Unit u1, Unit u2, boolean died1, boolean died2) {
		System.out.format("%s%s VS %s%s\n", (died1)? "X":"", u1.myCard.name, 
				(died2)? "X":"", u1.myCard.name);
	}

	@Override
	public void displayPower(Unit u, TriggeringCondition e) {
		System.out.format("%s used his power!\n", u.myCard.name);

	}

	@Override
	public void dispaySpell(SpellCard s, int player) {
		System.out.format("Player %d casts %s spell.\n", player, s.name);
	}

	@Override
	public void displayUnitDamage(Unit u, int damage) {
		System.out.format("Unit %s takes %d damage.\n", u.myCard.name, damage);
	}

	@Override
	public void setInputInterface(InputInterface i) {
		input = i;
		
	}
	
	@Override
	public void read() {
		ArrayList<Unit> myArmy = latestSituation.allUnitFromOneSide(me.playerNumber, false);
		//ArrayList<Unit> hisArmy = latestSituation.playerUnits.get(opponent.playerNumber);
		System.out.println("Type 'i' for info about units, or 'h' for help");
		
		int c = 0;
		boolean targeting = false;
		int selectedUnit = 0;
		while(c != 'z') { 
			try {
				c = System.in.read();
				if(Thread.interrupted()) {
					return;
				}
				myArmy = latestSituation.allUnitFromOneSide(me.playerNumber, false);

				if(!targeting) {
					switch(c) {
						case '0': case '1': case '2': case '3':
						case '4': case '5': case '6': case '7':
						case '8': case '9': 
							int i = c - '0';
							if(myArmy.size() > i && myArmy.get(i).canAttack()) {
								displayMessage("Select target: ");
								targeting = true;
								selectedUnit = i;
							} else {
								displayError("Invalid unit selected");
							}
							break;
						case 'q': case 'w': case 'e': case 'r': case 't':
						case 'a': case 's': case 'd': case 'f': case 'g':
							for(int j = 0; j < qwerty.length; j++) {
								if(c == qwerty[j]) {
									if(me.getHand().size() > j) {
										if(parent.canPlayCard(me.getHand().get(j), me.playerNumber)) {
											input.playUnitCard(j);
										} else {
											displayError("Can't play that");
										}
									}
								}
							}
							break;
						case 'i': 
							exploreUnits(); 
							break;
						case 'h': 
							displayHelp();
							break;
						case 'o':
							displayFieldState(me, latestSituation, opponent);
						case 10: case 13: 
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
								input.makeUnitsAttack(selectedUnit, tu);
							} else {
								displayError("Invalid target");
							}
							break;
						case 10: case 13: 
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
		}
		
	}
	
	private void displayHelp() {
		System.out.println("Help: \n" +
				"You may enter orders one at a time, or with a single line, but press enter.\n" +
				"Type your unit's number to select it for attack. \n" + 
				"Select target with his unit's number or type '-' to attack hero.\n" + 
				"To play your card press it's key (q..t + a..g)\n" +
				"Type 'i' to see info about all units with 'i' sign, or your cards.\n" +
				"Type 'o' to redraw field.");
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


	
}
