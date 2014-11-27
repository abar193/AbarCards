package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
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
    private DeckBuilder parent;
    private CardPickingPanel picker;
    private ArrayList<BasicCard> cards;
    
    
	private static final String SAVE_COMMAND = "Save deck";
	private static final String DELETE_COMMAND = "Delete deck";
	private static final String CANCEL_COMMAND = "Go back";
	
	private static final int MENU_HEIGHT = 100;
	
	public CardPickingFrame(DeckBuilder db) {
		parent = db;

		this.setSize(790, 570);
		createAndShowGUI();
	}
	
	public void createAndShowGUI() {
	    backButton = new JButton();
        input = new JTextField();
        saveButton = new JButton();
        deleteButton = new JButton();
        picker = new CardPickingPanel();
        leftButton = new JButton();
        rightButton = new JButton();
        deckPanel = new JPanel();
        switcherButton = new JButton();
        containerPanel = new JPanel();
        
        setMaximumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(800, 600));

        backButton.setText("<Back");
        backButton.setName("BackButton"); 
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        input.setText("Deck Name");

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonClicked(evt);
            }
        });
        
        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonClicked(evt);
            }
        });

        picker.setBackground(new java.awt.Color(102, 255, 102));
        picker.frameparent = this;
        
        GroupLayout pickerLayout = new GroupLayout(picker);
        picker.setLayout(pickerLayout);
        pickerLayout.setHorizontalGroup(
            pickerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 570, Short.MAX_VALUE)
        );
        pickerLayout.setVerticalGroup(
            pickerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        leftButton.setText("<");
        leftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftButtonActionPerformed(evt);
            }
        });

        rightButton.setText(">");
        rightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightButtonActionPerformed(evt);
            }
        });

        deckPanel.setBackground(new java.awt.Color(255, 0, 0));

        switcherButton.setText("Base/Action");

        containerPanel.setBackground(java.awt.Color.GRAY);
        containerPanel.setMinimumSize(new Dimension(100, 26 * 16));
        containerPanel.setPreferredSize(new Dimension(100, 26 * 16));
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));

        GroupLayout deckPanelLayout = new GroupLayout(deckPanel);
        deckPanel.setLayout(deckPanelLayout);
        deckPanelLayout.setHorizontalGroup(
            deckPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(deckPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(deckPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(switcherButton)
                    .addComponent(containerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        deckPanelLayout.setVerticalGroup(
            deckPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(deckPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(switcherButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(containerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(leftButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(picker, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rightButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deckPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(backButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(input, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteButton)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(backButton)
                    .addComponent(input, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton)
                    .addComponent(deleteButton))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(rightButton, GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                            .addComponent(leftButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deckPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addComponent(picker, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        
        picker.setDrawnCards(cards, 0);
        setVisible(true);
	}

	private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
	    parent.goBack();
    }

	private void saveButtonClicked(ActionEvent evt) {
	    parent.deckSaveName = input.getText();
        parent.saveDeck();
	}
	
	private void deleteButtonClicked(ActionEvent evt) {
	    parent.deleteDeck();
    }
	
    private void leftButtonActionPerformed(java.awt.event.ActionEvent evt) {
        parent.decPage();
    }

    private void rightButtonActionPerformed(java.awt.event.ActionEvent evt) {
        parent.incPage();
    }

	
	public void setDrawnCards(ArrayList<BasicCard> cards, int start) {
		picker.setDrawnCards(cards, start);
		containerPanel.repaint();
	}
	
	public void addSelectedCard(BasicCard c) {
	    JButton nb = new JButton();
        nb.setActionCommand("_" + containerPanel.getComponentCount());
        nb.setText(c.name);
        nb.addActionListener(this);
        buttons.add(nb);
        containerPanel.add(nb);
        containerPanel.revalidate();
        containerPanel.repaint();
	}
	
	public void setSelectedCards(ArrayList<BasicCard> cards) {
	    containerPanel.removeAll();
	    for(BasicCard c : cards) {
    	    JButton nb = new JButton();
            nb.setActionCommand("_" + containerPanel.getComponentCount());
            nb.setText(c.name);
            nb.addActionListener(this);
            buttons.add(nb);
            containerPanel.add(nb);
	    }
	    containerPanel.revalidate();
        containerPanel.repaint();
	}
	
	public void removeSelectedCard(int i) {
	    containerPanel.remove(buttons.get(i));
	    buttons.remove(i);
	    containerPanel.revalidate();
        containerPanel.repaint();
	}
	
	public void cardClicked(BasicCard card) {
		parent.selectCard(card);
	}
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {
		if(arg0.getActionCommand().length() <= 3 
		        && arg0.getActionCommand().charAt(0) == '_') 
		{
			int p = Integer.parseInt(arg0.getActionCommand().substring(1));
			parent.removeSelecteCard(p);
		} 
	}	
	
	private ArrayList<JButton> buttons = new ArrayList<JButton>(16); //TODO
	private JButton backButton;
    private JPanel containerPanel;
    private JPanel deckPanel;
    private JButton deleteButton;
    private JTextField input;
    private JButton leftButton;
    private JButton rightButton;
    private JButton saveButton;
    private JButton switcherButton;

}
