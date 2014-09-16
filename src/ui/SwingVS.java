package ui;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import java.util.Random;

import src.FieldSituation;
import src.Game;
import units.TriggeringCondition;
import units.Unit;
import cards.BasicCard;

public class SwingVS extends JFrame implements VisualSystemInterface, /*WindowListener,*/ ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private Game parent; 
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
	
	public SwingVS(Game g) {
		parent = g;
		
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
		
	}

	
	public void createAndShowGUI() {
		setLayout(new BorderLayout());
        enemyHand = new EnemySideDrawer();
        enemyHand.setPreferredSize(new Dimension(800, 100));
        enemyHand.setVisible(true);
        if(opponent != null) enemyHand.setCards(opponent.handSize);
        add(enemyHand, BorderLayout.PAGE_START);
        
        outputMessages = new JTextArea();
        outputMessages.setEditable(false);
        outputMessages.setPreferredSize(new Dimension(150, 350));
        outputMessages.setText(messages);
        add(outputMessages, BorderLayout.LINE_START);
        
        fieldDrawer = new FieldDrawer();
        fieldDrawer.parent = this;
        if(latestSituation != null) fieldDrawer.setSituation(latestSituation, playerNumber);
        add(fieldDrawer, BorderLayout.CENTER);
        
        Panel right = new Panel();
        right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
        right.setPreferredSize(new Dimension(85, 350));
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
        right.add(myDeck);
        add(right, BorderLayout.LINE_END);
        
        cardsDrawer = new CardsDrawer();
        cardsDrawer.parent = this;
        cardsDrawer.setPreferredSize(new Dimension(800, 100));
        if(cards != null) cardsDrawer.setCards(cards, me.getAvailableMana(), me.getTotalMana());
        add(cardsDrawer, BorderLayout.PAGE_END);
        pack();
        setVisible(true);
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
		} else {
			latestSituation = field;
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
	public void setInputInterface(InputInterface i) {
		input = i;	
	}
	
	public void reciveCardClick(int card) {
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
	Unit pickedUnit;
	boolean pickingUnit = false;
	
	public void reciveUnitClick(int side, int unit) {
		//System.out.println(String.format("Clicked unit %d for side %d", unit, side));
		
		if(pickingUnit) {
			if(latestSituation.allUnitFromOneSide((side + playerNumber) % 2, true).size() > unit && unit >= 0) {
				pickedUnit = latestSituation.allUnitFromOneSide((side + playerNumber) % 2, true).get(unit);
			}
		}
		
		if(unit >= latestSituation.countUnitsForSide((playerNumber + side) % 2, false)) {
			unit = -1; // target hero
		}
		
		if(targeting) {
			if(side == 0)
				targeting = false;
			else {
				if(parent.attackIsValid(selectedUnit, unit, me.playerNumber, (playerNumber + 1) % 2)) {
					targeting = false;
					input.makeUnitsAttack(selectedUnit, unit);
				} else {
					displayError("Invalid target");
				}
			}
		} else if(side == 0) {
			ArrayList<Unit>myArmy = latestSituation.allUnitFromOneSide(me.playerNumber, false);
			
			if(myArmy.size() > unit && unit >= 0) {
				if(myArmy.get(unit).canAttack()) {
					displayMessage("Select target: ");
					targeting = true;
					selectedUnit = unit;
				}
			} else {
				displayError("Invalid unit selected");
			}
		}
	}
	
	@Override
	public void read() {
		turnEnded = false;
		while(!turnEnded) {
			try { 
				Thread.sleep(100);
			} catch (Exception e) {
				
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		turnEnded = true;
	}


	@Override
	public Unit provideUnit() {
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
