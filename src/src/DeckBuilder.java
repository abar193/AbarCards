package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import ui.CardPickingFrame;
import cards.*;
import decks.DOMDeckReader;

public class DeckBuilder {
	
	private static final int CARDS_IN_A_ROW = 5;
	
	private static final String ext = ".deck";  
	private static final String[] names = {"Machines", "Aliens"};
	private static final String[] links = {"MachinesDeck.xml", "AliensDeck.xml"};
	
	public ArrayList<BasicCard> selectedCards;
	public ArrayList<BasicCard> fullDeck;
	public String deckRace;
	public String deckSaveName;
	
	private int playerChoose;
	private int startPos = 0;
	private CardPickingFrame frame;
	private final MenuController controller;
	
	public void drawCards() {
		frame.setDrawnCards(fullDeck, startPos);
		frame.repaint();
	}
	
	public void removeSelecteCard(int i) {
		if(i < selectedCards.size()) {
			selectedCards.remove(i);
			frame.removeSelectedCard(i);
			drawCards();
		}
	}
	
	public void selectCard(BasicCard c) {
		if(selectedCards.size() < Deck.UNIT_DECK_SIZE) { 
			selectedCards.add(c);
			frame.addSelectedCard(c);
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
		Deck d = new Deck(selectedCards, null);
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
		          new FileOutputStream(deckSaveName + ext), "utf-8"));
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
	
	public void deleteDeck() {
	    deleteDeck(deckSaveName + ext);
	    controller.goBack();
	}
	
	public static void deleteDeck(String s) {
	    try {
	        Files.deleteIfExists(java.nio.file.Paths.get(s, ""));
	    } catch(IOException e) {
	        System.out.println("No file found");
	    }
	}
	
	/** 
	 * Reads deck from file, and stores cards to selectedCards.
	 * @param file - file to read
	 * @param selectedCards - ArrayList to fill with player's selected cards
	 * @param fullDeck - array to fill with all deck cards
	 * @return returns integer value representing deck name's index in names array */
	public static int loadDeck(File file, ArrayList<BasicCard> selectedCards, ArrayList<BasicCard> fullDeck) {
		BufferedReader reader = null;
		try {
		    int choose = -1;
		    reader = new BufferedReader(new InputStreamReader(
		          new FileInputStream(file), "utf-8"));
		    String s;
		    s = reader.readLine();
		    // Read race name
		    for(int i = 0; i < names.length; i++) {
		        if(s.startsWith(names[i])) {
		            choose = i;
		            if(fullDeck != null) {
    		            ArrayList<BasicCard> classCards = new DOMDeckReader().parseFile(links[i]);
    	                fullDeck.addAll(0, classCards);
		            } else {
		                DOMDeckReader dpr = new DOMDeckReader();
		                fullDeck = dpr.parseFile("NeutralsDeck.xml");
		                ArrayList<BasicCard> classCards = dpr.parseFile(links[i]);
                        fullDeck.addAll(0, classCards);
		            }
	                break;
		        }
		    }
		    
		    if(selectedCards == null) return choose;
		    
		    // Load deck
            int count = 0;
		    while((s = reader.readLine()) != null) {
		        if(++count > Deck.UNIT_DECK_SIZE) break;
		        
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
		    return choose;
		} catch (IOException ex) {
			System.err.println("Could not read your deck, sorry man.");
		} finally {
		   try {reader.close();} 
		   catch (Exception ex) {}
		}
		return -1;
	}
	
	/* Loads all saved deck files and returns them in array list */
	private static ArrayList<File> deckFiles() {

		DeckFilesFilter filter = new DeckFilesFilter();
		File dir = new File(".");
		 
        if(!dir.isDirectory()){
            System.err.println("Directory does not exists here o_O");
            return null;
        }
 
        File[] array = dir.listFiles(filter);
        ArrayList<File>files = new ArrayList<File>(Arrays.asList(array));
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
	    DOMDeckReader dpr = new DOMDeckReader();
	    fullDeck = dpr.parseFile("NeutralsDeck.xml");
	    selectedCards = new ArrayList<BasicCard>(Deck.UNIT_DECK_SIZE);
	    boolean b = false;
	    deckSaveName = raceName;
	    
	    for(int i = 0; i < names.length; i++) {
	        if(raceName.equals(names[i])) {
	            ArrayList<BasicCard> classCards = dpr.parseFile(links[i]);
                fullDeck.addAll(0, classCards);
                playerChoose = i;
                b = true;
                break;
	        }
	    }
	    
	    if(!b) {
	        int pos = deckSaveName.lastIndexOf(ext);
	        deckSaveName = deckSaveName.substring(0, pos);
	        playerChoose = loadDeck(new File(raceName), selectedCards, fullDeck);
	        deckRace = names[playerChoose];
	    }
	    
	    frame = new CardPickingFrame(this);
	    frame.setSelectedCards(selectedCards);
	    
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
	
	public static class DeckFilesFilter implements FilenameFilter {
	    
        public DeckFilesFilter() {
        }
 
        public boolean accept(File dir, String name) {
            return (name.endsWith(".deck"));
        }
    }

}
