package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import cards.*;
import decks.DeckPackReader;

public class DeckBuilder {
	
	private final int CARDS_IN_A_ROW = 5;
	private final String DECK_NAME = "HeroDeck.Cards.";

	private final char qwerty[] = {'q', 'w', 'e', 'r', 't'};
	private final String[] names = {"Machines"};
	private final String[] links = {"MachinesDeck.xml"};
	
	public ArrayList<BasicCard> selectedCards;
	public ArrayList<BasicCard> fullDeck;

	private int playerChoose;
	
	/** Splits lines in fullDescription word by word, cuts words longer than 10 chars
	 * into smaller ones, and groups small words if their sum length < 10.
	 * @param deck ArrayList of cards, from where fullDescription is being read
	 * @param start Starting index. Will be proceeded cards with 
	 * 		indexes [start..start + CARDS_IN_A_ROW]
	 * @return String[][] array, where first index stands for corresponding card, 
	 * 		and second index - for cut lines of fullDescription. 	
	 */
	public String[][] splitLines(ArrayList<BasicCard> deck, int start) {
		String[][] splits = new String[CARDS_IN_A_ROW][];
		
		for(int i = 0; i < CARDS_IN_A_ROW; i++) {
			if(deck.get(start + i).fullDescription != null) {
				ArrayList<String>arr = new ArrayList<String>(Arrays.asList(deck.get(start + i).
						fullDescription.split(" ")));
				for(int j = 0; j < arr.size() - 1;) {
					if(arr.get(j).length() + arr.get(j+1).length() < 10) {
						// If j and j+1 words can be combined
						String s = arr.get(j);
						s = s + " " + arr.get(j + 1);
						arr.remove(j + 1);
						arr.remove(j);
						arr.add(j, s);
					} else if(arr.get(j).length() > 10) {
						// If j word is too long
						String s = arr.get(j).substring(0, 10);
						String z = arr.get(j).substring(10, arr.get(j).length());
						arr.remove(j);
						arr.add(j, z);
						arr.add(j, s);
					} else {
						// Else = proceed to next word
						j++;
					}
				}
				for(int j = arr.size() - 1; j < arr.size();) {
					// Split last word, if that is too big
					if(arr.get(j).length() > 10) {
						//If j word is too long
						String s = arr.get(j).substring(0, 10);
						String z = arr.get(j).substring(10, arr.get(j).length());
						arr.remove(j);
						arr.add(j, z);
						arr.add(j, s);
					} else {
						j++;
					}
				}
				splits[i] = arr.toArray(new String[arr.size()]);
			} else {
				// No fullDesciption - create just empty array
				splits[i] = new String[0];
			}
		}
		
		return splits;
	}
	
