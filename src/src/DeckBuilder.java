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

import ui.CardPickingFrame;
import cards.*;
import decks.DeckPackReader;

public class DeckBuilder {
	
	private static final int CARDS_IN_A_ROW = 5;
	private static final String DECK_NAME = "HeroDeck.Cards.";

	private static final String[] names = {"Machines"};
	private static final String[] links = {"MachinesDeck.xml"};
	
	public ArrayList<BasicCard> selectedCards;
	public ArrayList<BasicCard> fullDeck;
	public String deckRace;

	private int playerChoose;
	private int startPos = 0;
	private CardPickingFrame frame;
	private final MenuController controller;
	
	/** Prints title string, CARDS_IN_A_ROW cards from deck, and selected cards array.
	 * 
	 * @param deck source for drawing cards
	 * @param start starting index, from where cards should be drawn 
	 * @throws IndexOutOfBoundsException exception if start < 0, or 
	 * start + CARDS_IN_A_ROW > deck.size 
	 */
	public void drawCards() {
		frame.setDrawnCards(fullDeck, startPos, selectedCards);
		frame.repaint();
	}
	
	public void removeSelecteCard(int i) {
		if(i < selectedCards.size()) {
			selectedCards.remove(i);
			drawCards();
		}
	}
	
	public void selectCard(BasicCard c) {
		if(selectedCards.size() < Deck.DECK_SIZE) { 
			selectedCards.add(c);
			drawCards();
		}
	}
	
	public void incPage() {
		startPos = Math.min(fullDeck.size() - 10, startPos + 10);
		drawCards();
	}
	
