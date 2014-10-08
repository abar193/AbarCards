package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import cards.BasicCard;
import cards.UnitCard;
import src.DeckBuilder;


/**
 * Frame for picking cards to play them in deck. UI class for DeckBuilder controller.
 * @author Abar
 *
 */
public class CardPickingFrame extends JPanel implements ActionListener {

    private static final long serialVersionUID = 6581966660599964423L;
    DeckBuilder parent;
	CardPickingScreen picker;
	ArrayList<BasicCard> cards;
	JButton description;
	Panel selectedCardsText;
	JLayeredPane pane;
	Panel bottomLayer;
	
	private static final String SAVE_COMMAND = "Save deck";
	private static final String CANCEL_COMMAND = "Go back";
	
	public CardPickingFrame(DeckBuilder db, final int slotNum) {
		parent = db;
		this.setSize(800, 600);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(slotNum);
            }
        });
	}
	
	public void createAndShowGUI(int slot) {
		setLayout(new BorderLayout());
		System.out.println("Rendered");
		
		pane = new JLayeredPane();
		pane.setSize(this.getSize());
		//setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		add(pane);
		pane.setVisible(true);
		
		bottomLayer = new Panel();
		bottomLayer.setLayout(new BorderLayout());
		bottomLayer.setVisible(true);
		bottomLayer.setSize(800, 600);
		
		Panel panel = new Panel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setSize(800, 100);
		JLabel lab = new JLabel();
		lab.setText(String.format("Deck slot %d", slot));
		panel.add(lab);
		JButton button = new JButton();
		button.setText(SAVE_COMMAND);
		button.addActionListener(this);
		panel.add(button);
		button = new JButton();
		button.setText(CANCEL_COMMAND);
		button.addActionListener(this);
		panel.add(button);
		
		bottomLayer.add(panel, BorderLayout.PAGE_START);
        
		JButton left = new JButton();
		left.setSize(100, 400);
		left.setText("<");
		left.addActionListener(this);
		bottomLayer.add(left, BorderLayout.LINE_START);
        
		Panel cent = new Panel();
		cent.setSize(600, 400);
		bottomLayer.add(cent, BorderLayout.CENTER);
		cent.setLayout(new BoxLayout(cent, BoxLayout.LINE_AXIS));
		
		picker = new CardPickingScreen();
		picker.parent = this;
		picker.setMinimumSize(new Dimension(400, 400));
        picker.setBounds(0, 0, 500, 400);
        if(cards != null) picker.setDrawnCards(cards, 0); 
        cent.add(picker);
        
        selectedCardsText = new Panel();
        selectedCardsText.setLayout(new BoxLayout(selectedCardsText, BoxLayout.Y_AXIS));
        selectedCardsText.setBackground(java.awt.Color.LIGHT_GRAY);
        selectedCardsText.setSize(100, 450);
        selectedCardsText.setMaximumSize(new Dimension(100, 450));
        selectedCardsText.setMinimumSize(new Dimension(100, 450));
        selectedCardsText.setPreferredSize(new Dimension(100, 450));
        selectedCardsText.add(new JLabel("Selected cards:"));

        cent.add(selectedCardsText);
        
        JButton right = new JButton();
		right.setText(">");
		right.setSize(100, 400);
		right.addActionListener(this);
		bottomLayer.add(right, BorderLayout.LINE_END);
        
        description = new JButton();
        description.setSize(800, 100);
        description.setVisible(true);
        description.setText("LaLala");
        bottomLayer.add(description, BorderLayout.SOUTH);
        
        pane.add(bottomLayer, JLayeredPane.DEFAULT_LAYER);
        
        setVisible(true);
	}

	
	public void setDrawnCards(ArrayList<BasicCard> cards, int start, ArrayList<BasicCard> seletedCards) {
		while(picker == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		picker.setDrawnCards(cards, start);
		
		for(Component c : selectedCardsText.getComponents()) {
			if(!(c instanceof JLabel))
				selectedCardsText.remove(c);
		}

		int i = 0;
		for(BasicCard c : seletedCards) {
			JButton jb = new JButton();
			jb.setText(c.name);
			jb.setActionCommand("_" + Integer.toString(i++));
			jb.addActionListener(this);
			selectedCardsText.add(jb);
		}
		selectedCardsText.revalidate();
		selectedCardsText.repaint();
	}
	
	public void cardClicked(BasicCard card) {
		parent.selectCard(card);
	}
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {
		System.out.println(arg0.getActionCommand());
		
		if(arg0.getActionCommand().length() < 3 && arg0.getActionCommand().charAt(0) == '_') {
			int p = Integer.parseInt(arg0.getActionCommand().substring(1));
			parent.removeSelecteCard(p);
		} else if(arg0.getActionCommand().equals("<")) {
			parent.decPage();
		} else if(arg0.getActionCommand().equals(">")) {
			parent.incPage();
		} else if(arg0.getActionCommand().equals(SAVE_COMMAND)) {
		    parent.saveDeck();
		} else if(arg0.getActionCommand().equals(CANCEL_COMMAND)) {
		    parent.goBack();
		}
	}	

}