	/** Prints title string, CARDS_IN_A_ROW cards from deck, and selected cards array
	 * 
	 * @param deck source for drawing cards
	 * @param start starting index, from where cards should be drawn 
	 * @throws IndexOutOfBoundsException exception if start < 0, or 
	 * start + CARDS_IN_A_ROW > deck.size 
	 */
	public void drawCards(ArrayList<BasicCard> deck, int start) {
		String[][] splits = splitLines(deck, start);
		
		int maxFulldesc = 0;
		for(int i = 0; i < splits.length; i++) {
			if(splits[i].length > maxFulldesc) 
				maxFulldesc = splits[i].length;
		}
		
		// Top line
		System.out.format(" %26s%26s%26s\n", 
				"A - Play Vs Terran AI", 
				"S - Play Vs Zerg AI",
				"D - Play Vs Protoss AI");
		
		System.out.print(" ");
		for(int i = 0; i < 26; i++) {
			System.out.print(" - ");
		}
		System.out.println();
		
		// Names line
		for(int x = 0; x < CARDS_IN_A_ROW; x++) {
			System.out.format("%c%10s|", qwerty[x], deck.get(x + start).name);
		}
		System.out.println(" Your deck:");
		int y = 0;
		
		// Description line
		for(int x = 0; x < CARDS_IN_A_ROW; x++) {
			System.out.format("|%10s|", deck.get(x + start).description);
		}
		if(selectedCards.size() > y) {
			System.out.format(" %d %10s\n", y, selectedCards.get(y).name);
		} else {
			System.out.format(" %d %10s\n", y, "");
		}
		y++;
		
		// Stats line
		for(int x = 0; x < CARDS_IN_A_ROW; x++) {
			if(deck.get(x + start).type == CardType.Unit) {
				UnitCard bc = (UnitCard)deck.get(x + start);
				System.out.print(String.format("|%2dd/%2dh%2d$|", bc.getDamage(),
						bc.getHealth(), bc.cost));
			} else {
				System.out.format("|%6s%3d$|", "Spell", deck.get(x + start).cost);
			}
		}
		if(selectedCards.size() > y) {
			System.out.format(" %d %10s\n", y, selectedCards.get(y).name);
		} else {
			System.out.format(" %d %10s\n", y, "");
		}
		y++;
		
		// All fullDesc lines, from splits
		int yl = 0;
		for(; yl < maxFulldesc; yl++) {
			for(int x = 0; x < CARDS_IN_A_ROW; x++) {
				String s = "";
				if(splits[x].length > yl)
					if(splits[x][yl] != null) s = splits[x][yl];
				System.out.format("|%10s|", s);
			}
			if(selectedCards.size() > y + yl) {
				System.out.format(" %d %10s\n", y + yl, selectedCards.get(y + yl).name);
			} else {
				System.out.format(" %d %10s\n", y + yl, "");
			}
		}
		
		y += yl;
		
		// Finish selectedCards
		while(y < Math.max(15, selectedCards.size())) {
			if(selectedCards.size() > y) 
				System.out.format("%60s %d %10s\n", " ", y, selectedCards.get(y).name);
			else 
				System.out.format("%60s %d \n", " ", y);
			y++;
		}
		
		// Final lines
		System.out.println("(P)revious    (N)ext   (Z)Quit");
		
		System.out.print(" ");
		for(int i = 0; i < 26; i++) {
			System.out.print(" - ");
		}
		System.out.println();
		System.out.print(": ");
	}
	
	/** Lets player chose cards, until he is satisfied */
	public void pickCards() {
		int start = 0;
		String s = "";
		Scanner input = new Scanner(System.in);

		while(!s.equals("z")) {
			drawCards(fullDeck, start);
			s = input.nextLine();
			int v = 0;
			try { 
				v = Integer.parseInt(s);
				if(v <= selectedCards.size()) {
					selectedCards.remove(v);
				}
			} catch (NumberFormatException e) {
				if(s.length() > 0) {
					if(s.toUpperCase().equals("P")) {
						start = Math.max(0, start - CARDS_IN_A_ROW);
					} else if(s.toUpperCase().equals("N")) {
						start = Math.min(fullDeck.size() - CARDS_IN_A_ROW, start + CARDS_IN_A_ROW);
					} else if(s.toUpperCase().equals("A") || s.toUpperCase().equals("S")
							|| s.toUpperCase().equals("D")) {
						Deck d = validDeck();
						if(d != null) {
							launchGame(d, s.toUpperCase().charAt(0));
							input.close();
							return;
						}
					} 
					int i = 0;
					for(char c: qwerty) {
						if(s.toLowerCase().charAt(0) == c) {
							if(selectedCards.size() < 15) {
								selectedCards.add(fullDeck.get(start + i));
								break;
							}
						}
						i++;
					}
				}
			}
		}
		
		input.close();
	}
	
	/**
	 * Checks, if deck from selected cards is valid
	 * @return deck, or null if selected cards cannot build valid deck
	 */
	public Deck validDeck() {
		Deck d = new Deck(selectedCards);
		if(d.validateCards()) {
			return d;
		}
		return null;
	}
	