	public void decPage() {
		startPos = Math.max(0, startPos - 10);
		drawCards();
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
		    writer.write(String.format("%s\n", names[playerChoose]));
		    
		    for(BasicCard c : selectedCards) {
		    	writer.write(String.format("%s %d\n", c.name, c.cost));
		    }
		} catch (IOException ex) {
			System.out.println("Could not save your deck, sorry man.");
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
	
	/** Called by CardPickingFrame, when user wants to get back. */
	public void goBack() {
	    controller.goBack();
	}
	
	/** Reads deck from file, and stores cards to selectedCards */
	public void loadMyDeck(File file) {
		BufferedReader reader = null;
		try {
		    reader = new BufferedReader(new InputStreamReader(
		          new FileInputStream(file), "utf-8"));
		    String s;
		    s = reader.readLine();
		    // Read race name
		    for(int i = 0; i < names.length; i++) {
		        if(s.startsWith(names[i])) {
		            deckRace = names[i];
		            ArrayList<BasicCard> classCards = new DeckPackReader().parseFile(links[i]);
		            playerChoose = i;
	                fullDeck.addAll(0, classCards);
	                break;
		        }
		    }
		    // Load deck
		    int count = 0;
		    while((s = reader.readLine()) != null) {
		        if(++count > Deck.DECK_SIZE) break; 
		    	String[] splits = new String[2];
		    	int lastpost = s.lastIndexOf(" ");
		    	splits[0] = s.substring(0, lastpost);
		    	splits[1] = s.substring(lastpost + 1);
		    	try{ 
		    		int cost = Integer.parseInt(splits[1]);
		    		boolean found = false;
		    		for(BasicCard c : fullDeck) {
		    			if(c.name.equals(splits[0]) && c.cost == cost) { 
		    				selectedCards.add(c);
		    				found = true;
		    				break;
		    			}
		    		}
		    		if(!found) {
		    		    System.err.format("Not found %s card at %d cost\n", splits[0], cost);
		    		}
		    	} catch (NumberFormatException e) {
		    		System.err.println("Contaminated file, at line " + splits[0]);
		    	}
		    }
		} catch (IOException ex) {
			System.err.println("Could not read your deck, sorry man.");
		} finally {
		   try {reader.close();} 
		   catch (Exception ex) {}
		}
	}
	
	/* Loads all saved deck files and returns them in array list */
	private static ArrayList<File> deckFiles() {
		ArrayList<File> files = new ArrayList<File>();
		for(int i = 0; i < names.length; i++) {
			File f = new File(DECK_NAME + i);
			if(f.exists() && !f.isDirectory()) {
				files.add(f);
			}
		}
		return files;
	}
	
	/**
	 * Returns ArrayList of race names and save files names.
	 * @param playDeck if set to true, then returns only save files 
	 */
	public static ArrayList<String> availableFiles(boolean playDeck) {
	    ArrayList<File> files = deckFiles();
	    int size = files.size() + ((!playDeck) ? names.length : 0);
	    ArrayList<String> retnames = new ArrayList<String>(size);
	    
	    for(File f : files) {
	        retnames.add(f.getName());
	    }
	    
	    if(playDeck) return retnames;
	    
	    for(String s : names) {
	        retnames.add(s);
	    }
	    return retnames;
	}
	
	/** 
	 * Initialises DeckBuilder for use from MainMenu. 
	 * @param raceName either name of race, or name of saved deck file 
	 */
	public DeckBuilder(String raceName, MenuController controller) {
	    this.controller = controller;
	    fullDeck = new DeckPackReader().parseFile("NeutralsDeck.xml");
	    selectedCards = new ArrayList<BasicCard>(Deck.DECK_SIZE);
	    boolean b = false;
	    
	    for(int i = 0; i < names.length; i++) {
	        if(raceName.equals(names[i])) {
	            ArrayList<BasicCard> classCards = new DeckPackReader().parseFile(links[i]);
                fullDeck.addAll(0, classCards);
                playerChoose = i;
	        }
	    }
	    
	    if(!b) loadMyDeck(new File(raceName));
	    frame = new CardPickingFrame(this, playerChoose);
	    
	    new Thread(new Runnable() {
            @Override
            public void run() {
                startPos = 0;
                drawCards();
            }
        }).start();
	}
	
	/** Gettter of frame. */
	public CardPickingFrame frame() {
	    return this.frame;
	}
	
	public static ArrayList<BasicCard> loadDeck(File file) {
        BufferedReader reader = null;
        ArrayList<BasicCard> selectedCards = new ArrayList<BasicCard>(15);
        try {
            reader = new BufferedReader(new InputStreamReader(
                  new FileInputStream(file), "utf-8"));
            String s;
            s = reader.readLine();
            DeckPackReader dpr = new DeckPackReader();
            ArrayList<BasicCard> fullDeck = dpr.parseFile("NeutralsDeck.xml");
            
            for(int i = 0; i < names.length; i++) {
                if(s.startsWith(names[i])) {
                    ArrayList<BasicCard> classCards = dpr.parseFile(links[i]);
                    fullDeck.addAll(0, classCards);
                    break;
                }
            }
            // Load deck
            int count = 0;
            while((s = reader.readLine()) != null) {
                if(++count > Deck.DECK_SIZE) break; 
                String[] splits = new String[2];
                int lastpost = s.lastIndexOf(" ");
                splits[0] = s.substring(0, lastpost);
                splits[1] = s.substring(lastpost + 1);
                try{ 
                    int cost = Integer.parseInt(splits[1]);
                    boolean found = false;
                    for(BasicCard c : fullDeck) {
                        if(c.name.equals(splits[0]) && c.cost == cost) { 
                            selectedCards.add(c);
                            found = true;
                            break;
                        }
                    }
                    if(!found) {
                        System.err.format("Not found %s card at %d cost\n", splits[0], cost);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Contaminated file, at line " + splits[0]);
                }
            }
        } catch (IOException ex) {
            System.err.println("Could not read your deck, sorry man.");
        } finally {
           try {reader.close();} 
           catch (Exception ex) {}
        }
        
        return selectedCards;
    }
	
}
