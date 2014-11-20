package ui;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import players.PlayerData;
import players.PlayerOpenData;
import cards.SpellCard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import src.FieldSituation;
import src.GameInterface;
import units.Building;
import units.FieldObject;
import units.TriggeringCondition;
import units.Unit;
import cards.BasicCard;

/** 
 * Brand new (lagging) VS with GUI and mouse clicks.
 * @author Abar
 *
 */
public class SwingVS extends JPanel implements VisualSystemInterface, ActionListener {
    
	private static final long serialVersionUID = 1L;
	
	private GameInterface parent;
	private InputInterface input;
	
	private CardsDrawer cardsDrawer;
	private JTextArea outputMessages;
	private FieldDrawer fieldDrawer; 
	private JLabel enemyDeck, myDeck;
	private EnemySideDrawer enemyHand;
	
	private ArrayList<BasicCard> cards;
	private String messages = "";
	private FieldSituation latestSituation;
	private PlayerData me;
	private PlayerOpenData opponent;
	int playerNumber;
	
	boolean turnEnded;
	public boolean targeting = false; 
	private boolean initialised = false;
	
	private final static int MIDDLE_AREA_HEIGHT = 350;
	private final static int MESSAGES_WIDTH = 150;
	private final static int RIGHT_PANEL_WIDTH = 85;
	private final static int CARDS_DRAWER_HEIGHT = 100;
	
	public SwingVS(GameInterface g) {
		parent = g;
		
		System.out.println("Constructed");
		
		setSize(790, 570);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
		
	}
	
