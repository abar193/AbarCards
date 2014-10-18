package src;

import cards.*;
import ui.ConsoleVS;
import ui.SwingVS;
import units.Unit;
import units.Unit.Quality;
import units.UnitFactory;
import units.TriggeringCondition;
import units.UnitPower;
import effects.*;

import java.util.ArrayList;
import java.util.Iterator;

import decks.DeckPackReader;
import players.*;

/**
 * Main class, with everything necessary for playing the game. 
 * @author Abar
 *
 */
public class Game implements GameInterface, ProviderGameInterface {
	
	/* Stores 2 players. */
	private ArrayList<PlayerInterface> players;
	/* Stores playerData for both players */
	private PlayerData[] playersData;
	/* Field, where all units, building (and heroes?) are standing*/
	private FieldSituation field;
	public UnitFactory factory;
	
	private Thread playerThread;
	private int playerTurn;
	private boolean playingCard = false;
	private boolean gameRunning = true;
	
	public GameStatReceiver statReceiver;
	/**
	 * Initialises player-relevant variables, and randomly puts chooses Player1 and Player2 from 
	 * 		p1 and p2. Order of p1 and p2 is irrelevant, the key thing is that d1 shall store 
	 * 		corresponding information of p1, and d2 - p2. 
	 * @param p1 one of the players
	 * @param p2 one of the players
	 * @param d1 data of one player
	 * @param d2 data of one player
	 */
	public void initPlayers(PlayerInterface p1, PlayerInterface p2, PlayerData d1, PlayerData d2) {
		players = new ArrayList<PlayerInterface>(2);
		playersData = new PlayerData[2];
		java.util.Random r = new java.util.Random();
		if(r.nextInt(2) == 0) {
			players.add(p1);
			players.add(p2);
			playersData[0] = d1;
			playersData[1] = d2;
			field.heroes.add(d1.representingUnit);
			d1.representingUnit.myPlayer = 0;
			field.heroes.add(d2.representingUnit);
			d2.representingUnit.myPlayer = 1;
		} else {
			players.add(p2);
			players.add(p1);
			playersData[0] = d2;
			playersData[1] = d1;
			field.heroes.add(d2.representingUnit);
			d2.representingUnit.myPlayer = 0;
			field.heroes.add(d1.representingUnit);
			d1.representingUnit.myPlayer = 1;
		}
		playersData[0].setPlayerNumber(0);
		playersData[1].setPlayerNumber(1);
		p1.setParentGameInterface(this);
		p2.setParentGameInterface(this);
	}
	
	public void configure(PlayerInterface p1, PlayerInterface p2, Deck d1, Deck d2, int h1, int h2) {
		/* * * Initialization * * */
		field = new FieldSituation(this);
		
		PlayerData player1 = new PlayerData(d1, h1, p1, this);
		PlayerData player2 = new PlayerData(d2, h2, p2, this);
		initPlayers((PlayerInterface)p1, (PlayerInterface)p2, player1, player2);
		
		playersData[0].pullCard(2);
		playersData[1].pullCard(3);
		
	}
	
	/** Launches the game */
	public void play() {
		
		factory = new UnitFactory();
		
		/* * * Game cycle * * */
		field.refreshUnits();
		int i = 0;
		this.gameRunning = true;
		while(playersData[0].getHealth() > 0 && playersData[1].getHealth() > 0 && gameRunning) {
			/* * * Init * * */
		    int player = i % 2;
		    int opponent = (i + 1) % 2;
			for(Unit u : field.allUnitFromOneSide(player, false)) {
				u.startTurn();
			}
			recalculateFieldModifiers();
			ArrayList<BasicCard> cards = playersData[player].pullCard(1);
			if(cards != null) 
				informLostCards(cards, player);
			
			/* * * Player * * */
			playersData[player].newTurn();
			players.get(player).reciveInfo(playersData[player], field, 
					playersData[opponent].craeteOpenData());
			players.get(opponent).reciveInfo(playersData[opponent], field, 
                    playersData[player].craeteOpenData());
			playingCard = false;
			
			/* * * Wait for player * * */
			playerTurn = i % 2;
			playerThread = new Thread(players.get(i%2));
			Thread tk = new Thread(new ThreadKiller(playerThread));
			if(!(playerThread instanceof Runnable)) players.get(i%2).run();
			else playerThread.start();
			tk.start();
			try { 
				playerThread.join();
				if(tk.isAlive()) 
					tk.interrupt();
			} catch (InterruptedException e) {
				
			}
			playerThread.interrupt();
			/* * * End turn * * */
			
			playingCard = false;
			playersData[i%2].auras.removeOutdatedAuras();
			for(Unit u : field.allUnitFromOneSide(i%2, false)) {
				u.endTurn();
			}
			i++;
		}
		
	}
	
