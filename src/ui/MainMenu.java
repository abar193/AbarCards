package ui;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import src.DeckBuilder;
import src.MenuController;
import src.MenuController.PossibleOpponents;
import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLPanel;
import aurelienribon.slidinglayout.SLSide;
import aurelienribon.slidinglayout.SLTransition;
import aurelienribon.tweenengine.Tween;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

/** 
 * Main menu of the game.
 * @author Abar
 *
 */
public class MainMenu extends JFrame implements ActionListener {
    
    private static final long serialVersionUID = -3549910545759711405L;

    private enum MenuState {
        Main, PickingBuildDeck, Building, PickingPlayDeck, Playing, Waiting; 
    }
    
    private final int SCREEN_WIDTH = 800; 
    private final int SCREEN_HEIGHT = 600;
    private final int LEFT_BUTTONS_WIDTH = 150;
    
    public boolean waiting = false;
    
    /** Configs for screen animations. See slidinglayout descriptions. */
    private SLConfig configMain, configBuildDeck, configChooseDeck, configGame, configWaiting;
    
    private JButton button1, button2, button3, buttonCancel;
    private JPanel buildDecks, playDecks, waitingPanel;
    private JLayeredPane gamePanel;
    private MenuState state;
    private final MenuController controller;
    private MenuController.PossibleOpponents opponent = PossibleOpponents.SinglePassiveBot;
    private final RadioListener listener = new RadioListener();
    
    /** Constructor. */
    public MainMenu() throws HeadlessException {
        super();
        
        controller = new MenuController(this);
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Cards Game");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
        
        this.addComponentListener(new ComponentAdapter() 
        {  
            public void componentResized(ComponentEvent evt) {
                Component c = (Component)evt.getSource();
                if(state == MenuState.Playing || state == MenuState.Building) {
                    gamePanel.setBounds(getContentPane().getBounds());
                    gamePanel.getComponent(0).setBounds(gamePanel.getBounds());
                    if(gamePanel.getComponent(0) instanceof SwingVS) {
                        ((SwingVS)gamePanel.getComponent(0)).resizeComponents();
                    }
                }
            }
        });
    }

    public final void createAndShowGUI() {
        
        this.setContentPane(new SLPanel());
        this.setBackground(java.awt.Color.WHITE);
        state = MenuState.Main;
        this.setMinimumSize(new java.awt.Dimension(800, 600));
        
        button1 = new JButton("New game");
        button1.addActionListener(this);
        button2 = new JButton("Build deck");
        button2.addActionListener(this);
        button3 = new JButton("Options");
        button3.addActionListener(this);
        
        buildDecks = new JPanel();
        buildDecks.setBackground(java.awt.Color.GREEN);
        buildDecks.setLayout(new BoxLayout(buildDecks, BoxLayout.Y_AXIS));
                
        playDecks = new JPanel();
        playDecks.setBackground(java.awt.Color.LIGHT_GRAY);
        playDecks.setLayout(new BoxLayout(playDecks, BoxLayout.Y_AXIS));
        updateDecks();
        
        configMain = new SLConfig((SLPanel) this.getContentPane());
        configMain.col(LEFT_BUTTONS_WIDTH).row(75).row(75).row(50).gap(10, 10)
            .place(0, 0, button1)
            .place(1, 0, button2)
            .place(2, 0, button3);
        
        configBuildDeck = new SLConfig((SLPanel) this.getContentPane());
        configBuildDeck.col(LEFT_BUTTONS_WIDTH).col(1f).row(1f).gap(10, 10)
            .beginGrid(0, 0)
                .col(1f).row(75).row(75).row(50)
                .place(0, 0, button1)
                .place(1, 0, button2)
                .place(2, 0, button3)
            .endGrid()
            .place(0, 1, buildDecks);
       
        configChooseDeck = new SLConfig((SLPanel) this.getContentPane());
        configChooseDeck.col(LEFT_BUTTONS_WIDTH).col(1f).row(1f).gap(10, 10)
            .beginGrid(0, 0)
                .col(1f).row(75).row(75).row(50)
                .place(0, 0, button1)
                .place(1, 0, button2)
                .place(2, 0, button3)
            .endGrid()
            .place(0, 1, playDecks);
       
        waitingPanel = new JPanel();
        waitingPanel.setBackground(java.awt.Color.green);
        waitingPanel.add(new JLabel("Waiting for the start"));
        buttonCancel = new JButton("Cancel search");
        buttonCancel.addActionListener(this);
        waitingPanel.add(buttonCancel);
        configWaiting = new SLConfig((SLPanel) this.getContentPane());
        configWaiting.row(1f).row(9f).col(1f).gap(10, 10)
            .place(0, 0, waitingPanel)
            .beginGrid(1, 0)
                .col(LEFT_BUTTONS_WIDTH)
                .col(1f).row(1f)
                .beginGrid(0, 0)
                    .col(1f).row(75).row(75).row(50)
                    .place(0, 0, button1)
                    .place(1, 0, button2)
                    .place(2, 0, button3)
                .endGrid()
                .place(0, 1, playDecks)
            .endGrid();
        
        gamePanel = new JLayeredPane();
        //gamePanel = new JPanel();
        gamePanel.setBackground(java.awt.Color.BLUE);
        gamePanel.setBounds(this.getContentPane().getBounds());
        configGame = new SLConfig((SLPanel) this.getContentPane());
        configGame.col(1f).row(1f).place(0, 0, gamePanel);
        
        ((SLPanel) this.getContentPane()).setTweenManager(SLAnimator.createTweenManager());
        ((SLPanel) this.getContentPane()).initialize(configMain);
        
        Tween.registerAccessor(MainMenu.class, new PanelAccessor());
        SLAnimator.start();

        setVisible(true);
    }
    