	public void createAndShowGUI() {
	    final int width = this.getWidth();
	    final int height = this.getHeight();
	    
	    System.out.println("CaSG: started");
		setLayout(new BorderLayout());
        enemyHand = new EnemySideDrawer();
        enemyHand.setPreferredSize(new Dimension(width, height - MIDDLE_AREA_HEIGHT
                - CARDS_DRAWER_HEIGHT));
        enemyHand.setVisible(true);
        if(opponent != null) {
			enemyHand.setCards(opponent.handSize);
		}
        add(enemyHand, BorderLayout.PAGE_START);
        
        outputMessages = new JTextArea();
        outputMessages.setEditable(false);
        outputMessages.setPreferredSize(new Dimension(MESSAGES_WIDTH, MIDDLE_AREA_HEIGHT));
        outputMessages.setText(messages);
        add(outputMessages, BorderLayout.LINE_START);
        
        fieldDrawer = new FieldDrawer();
        fieldDrawer.parent = this;
        if(latestSituation != null) fieldDrawer.setSituation(latestSituation, playerNumber);
        add(fieldDrawer, BorderLayout.CENTER);
        
        Panel right = new Panel();
        right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
        right.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH, MIDDLE_AREA_HEIGHT));
        enemyDeck = new JLabel();
        if(opponent != null)
        	enemyDeck.setText(Integer.toString(opponent.deckSize));
        myDeck = new JLabel();
        if(me != null)
        	myDeck.setText(Integer.toString(me.getDeckSize()));
        JButton butt = new JButton();
        butt.setText("End turn");
        butt.addActionListener(this);
        right.add(enemyDeck);
        right.add(butt);
        butt = new JButton();
        butt.setText("Quit");
        butt.addActionListener(this);
        right.add(butt);
        right.add(myDeck);
        add(right, BorderLayout.LINE_END);
        
        cardsDrawer = new CardsDrawer();
        cardsDrawer.parent = this;
        cardsDrawer.setPreferredSize(new Dimension(width, CARDS_DRAWER_HEIGHT));
        if(cards != null) cardsDrawer.setCards(cards, me.getAvailableMana(), me.getTotalMana());
        add(cardsDrawer, BorderLayout.PAGE_END);
        setVisible(true);
        System.out.println("CaSG: ended");
        
        initialised = true;
	}

	@Override
	public void displayError(String m) {
		if(outputMessages != null) {
			outputMessages.setText("Error! > " + m + "\n" + outputMessages.getText());
		} else {
			messages = "Error! > " + m + "\n" + messages;
		}
	}

	@Override
	public void displayMessage(String m) {
		if(outputMessages != null) {
			outputMessages.setText("> " + m + "\n" + outputMessages.getText());
		} else {
			messages = "> " + m + "\n" + messages;
		}
	}

	@Override
	public void displayFieldState(PlayerData p1, FieldSituation field,
			PlayerOpenData p2) 
	{
		me = p1;
		opponent = p2;
		latestSituation = field;
		playerNumber = p1.playerNumber;

		if(enemyHand != null) {
			enemyHand.setCards(p2.handSize);
		}

		if(cardsDrawer != null) {
			cardsDrawer.setCards(p1.getHand(), p1.getAvailableMana(), p1.getTotalMana());
		} else {
			cards = p1.getHand();
		}
		
		if(fieldDrawer != null) {
			fieldDrawer.setSituation(field, p1.playerNumber);
		} 
		
		if(enemyDeck != null) {
			enemyDeck.setText(Integer.toString(p2.deckSize));
			myDeck.setText(Integer.toString(p1.getDeckSize()));
		} 
	}

	@Override
	public void displayAttack(Unit u1, Unit u2, boolean died1, boolean died2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayPower(Unit u, TriggeringCondition e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispaySpell(SpellCard s, int player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayUnitDamage(Unit u, int damage) {
		// TODO Auto-generated method stub
		
	}

	@Override 
	public void repaint() {
	    try {
	        if(!initialised) return;
	        cardsDrawer.repaint();
	        outputMessages.repaint();
	        fieldDrawer.repaint(); 
	        enemyHand.repaint();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@Override
	public void setInputInterface(InputInterface i) {
		input = i;	
	}
	
	public void receiveCardClick(int card) {
	    if(turnEnded) return;
		targeting = false;
		if(me.getHand().size() > card) {
			if(parent.canPlayCard(me.getHand().get(card), me.playerNumber)) {
				input.playUnitCard(card);
			} else {
				displayMessage("Can't play that");
			}
		}
	}
	
	public int selectedUnit = 0;
	FieldObject pickedUnit;
	boolean pickingUnit = false;
	
	public void receiveUnitClick(int side, int unit) {
		if(turnEnded) return;
		
		if(pickingUnit) {
		    side = (side == 0) ? me.playerNumber : opponent.playerNumber;
			if(unit >= 0) {
			    if(latestSituation.allObjectsFromOneSide(side, false).size() > unit) {
			        pickedUnit = latestSituation.allObjectsFromOneSide(side, false).get(unit);
			    }
			} else {
			    int pick = Math.abs(unit) - 1;
			    if(latestSituation.allBuildingsFromOneSide(side).size() > pick) {
			        pickedUnit = latestSituation.allBuildingsFromOneSide(side).get(pick);
                }
			} return;
		}
		
		if(targeting) {
		    if(side == 0 && unit == this.selectedUnit && unit < 0) { // double click on building
		        targeting = false;
		        if(parent.canUseBuilding(selectedUnit, me.playerNumber)) {
		            new Thread(new Runnable(){
                        @Override
                        public void run() {
                            parent.useBuildingCard(selectedUnit, me.playerNumber);
                        }}).start();
		        }
		    } else if(side == 0) {
				targeting = false;
		    } else {
				if(parent.attackIsValid(selectedUnit, unit, me.playerNumber)) {
					targeting = false;
					input.makeUnitsAttack(selectedUnit, unit);
				} else {
					displayError("Invalid target");
				}
			}
		} else if(side == 0) {
		    ArrayList<FieldObject> myArmy;
		    int selection;
		    if(unit >= 0) {
		        myArmy = latestSituation.allObjectsFromOneSide(me.playerNumber, false);
		        selection = unit;
		    } else {
		        myArmy = latestSituation.allBuildingsFromOneSide(me.playerNumber);
                selection = Math.abs(unit) - 1;
		    }
			
			if(myArmy.size() > selection) {
			    if(unit >= 0 && ((Unit)myArmy.get(selection)).canAttack()) {
			        displayMessage("Select target: ");
					targeting = true;
					selectedUnit = unit;
				} else if(unit < 0 && (myArmy.get(selection) instanceof Building)) {
				    if(((Building)myArmy.get(selection)).productAvailable()) {
    				    displayMessage("Double click to confirm");
    				    targeting = true;
                        selectedUnit = unit;
				    } else {
				        displayMessage("Building not ready");
				    }
				}
			}
		}
	}
	
	@Override
	public void read() {
		turnEnded = false;
		if(this.cardsDrawer != null)
		    this.cardsDrawer.repaint();
		if(this.fieldDrawer != null)
		    this.fieldDrawer.repaint();
		while(!turnEnded) {
			try { 
				Thread.sleep(100);
			} catch (InterruptedException e) {
				turnEnded = true;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		turnEnded = true;
		if(arg0.getActionCommand().equals("End turn")) {
    		new Thread(new Runnable() {
    			@Override
    			public void run() {
    				parent.endTurn(playerNumber);
    			}
    		}).start();
		} else {
		    parent.playerQuits(this.playerNumber);
		    src.MenuController.instance().quitGame();
		}
		
	}

	@Override
	public FieldObject provideUnit() {
		pickedUnit = null;
		pickingUnit = true;
		displayMessage("Pick unit target");
		while(pickedUnit == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		pickingUnit = false;
		return pickedUnit;
	}
}
