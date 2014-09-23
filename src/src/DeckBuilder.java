package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import network.ServerGame;
import ui.CardPickingFrame;
import ui.ConsoleVS;
import ui.SwingVS;
import cards.*;
import decks.DeckPackReader;

public class DeckBuilder implements ActionListener {
	
	private final int CARDS_IN_A_ROW = 5;
	private final String DECK_NAME = "HeroDeck.Cards.";

	private final String[] names = {"Machines"};
	private final String[] links = {"MachinesDeck.xml"};
	
	public ArrayList<BasicCard> selectedCards;
	public ArrayList<BasicCard> fullDeck;

	private int playerChoose;
	private int startPos = 0;
	CardPickingFrame frame;
	
	
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
		frame.setDrawnCards(deck, start, selectedCards);
		frame.repaint();
	}
	
	public void removeSelecteCard(int i) {
		if(i < selectedCards.size()) {
			selectedCards.remove(i);
			drawCards(fullDeck, startPos);
		}
	}
	
	public void selectCard(BasicCard c) {
		if(selectedCards.size() < 15) { 
			selectedCards.add(c);
			drawCards(fullDeck, startPos);
		}
	}
	
	public void incPage() {
		startPos = Math.min(fullDeck.size() - 10, startPos + 10);
		drawCards(fullDeck, startPos);
	}
	
	public void decPage() {
		startPos = Math.max(0, startPos - 10);
		drawCards(fullDeck, startPos);
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
		    	String[] splits = new String[2];
		    	int lastpost = s.lastIndexOf(" ");
		    	splits[0] = s.substring(0, lastpost);
		    	splits[1] = s.substring(lastpost + 1);
		    	try{ 
		    		int cost = Integer.parseInt(splits[1]);
		    		for(BasicCard c : fullDeck) {
		    			if(c.name.equals(splits[0]) && c.cost == cost) { 
		    				selectedCards.add(c);
		    				break;
		    			}
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
	
	public void startGame(String command) {
		System.out.println("Launching: 1");
		Deck d = validDeck();
		if(d == null) return;
		
		switch(command) {
			case "Play vs Terran Ai":
				System.out.println("Launching: 1.1");
				launchGame(d, 'A');
				break;
			case "Play vs Passive Ai":
				System.out.println("Launching: 1.2");
				launchGame(d, 'S');
				break;
			case "Socket: vs Terran Ai":
				System.out.println("Watch out, we got a sockets there!");
				onlineGame(d, 'T');
			default: 
				break;
		}
	}
	
	public void onlineGame(Deck d, char c) {
		String opponent = "";
		if(c == 'T') opponent = "Terran";
		if(ServerGame.instance().validateDeck(selectedCards, opponent)) {
			if(ServerGame.instance().play()) {
				players.RealPlayer r = new players.RealPlayer(new SwingVS(ServerGame.instance()));
				r.setParentGameInterface(ServerGame.instance());
				ServerGame.instance().setPI(r);
			}
		}
		
	}
	
	public void launchGame(Deck d, char v) {
		DeckPackReader dpr = new DeckPackReader();
		System.out.println("Launching: 2");
		d.shuffleCards();
		Game g = new Game();
		players.RealPlayer r = new players.RealPlayer(new SwingVS(g));
		saveDeck();
		switch (v) {
			case 'A': {
				System.out.println("Launching: 2.1");
				Deck d2 = new Deck(dpr.parseFile("BotImbaDeck.xml"));
				d2.shuffleCards();
				g.configure(r, new players.SimpleBot(), d, d2, 15, 15);
				g.play();
				break;
			}
			case 'S':
			case 'D': {
				System.out.println("Launching: 2.2");
				Deck d2 = new Deck(dpr.parseFile("BotImbaDeck.xml"));
				d2.shuffleCards();
				g.configure(r, new players.PassiveBot(), d, d2, 15, 15);
				g.play();
				break;
			}
			default: 
				System.out.println("Launching: 2.3");
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
		frame.enableOverlay();
		if(files != null && files.size() > 0) {
			
			for(int i = 0; i < files.size(); i++) {
				frame.addOverlayOption(files.get(i).getName(), this);
			}
		}
		
		for(int i = 0; i < names.length; i++) {
			frame.addOverlayOption(names[i], this);
		}

	}
	
	public DeckBuilder(ArrayList<BasicCard> deck) {
		fullDeck = deck;
		selectedCards = new ArrayList<BasicCard>(15);
		frame = new CardPickingFrame(this);
	}
	
	public static void main(String[] arg) {
		DeckPackReader dpr = new DeckPackReader();
		ArrayList<BasicCard> c = dpr.parseFile("NeutralsDeck.xml");
		DeckBuilder db = new DeckBuilder(c);
		db.chooseDeck();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		for(int i = 0; i < names.length; i++) {
			if(arg0.getActionCommand().equals(names[i])) {
				selectedCards = new ArrayList<BasicCard>(15);
				ArrayList<BasicCard> classCards = new DeckPackReader().parseFile(links[i]);
				fullDeck.addAll(0, classCards);
				frame.disableOverlay();
				drawCards(fullDeck, startPos);
				return;
			}
		}
		
		final int c = arg0.getActionCommand().charAt(arg0.getActionCommand().length() - 1) - '0';
		selectedCards = new ArrayList<BasicCard>(15);
		ArrayList<BasicCard> classCards = new DeckPackReader().parseFile(links[c]);
		fullDeck.addAll(0, classCards);
		frame.disableOverlay();
		loadDeck(deckFiles().get(c));
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				drawCards(fullDeck, 0);
			}
		}).start();
		
	}

}