    public void deleteDeck(String name) {
        System.out.println(name);
    }
    
    // For that code below I should punish myself.
    // TODO refractor it
    @Override
    public void actionPerformed(final ActionEvent e) {
        if(e.getSource().equals(button1)) {
            if(state == MenuState.Main) {
                ((SLPanel) getContentPane()).createTransition()
                    .push(new SLKeyframe(configChooseDeck, 1f)
                        .setStartSide(SLSide.RIGHT, playDecks)
                        .setCallback(new SLKeyframe.Callback() {@Override public void done() {
                            state = MenuState.PickingPlayDeck;
                        }}))
                    .play();
            } else if(state == MenuState.PickingPlayDeck) {
                ((SLPanel) getContentPane()).createTransition()
                    .push(new SLKeyframe(configMain, 1f)
                        .setEndSide(SLSide.RIGHT, playDecks)
                        .setCallback(new SLKeyframe.Callback() {@Override public void done() {
                            state = MenuState.Main;
                        }}))
                    .play();
            } 
            
            if(state == MenuState.PickingBuildDeck) {
                ((SLPanel) getContentPane()).createTransition()
                    .push(new SLKeyframe(configMain, 0.5f)
                        .setEndSide(SLSide.BOTTOM, buildDecks)
                        .setCallback(new SLKeyframe.Callback() {@Override public void done() {
                            state = MenuState.PickingPlayDeck;
                        }}))
                     .push(new SLKeyframe(configChooseDeck, 0.5f)
                        .setStartSide(SLSide.RIGHT, playDecks)
                        .setCallback(new SLKeyframe.Callback() {@Override public void done() {
                            state = MenuState.PickingPlayDeck;
                        }}))
                    .play();
            }
        } else if(e.getSource().equals(button2)) {
            if(state == MenuState.Main) {
                ((SLPanel) getContentPane()).createTransition()
                    .push(new SLKeyframe(configBuildDeck, 1f)
                        .setStartSide(SLSide.RIGHT, buildDecks)
                        .setCallback(new SLKeyframe.Callback() {@Override public void done() {
                            state = MenuState.PickingBuildDeck;
                        }}))
                    .play();
            } else if(state == MenuState.PickingBuildDeck) {
                ((SLPanel) getContentPane()).createTransition()
                    .push(new SLKeyframe(configMain, 1f)
                        .setEndSide(SLSide.RIGHT, buildDecks)
                        .setCallback(new SLKeyframe.Callback() {@Override public void done() {
                            state = MenuState.Main;
                        }}))
                    .play();
            } else if(state == MenuState.PickingPlayDeck) {
                ((SLPanel) getContentPane()).createTransition()
                    .push(new SLKeyframe(configMain, 0.5f)
                        .setEndSide(SLSide.TOP, playDecks)
                        .setCallback(new SLKeyframe.Callback() {@Override public void done() {
                            state = MenuState.Main;
                        }}))
                    .push(new SLKeyframe(configBuildDeck, 0.5f)
                        .setStartSide(SLSide.RIGHT, buildDecks)
                        .setCallback(new SLKeyframe.Callback() {@Override public void done() {
                            state = MenuState.PickingBuildDeck;
                        }}))
                .play();
            }
        } else if(e.getSource().equals(button3)) {
            String[] responses = {"Not implemented!", "Sorry", "No way", "Turn back, mortal!", 
                    "Check again later", "Special for TLMH", "Made by Abar", "Go and play!" };
            button3.setText(responses[new java.util.Random().nextInt(responses.length)]);
        } else if(e.getSource().equals(buttonCancel)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    controller.requestSearchCancelation();
                }
            }).start();
        } else if(state == MenuState.PickingBuildDeck) {
            String s = e.getActionCommand();
            final DeckBuilder db = controller.provideDeckBuilder(s);
            
            gamePanel.removeAll();
            gamePanel.add(db.frame());
            
            ((SLPanel) getContentPane()).createTransition()
            .push(new SLKeyframe(configGame, 1f)
                .setEndSide(SLSide.LEFT, button1)
                .setEndSide(SLSide.LEFT, button2)
                .setEndSide(SLSide.LEFT, button3)
                .setEndSide(SLSide.LEFT, buildDecks)
                .setStartSide(SLSide.RIGHT, gamePanel)
                .setCallback(new SLKeyframe.Callback() {@Override public void done() {
                    state = MenuState.Building;
                    gamePanel.repaint();
                    gamePanel.validate();
                }}))
            .play();
        } else if(state == MenuState.PickingPlayDeck) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    controller.launchGame(e.getActionCommand(), opponent);
                }
            }).start();   
        }
    }    

    /** Called by MenuController, informs that Menu should swipe back from configBuilding to 
     * configBuildDeck. 
     */
    public void goBack() {
        updateDecks();
        
        ((SLPanel) getContentPane()).createTransition()
        .push(new SLKeyframe(configBuildDeck, 1f)
            .setStartSide(SLSide.LEFT, button1)
            .setStartSide(SLSide.LEFT, button2)
            .setStartSide(SLSide.LEFT, button3)
            .setStartSide(SLSide.LEFT, buildDecks)
            .setEndSide(SLSide.RIGHT, gamePanel)
            .setCallback(new SLKeyframe.Callback() {@Override public void done() {
                state = MenuState.PickingBuildDeck;
                gamePanel.removeAll();
            }}))
        .play();
    }
    
    /** Called by MenuController, when player quits game. */
    public void quitGame() {
        ((SLPanel) getContentPane()).createTransition()
        .push(new SLKeyframe(configMain, 1f)
            .setStartSide(SLSide.LEFT, button1)
            .setStartSide(SLSide.LEFT, button2)
            .setStartSide(SLSide.LEFT, button3)
            .setEndSide(SLSide.RIGHT, gamePanel)
            .setCallback(new SLKeyframe.Callback() {@Override public void done() {
                state = MenuState.Main;
                gamePanel.removeAll();
            }}))
        .play();
    }
    
    public void reciveWaitSignal() {
        ((SLPanel) getContentPane()).createTransition()
        .push(new SLKeyframe(configWaiting, 1f)
            .setStartSide(SLSide.TOP, waitingPanel)
            .setCallback(new SLKeyframe.Callback() {@Override public void done() {
                state = MenuState.Waiting;
                waiting = true;
            }}))
        .play();
    }
    
    public void cancelWaiting() {
        ((SLPanel) getContentPane()).createTransition()
        .push(new SLKeyframe(configChooseDeck, 1f)
            .setEndSide(SLSide.TOP, waitingPanel)
            .setCallback(new SLKeyframe.Callback() {@Override public void done() {
                state = MenuState.PickingPlayDeck;
                waiting = false;
            }}))
        .play();
    }
    
    public void reciveVs(final SwingVS vs) {
        gamePanel.removeAll();
        vs.setBackground(java.awt.Color.white);
        if(vs == null) System.err.println("Null");
        gamePanel.add(vs);
        
        SLTransition trans;
        SLKeyframe frame = new SLKeyframe(configGame, 1f);
        trans = ((SLPanel) getContentPane()).createTransition();
        trans.push(frame
            .setEndSide(SLSide.LEFT, button1)
            .setEndSide(SLSide.LEFT, button2)
            .setEndSide(SLSide.LEFT, button3)
            .setEndSide(SLSide.LEFT, playDecks)
            .setStartSide(SLSide.RIGHT, gamePanel)
            .setCallback(new SLKeyframe.Callback() {@Override public void done() {
                state = MenuState.Playing;
                vs.setBounds(gamePanel.getBounds());
                vs.setSize(gamePanel.getBounds().width, gamePanel.getBounds().height);
                vs.invalidate();
                vs.resizeComponents();
                vs.repaint();
                gamePanel.repaint();
                gamePanel.validate();
            }}));
        
        if(state == MenuState.Waiting) frame.setEndSide(SLSide.TOP, waitingPanel);
        
        trans.play();
    }
    
    private void updateDecks() {
        buildDecks.removeAll();

        buildDecks.add(new JLabel("Choose existing deck:"));
        ArrayList<String> names = new ArrayList<String>(5);
        int decks = DeckBuilder.availableFiles(false, names);
        int j = 0;
        for(String i : names) {
            if(j++ == decks) buildDecks.add(new JLabel("Build new deck:"));
                JButton butt = new JButton(i);
                butt.addActionListener(this);
                buildDecks.add(butt);
        }
        
        playDecks.removeAll();
        
        playDecks.add(new JLabel("Pick opponent:"));
        ButtonGroup group = new ButtonGroup();
        JRadioButton rb = new JRadioButton("Local passive bot");
        rb.setBackground(playDecks.getBackground());
        rb.setActionCommand("0");
        rb.setSelected(true);
        rb.addActionListener(listener);
        playDecks.add(rb);
        group.add(rb);
        
        rb = new JRadioButton("Local easy bot");
        rb.setBackground(playDecks.getBackground());
        rb.setActionCommand("1");
        rb.addActionListener(listener);
        playDecks.add(rb);
        group.add(rb);
        
        rb = new JRadioButton("Via server vs Easy bot");
        rb.setBackground(playDecks.getBackground());
        rb.setActionCommand("2");
        rb.addActionListener(listener);
        playDecks.add(rb);
        group.add(rb);
        
        rb = new JRadioButton("Via server vs real player");
        rb.setBackground(playDecks.getBackground());
        rb.setActionCommand("3");
        rb.addActionListener(listener);
        playDecks.add(rb);
        group.add(rb);
        
        rb = new JRadioButton("Developer game");
        rb.setBackground(playDecks.getBackground());
        rb.setActionCommand("4");
        rb.addActionListener(listener);
        playDecks.add(rb);
        group.add(rb);
        
        playDecks.add(new JLabel("Choose a deck to play with:"));
        names = new ArrayList<String>(5);
        DeckBuilder.availableFiles(true, names);
        for(String i : names) {
            JButton butt = new JButton(i);
            butt.addActionListener(this);
            playDecks.add(butt);
        }
    }
    
    public void setOpponent(int i) {
        switch(i) {
            case 0: 
                opponent = PossibleOpponents.SinglePassiveBot;
                break;
            case 1: 
                opponent = PossibleOpponents.SingleEasyBot;
                break;
            case 2: 
                opponent = PossibleOpponents.SocketEasyBot;
                break;
            case 3: 
                opponent = PossibleOpponents.SocketPlayer;
                break;
            case 4:
                opponent = PossibleOpponents.SocketDev;
            default:
        }
    }
    
    public class RadioListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            setOpponent(arg0.getActionCommand().charAt(0)-'0');
        }
        
    }
    
    /** Required for SlidingLayout to animate objects. */
    public static class PanelAccessor extends SLAnimator.ComponentAccessor  {

        /** Move X coordinate. */
        public static final int POSITION_X = 1;
        /** Move Y coordinate. */
        public static final int POSITION_Y = 2;
        /** Move both of them. */
        public static final int POSITION_XY = 3;

        @Override
        public final int getValues(Component target, int tweenType, float[] returnValues) {
            switch (tweenType) {
                case POSITION_X: returnValues[0] = target.getX(); return 1;
                case POSITION_Y: returnValues[0] = target.getY(); return 1;
                case POSITION_XY:
                    returnValues[0] = target.getX();
                    returnValues[1] = target.getY();
                    return 2;
                default: assert false; return -1;
            }
        }
        @Override
        public void setValues(Component target, int tweenType, float[] newValues) {
            switch (tweenType) {
                case POSITION_X: 
                    target.setLocation((int)newValues[0], (int)target.getAlignmentY()); 
                    break;
                case POSITION_Y: 
                    target.setLocation((int)target.getAlignmentX(), (int)newValues[0]);
                    break;
                case POSITION_XY:
                    target.setLocation((int)newValues[0], (int)newValues[1]);
                    break;
                default: assert false; break;
            }
        }
    }
    
    public static void main(String[] args) {
        MainMenu mm = new MainMenu();
    }
}
