package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import ui.CardPickingFrame;
import cards.*;
import decks.DOMDeckReader;
import decks.DeckArrays;
import cards.CardJSONOperations;

public class DeckBuilder {
	
	private static final int CARDS_IN_A_ROW = 5;
	
	private static final String ext = ".deck";  
	public static final String[] availableRaces = {"Machines", "Aliens"};
	public static final String[] links = {"MachinesDeck.xml", "AliensDeck.xml"};
	
	public DeckArrays selectedCards;
	public ArrayList<BasicCard> actionCards;
	public ArrayList<BasicCard> baseCards;
	
	// link to either actionCards or baseCards depending on what's chosen
	private ArrayList<BasicCard> activeArray; 
	private ArrayList<BasicCard> activeSelectedArray;
	
	public String deckRace;
	public String deckSaveName;
	
	private int playerChoose;
	private int startPos = 0;
	private CardPickingFrame frame;
	private final MenuController controller;
	
	private boolean showingBaseCards = false;
	
	public void toggleSelection() {
	    startPos = 0;
	    if(!showingBaseCards) {
	        showingBaseCards = true;
	        activeArray = baseCards;
	        activeSelectedArray = selectedCards.baseCards;
	    } else {
	        showingBaseCards = false;
            activeArray = actionCards;
            activeSelectedArray = selectedCards.actionCards;
	    }
	    frame.setSelectedCards(activeSelectedArray);
	    drawCards();
	}
	
	public void drawCards() {
	    if(!showingBaseCards)
	        frame.setDrawnCards(actionCards, startPos);
	    else 
	        frame.setDrawnCards(baseCards, startPos);
		frame.repaint();
	}
	
	public void removeSelecteCard(int i) {
		if(i < activeSelectedArray.size()) {
			activeSelectedArray.remove(i);
			frame.removeSelectedCard(i);
			drawCards();
		} 
	}
	
	public void selectCard(BasicCard c) {
		if(activeSelectedArray.size() < 
		        ((showingBaseCards) ? Deck.BASE_DECK_SIZE : Deck.UNIT_DECK_SIZE)) 
		{ 
			activeSelectedArray.add(c);
			frame.addSelectedCard(c);
			drawCards();
		}
	}
	
	public void incPage() {
		startPos = Math.min(activeArray.size() - 10, startPos + 10);
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
		Deck d = new Deck(selectedCards.actionCards, selectedCards.baseCards);
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
		    FileOutputStream fos = new FileOutputStream(deckSaveName + ext); 
		    writer = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
		    
		    writer.write(String.format("%s\n", availableRaces[playerChoose]));
		    ArrayList<BasicCard> bothDecks = new ArrayList<BasicCard>();
		    bothDecks.addAll(selectedCards.actionCards);
		    bothDecks.addAll(selectedCards.baseCards);
		    
		    for(BasicCard c : bothDecks) {
		        String map = CardJSONOperations.instance.stringFromCard(c); 
		        writer.write(String.format("%s\n", map));
		    }
		} catch (IOException ex) {
			System.err.println("Could not save your deck, sorry man.");
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
	 * Load and fills local arrays actionCards and baseCards with available cards.
	 * @param deckNumber number of deck to load from links array
	 */
	public void fillCardsArrays(int deckNumber) {
	    DeckArrays da = new DOMDeckReader().parseFile(links[deckNumber]);
	    this.actionCards = da.actionCards;
	    this.baseCards = da.baseCards;
	    playerChoose = deckNumber;
	}
	
	/** 
	 * Loads selected cards from file and returns deck number of that file.
	 * @param file File name. May be *.deck or name of race - in second case actionCards and 
	 *     baseCards will remain untouched, but deck number will be returned
	 * @param actionCards initialized ArrayList to fill with actionCards (units and spells)
	 * @param baseCards initialized ArrayList to fill with baeCards (buildings and energy cards?)
	 * @return deck number from availableNames
	 */
	public static int loadSelectedCards(String fileName, DeckArrays selectedSet) {
		BufferedReader reader = null;
		// Check if that is just a race name
		for(int i = 0; i < availableRaces.length; i++) {
            if(fileName.equals(availableRaces[i])) {
                return i;
            }
        }
		try {
		    File file = new File(fileName);
		    int chose = -1;
		    FileInputStream fis = new FileInputStream(file);
		    reader = new BufferedReader(new InputStreamReader(fis, "utf-8"));
		    String s;
		    s = reader.readLine();

		    for(int i = 0; i < availableRaces.length; i++) {
		        if(s.startsWith(availableRaces[i])) {
		            chose = i;
		        }
		    }
		    
		    // Load deck
            int count = 0;

		    while((s = reader.readLine()) != null) {
		        if(++count > (Deck.UNIT_DECK_SIZE + Deck.BASE_DECK_SIZE)) break;
		        
		    	BasicCard bc = CardJSONOperations.instance.cardFromString(s);
		    	if(bc instanceof BuildingCard) {
		    	    selectedSet.baseCards.add(bc);
		    	} else {
		    	    selectedSet.actionCards.add(bc);
		    	}
		    }
		    return chose;
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
	 * @param availableFiles initialised array to fill with file names
	 */
	public static int availableFiles(boolean playDeck, ArrayList<String> availableFiles) {
	    ArrayList<File> files = deckFiles();
	    	    
	    for(File f : files) {
	        availableFiles.add(f.getName());
	    }
	    
	    if(playDeck) return files.size();
	    
	    for(String s : availableRaces) {
	        availableFiles.add(s);
	    }
	    return files.size();
	}
	
	/** 
	 * Initialises DeckBuilder for use from MainMenu. 
	 * @param raceName either name of race, or name of saved deck file 
	 */
	public DeckBuilder(String raceName, MenuController controller) {
	    this.controller = controller;
	    deckSaveName = raceName;
	    if(deckSaveName.endsWith(ext)) 
	        deckSaveName = deckSaveName.substring(0, deckSaveName.length() - ext.length());
	    
	    selectedCards = new DeckArrays();
	    
	    fillCardsArrays(loadSelectedCards(raceName, selectedCards));
	    
	    activeArray = actionCards;
	    activeSelectedArray = selectedCards.actionCards;
	    
	    frame = new CardPickingFrame(this);
	    frame.setSelectedCards(selectedCards.actionCards);
	    
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