	/**
	 * Saves cards from selectedCards to file Deck.X, where X stands for 
	 * hero number.
	 * This method assumes selectedCards are validated and can be build into 
	 * valid deck.
	 */
	public void saveDeck() {
		BufferedWriter writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(DECK_NAME + playerChoose), "utf-8"));
		    for(BasicCard c : selectedCards) {
		    	writer.write(String.format("%s %d\n", c.name, c.cost));
		    }
		} catch (IOException ex) {
			System.out.println("Could not save your deck, sorry man.");
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
	
	/** Reads deck from file, and stores cards to selectedCards */
	public void loadDeck(File file) {
		BufferedReader reader = null;
		try {
		    reader = new BufferedReader(new InputStreamReader(
		          new FileInputStream(file), "utf-8"));
		    String s;
		    while((s = reader.readLine()) != null) {
		    	String[] splits = s.split(" ");
		    	try{ 
		    		int cost = Integer.parseInt(splits[1]);
		    		for(BasicCard c : fullDeck) {
		    			if(c.name.equals(splits[0]) && c.cost == cost) 
		    				selectedCards.add(c);
		    		}
		    	} catch (NumberFormatException e) {
		    		System.out.println("Contaminated file, at line " + splits[0]);
		    	}
		    }
		} catch (IOException ex) {
			System.out.println("Could not read your deck, sorry man.");
		} finally {
		   try {reader.close();} catch (Exception ex) {}
		}
	}
	
	public void launchGame(Deck d, char v) {
		DeckPackReader dpr = new DeckPackReader();
		d.shuffleCards();
		players.RealPlayer r = new players.RealPlayer();
		Game g = new Game();
		saveDeck();
		switch (v) {
			case 'A': {
				Deck d2 = new Deck(dpr.parseFile("BotImbaDeck.xml"));
				d2.shuffleCards();
				g.play(r, new players.SimpleBot(), d, d2, 15, 15);
				break;
			}
			case 'S':
			case 'D': {
				Deck d2 = new Deck(dpr.parseFile("BotImbaDeck.xml"));
				d2.shuffleCards();
				g.play(r, new players.PassiveBot(), d, d2, 15, 15);
				break;
			}
			default: 
		}
	}
	
	/* Loads all saved deck files and returns them in array list */
	private ArrayList<File> deckFiles() {
		ArrayList<File> files = new ArrayList<File>();
		for(int i = 0; i < names.length; i++) {
			File f = new File(DECK_NAME + i);
			if(f.exists() && !f.isDirectory()) {
				files.add(f);
			}
		}
		return files;
	}
	
	public void chooseDeck() {
		ArrayList<File> files = deckFiles();
		if(files != null && files.size() > 0) {
			System.out.println("Load your previous deck: ");
			
			for(int i = 0; i < files.size(); i++) {
				System.out.format("%c - %s\n", qwerty[i], files.get(i).getName());
			}
		}
		
		System.out.println("Choose your hero: ");
		
		for(int i = 0; i < names.length; i++) {
			System.out.format("%d - %s\n", i, names[i]);
		}
		char i = 0;
		while(true) {
			try {
				i = (char)System.in.read();
			} catch (Exception e) {
				
			}
			if(i - '0' >= 0 && i - '0' < names.length) {
				ArrayList<BasicCard> c = new DeckPackReader().parseFile(links[i-'0']);
				fullDeck.addAll(0, c);
				pickCards();
				return;
			} else {
				for(int j = 0; j < files.size(); j++) {
					if(i == qwerty[j]) {
						String fname = files.get(j).getName(); 
						int num = fname.charAt(fname.length() - 1) - '0';
						ArrayList<BasicCard> c = new DeckPackReader().parseFile(links[num]);
						fullDeck.addAll(0, c);
						loadDeck(files.get(j));
						pickCards();
						return;
					}
				}
			}
		}
	}
	
	public DeckBuilder(ArrayList<BasicCard> deck) {
		fullDeck = deck;
		selectedCards = new ArrayList<BasicCard>(15);
	}
	
	public static void main(String[] arg) {
		DeckPackReader dpr = new DeckPackReader();
		ArrayList<BasicCard> c = dpr.parseFile("NeutralsDeck.xml");
		DeckBuilder db = new DeckBuilder(c);
		db.chooseDeck();
	}

}
