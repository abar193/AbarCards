package players;

import src.FieldSituation;
import src.Game;
import units.Unit;

import java.util.ArrayList;

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
	
	@Override
	public void reciveInfo(PlayerData you, FieldSituation field, PlayerOpenData enemy) {
		me = you;
		latestSituation = field;
		opponent = enemy;
		System.out.println(String.format("Enemy has %d/%d mana and %d health", 
				enemy.availableMana, enemy.totalMana, enemy.health));
		System.out.println(String.format("Enemy hand has %d cards, deck %d cards.",  
				enemy.handSize, enemy.deckSize));
		System.out.println("Field: ");
		for(int i = 0; i < 80; i++) {
			System.out.print("=");
		}
		System.out.println();
		displayFieldSide(field.playerUnits.get(enemy.playerNumber));
		for(int i = 0; i < 40; i++) {
			System.out.print("- ");
		}
		System.out.println();
		displayFieldSide(field.playerUnits.get(you.playerNumber));
		for(int i = 0; i < 80; i++) {
			System.out.print("=");
		}
		
		System.out.println(String.format("\nYou have %d/%d mana and %d health", 
				you.getAvailableMana(), you.getTotalMana(), you.getHealth()));
		System.out.println(String.format("Your deck has %d cards, your hand: ", you.getDeckSize()));
		
		int i = 0;
		for(BasicCard bc : you.getHand()) {
			System.out.print(String.format("%c%10s, %9s, %5s Cost: %2d|", qwerty[i], bc.name, bc.description, 
					bc.type.toString(), bc.cost));
			if(++i % 2 == 0) System.out.println();
		}
		System.out.println();
	}
	
	/**
	 * Displays single side of a FieldSituation (draws ArrayList<Unit> like cards).
	 * @param arr array to display
	 */
	private void displayFieldSide(ArrayList<Unit> arr){
		for(int i = 0; i < arr.size(); i++) {
			System.out.print(String.format("%d%10s|", i, arr.get(i).myCard.name));
		}
		System.out.println();
		for(int i = 0; i < arr.size(); i++) {
			System.out.print(String.format("|%10s|", arr.get(i).myCard.description));
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

	/**
	 * Prints message to console.
	 */
	@Override
	public void reciveAction(String m) {
		System.out.println("> " + m);
	}

	// For keyboard console input.
	private static final char[] qwerty = { 'q', 'w', 'e', 'r', 't', 'a', 's', 'd', 'f', 'g' };
	
	@Override
	public void makeTurn() {
		ArrayList<Unit> myArmy = latestSituation.playerUnits.get(me.playerNumber);
		//ArrayList<Unit> hisArmy = latestSituation.playerUnits.get(opponent.playerNumber);
		
		int c = 0;
		boolean targeting = false;
		int selectedUnit = 0;
		while(c != 'z') { 
			try {
				c = System.in.read();
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
						case 10: case 13: break;
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


	@Override
	public void setParentGame(Game g) {
		parent = g;
	}

}
