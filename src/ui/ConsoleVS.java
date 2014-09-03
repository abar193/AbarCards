package ui;

import java.util.ArrayList;
import java.util.List;

import cards.BasicCard;
import cards.CardType;
import cards.UnitCard;
import players.PlayerData;
import players.PlayerOpenData;
import src.FieldSituation;
import units.TriggeringCondition;
import units.Unit;
import units.Unit.Quality;
import cards.SpellCard;

public class ConsoleVS implements VisualSystemInterface {

	public ConsoleVS() {
		
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

}