	/**
	 * Checks if one unit may attack another one.
	 * @param attacker attacker's unit position field.playerUnits.get(playerA) 
	 * @param target target's unit position field.playerUnits.get(playerT)
	 * @param playerA attacker players number
	 * @param playerT opponent players number
	 * @return true if attack is valid
	 */
	public boolean attackIsValid(int attacker, int target, int playerA) {
	    if(playerA != playerTurn || !gameRunning) return false;
	    
		int playerT = (playerA + 1) % 2;
		if(target == -1) {
			if(field.unitExist(attacker, playerA) && (field.tauntUnitsForPlayer(playerT) == 0)) {
				return true;
			} 
			return false;
		}
		if(field.unitExist(attacker, playerA) & field.unitExist(target, playerT)) {
			Unit u = field.unitForPlayer(target, playerT);
			if((u.hasQuality(Quality.Taunt) || (field.tauntUnitsForPlayer(playerT) == 0)) && 
					!u.hasQuality(Quality.Stealth)) 
				if(field.unitForPlayer(attacker, playerA).canAttack())
					return true;
		}
		return false;
	}
	
	public boolean attackIsValid(Unit attacker, Unit target) {
		if(field.containsOnDifferentSides(attacker, target) && gameRunning) {
			if(target.hasQuality(Quality.Taunt)
					|| (field.tauntUnitsForPlayer(target.myPlayer) == 0)) 
				if(attacker.canAttack())
					return true;
		}
		return false;
	}
	
	/**
	 * Informs both players, that someone lost cards
	 * @param cards
	 */
	public void informLostCards(ArrayList<BasicCard> cards, int player) {
		for(BasicCard bc : cards) {
			informAll(String.format("Player %d lost %s card!", player, bc.name));
		}
	}
	
	/**
	 * Makes two units attack each other (attackIsValid(attacker, target) is called first)
	 */
	public void commitAttack(Unit attacker, Unit target) {
		String s = attacker.myCard.name + 
				" VS " + target.myCard.name;
		informAll(s);
		
		attacker.attackUnit(target);
		if(target.isDead()) {
			informAll(target.myCard.name + " is dead");
			target.respondToEvent(TriggeringCondition.OnDeath, null);
			passEventAboutUnit(target, TriggeringCondition.OnAllyDeath);
			field.removeUnitOfPlayer(target, target.myPlayer); 
		}
		if(attacker.isDead()) {
			informAll(attacker.myCard.name + " is dead"); 
			attacker.respondToEvent(TriggeringCondition.OnDeath, null);
			passEventAboutUnit(attacker, TriggeringCondition.OnAllyDeath);
			field.removeUnitOfPlayer(attacker, attacker.myPlayer); 
		}
		recalculateFieldModifiers();
		updateInfoForAll();
	}
	
	/**
	 * Makes two units attack each other (attackIsValid(a, t, pa, pt) is called first)
	 */
	public void commitAttack(int a, int t, int pa) {
		if(attackIsValid(a, t, pa)) {
			int pt = (pa + 1) % 2;
			Unit attacker = field.unitForPlayer(a, pa);
			if(t == -1) {
				String s = attacker.myCard.name + " VS Hero";
				informAll(s);
				playersData[pt].takeDamage(attacker.getCurrentDamage()); 
				players.get(pt).reciveAction(String.format("You take %d damage!", attacker.getCurrentDamage()));
				attacker.attackUnit(null);
				recalculateFieldModifiers();
				updateInfoForAll();
				return;
			}
			Unit target = field.unitForPlayer(t, pt);
			commitAttack(attacker, target);
		}
	}
	
	/**
	 * Sends same message to both players.
	 * @param m
	 */
	public void informAll(String m) {
		if(players != null) {
			players.get(0).reciveAction(m);
			players.get(1).reciveAction(m);
		}
	}
	
	public void displayError(String m) {
		if(players != null) {
			players.get(0).visual().displayError(m);
			players.get(1).visual().displayError(m);
		}
	}
	
	/**
	 * Checks if player may play his card.
	 * @param c card to play
	 * @param player players number
	 */
	public boolean canPlayCard(BasicCard c, int player) {
	    if(player != playerTurn || playingCard || !gameRunning) return false;
	    
		if(c.type == CardType.Unit)
			return playersData[player].canPlayCard(c);
		else if(c.type == CardType.Spell) 
			return playersData[player].canPlayCard(c) 
			        & ((SpellCard)c).spell.validate(player, this);
		else return false;
	}
	
	/** Creates and places on field unit, performing all checks, and 
	 * triggering spawn-relevant events
	 * @param uc unit card for unit
	 * @param player player's number
	 * @return null if nothing was placed, or created unit 
	 */
	public Unit createUnit(UnitCard uc, int player, boolean fromSpell) {
		Unit u = factory.createUnit(uc, player, this);
		try {
			if(field.canUnitBeAdded(u, player)) {
				u.respondToEvent(TriggeringCondition.BeforeCreate, null);
				field.addUnit(u, player);
				if(fromSpell) 
				    triggerCreatingEvents(u);
			}
		} catch (IllegalArgumentException e) {
			return null;
		}
		
		return u;
	}
	
	/**
	 * Plays card of player. Summons unit, or casts spell, depending of card type.
	 * @param c
	 * @param player
	 */
	public void playCard(BasicCard c, int player) {
	    synchronized(playersData[player]) {
    		if(canPlayCard(c, player)) {
    		    playingCard = true;
    			// Drawing
    			players.get(player).reciveAction("Playing " + c.name);
    			players.get((player + 1) % 2).reciveAction("Opponent plays " + c.name);
    
    			if(c.type == CardType.Unit) {
    			    Unit u = createUnit((UnitCard)c, player, false); 
    				if(u == null) 
    					players.get(player).reciveAction("Can't add that");
    				else {
    					playersData[player].playCard(c);
    					triggerCreatingEvents(u);
    				}
    			} else if (c.type == CardType.Spell) {
    				SpellCard card = (SpellCard)c;	
    				if(card.validate(player, this)) {
    					card.exequte(player, this);
    					playersData[player].playCard(c);
    				}
    			}
    			
    			recalculateFieldModifiers();
    			updateInfoForAll();
    			playingCard = false;
    		}
	    }
	}
	
	/**
	 * Called for freshly created unit, to trigger "OnCreate" and "OnAllySpawn" events.
	 * @param u new units
	 */
	private void triggerCreatingEvents(Unit u) {
	    u.respondToEvent(TriggeringCondition.OnCreate, null);
        this.passEventAboutUnit(u, TriggeringCondition.OnAllySpawn);
	}
	
	/**
	 * Passes spell to the chosen player
	 * @param player 0..1 - id of a player in playersData array
	 * @param spell spell to be received - instance of PlayerValueSpell 
	 */
	public void applySpellToPlayer(int player, AbstractSpell spell) {
		playersData[player].reciveSpell(spell);
	}
	
	/**
	 * Adds aura effect to the player
	 */
	public void addAuraForPlayer(int player, AuraEffect ae) {
		playersData[player].auras.addAura(ae);
	}
	
	public Unit askPlayerForTarget(int player) {
		return players.get(player).selectTarget();
	}
	
	/**
	 * Calls reciveInfo with actual state for both players 
	 */
	public void updateInfoForAll() {
		players.get(0).reciveInfo(playersData[0], field, 
				playersData[1].craeteOpenData());
		players.get(1).reciveInfo(playersData[1], field, 
				playersData[0].craeteOpenData());
	}
	
	/**
	 * Calculates cost- and health-modifiers based on players auras, and applies them to units.
	 */
	public synchronized void recalculateFieldModifiers() {
		for(int i = 0; i < 2; i++) {
		    playersData[i].auras.calculateModifiers();
		    if(playersData[i].getHealth() <= 0) endGame(i);
		    
			int[] modifiers = playersData[i].auras.getModifiers();
			Iterator<Unit> j = field.provideIteratorForSide(i);
			while(j.hasNext()) { // Iteration 1: calculate aura-modifiers, remove dead units
				Unit u = j.next();
				u.modDmg = modifiers[1];
				u.modHealth = modifiers[2];
				u.modQualities = 0;
				if(u.isDead()) {
					informAll(u.myCard.name + " is dead");
					j.remove();
					u.respondToEvent(TriggeringCondition.OnDeath, null);
					field.passEventAboutRemovedUnitFromSide(u.myPlayer, u, 
							TriggeringCondition.OnAllyDeath);
					if(playersData[i].auras.unitDies(u)) { 
						recalculateFieldModifiers();
						return;
					}
					j = field.provideIteratorForSide(i); // to avoid deathrate-summonUnits-exception
				}
			}
			j = field.provideIteratorForSide(i);
			while(j.hasNext()) { // Iteration 2: trigger UnitPowers with condition "always"
				Unit u = j.next();
				u.respondToEvent(TriggeringCondition.Always, null);
			}
		}
	}
	
	/** Provides current FieldSituation for spell targeters */
	public FieldSituation provideFieldSituation() {
		return field;
	}
	
	/**
	 * Call's fieldSitation's method for convenience
	 */
	public void passEventAboutUnit(Unit u, TriggeringCondition e) {
		field.passEventAboutUnit(u, e);
	}
	
	/** For tests only */
	public void applyFieldSituation(FieldSituation newField) {
		field = newField;
	}

	private void endGame(int looser) {
	    gameRunning = false;
	    playerThread.interrupt();
	    int winner;
	    if(playersData[0].getHealth() <= 0 && playersData[1].getHealth() <= 0) {
	        winner = -1;
            informAll("Tie!");
        } else {
            winner = (looser + 1) % 2;
            informAll(String.format("Player %d won!", winner));
        }
        if(statReceiver != null) {
            statReceiver.gameEnded(this, winner);
        }
	}

	@Override
	public void endTurn(int player) {
		if(playerTurn == player) {
			playerThread.interrupt();
		}
	}

    @Override
    public void playerQuits(int player) {
        endGame(player);
    }
}
