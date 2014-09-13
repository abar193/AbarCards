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
public class Game {
	
	/**
	 * Stores 2 players.
	 */
	private ArrayList<PlayerInterface> players;
	/** Stores playerData for both players */
	private PlayerData[] playersData;
	/** Field, where all units, building (and heroes?) are standing*/
	private FieldSituation field;
	public UnitFactory factory;
	
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
		p1.setParentGame(this);
		p2.setParentGame(this);
	}
	
	
	/** Launches the game */
	public void play(PlayerInterface p1, PlayerInterface p2, Deck d1, Deck d2, int h1, int h2) {
		
		factory = new UnitFactory();
		
		/* * * Initialization * * */
		field = new FieldSituation();
		currentGame = this;
		
		PlayerData player1 = new PlayerData(d1, h1, p1);
		PlayerData player2 = new PlayerData(d2, h2, p2);
		initPlayers((PlayerInterface)p1, (PlayerInterface)p2, player1, player2);
		
		playersData[0].pullCard(2);
		playersData[1].pullCard(3);
		
		/* * * Test games * * */ 
		
		Unit tauntUnit = new Unit(new UnitCard(0, 2, 1, "Bunker", ""), 
				player2.playerNumber);
		tauntUnit.setQuality(Quality.Taunt);
		tauntUnit.myCard.fullDescription = "Has taunt";
		field.addUnit(tauntUnit, player2.playerNumber);
		tauntUnit = new Unit(new UnitCard(1, 14, 1, "Tanker", ""), 
				player1.playerNumber);
		field.addUnit(tauntUnit, player1.playerNumber);
		/* * * Game cycle * * */
		field.refreshUnits();
		int i = 0;
		while(playersData[0].getHealth() > 0 && playersData[1].getHealth() > 0) {
			/* * * Init * * */
			for(Unit u : field.allUnitFromOneSide(i%2, false)) {
				u.startTurn();
			}
			recalculateFieldModifiers();
			ArrayList<BasicCard> cards = playersData[i%2].pullCard(1);
			if(cards != null) 
				informLostCards(cards, i%2);
			
			/* * * Player * * */
			playersData[i%2].newTurn();
			players.get(i%2).reciveInfo(playersData[i%2], field, 
					playersData[(i+1)%2].craeteOpenData());
			
			/* * * Wait for player * * */
			Thread t = new Thread(players.get(i%2));
			Thread tk = new Thread(new ThreadKiller(t));
			t.start();
			tk.start();
			try { 
				t.join();
				if(tk.isAlive()) 
					tk.interrupt();
			} catch (InterruptedException e) {
				
			}
			t.interrupt();
			/* * * End turn * * */
			playersData[i%2].auras.removeOutdatedAuras();
			for(Unit u : field.allUnitFromOneSide(i%2, false)) {
				u.endTurn();
			}
			i++;	
		}
		
		if(playersData[0].getHealth() > 0 && playersData[1].getHealth() > 0) {
			informAll("Tie!");
		} else if(playersData[0].getHealth() > 0) {
			informAll("Player 2 won!");
		} else {
			informAll("Player 1 won!");
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
	public boolean attackIsValid(int attacker, int target, int playerA, int playerT) {
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
		if(field.containsOnDifferentSides(attacker, target)) {
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
			if(playersData[target.myPlayer].auras.unitDies(target)) 
				recalculateFieldModifiers();
		}
		if(attacker.isDead()) {
			informAll(attacker.myCard.name + " is dead"); 
			attacker.respondToEvent(TriggeringCondition.OnDeath, null);
			passEventAboutUnit(attacker, TriggeringCondition.OnAllyDeath);
			field.removeUnitOfPlayer(attacker, attacker.myPlayer);
			if(playersData[attacker.myPlayer].auras.unitDies(attacker)) 
				recalculateFieldModifiers();
		}
		updateInfoForAll();
	}
	
	/**
	 * Makes two units attack each other (attackIsValid(a, t, pa, pt) is called first)
	 */
	public void commitAttack(int a, int t, int pa, int pt) {
		if(attackIsValid(a, t, pa, pt)) {
			Unit attacker = field.unitForPlayer(a, pa);
			if(t == -1) {
				String s = attacker.myCard.name + " VS Hero";
				informAll(s);
				playersData[pt].takeDamage(attacker.getCurrentDamage()); 
				players.get(pt).reciveAction(String.format("You take %d damage!", attacker.getCurrentDamage()));
				attacker.attackUnit(null);
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
		if(c.type == CardType.Unit)
			return playersData[player].canPlayCard(c);
		else if(c.type == CardType.Spell) 
			return playersData[player].canPlayCard(c) & ((SpellCard)c).spell.validate(player);
		else return false;
	}
	
	/** Creates and places on field unit, performing all checks, and 
	 * triggering spawn-relevant events
	 * @param uc unit card for unit
	 * @param player player's number
	 * @return true, if unit has been placed 
	 */
	public boolean createUnit(UnitCard uc, int player) {
		Unit u = factory.createUnit(uc, player);
		try {
			if(field.canUnitBeAdded(u, player)) {
				u.respondToEvent(TriggeringCondition.BeforeCreate, null);
				field.addUnit(u, player);
			}
		} catch (IllegalArgumentException e) {
			return false;
		}
		u.respondToEvent(TriggeringCondition.OnCreate, null);
		this.passEventAboutUnit(u, TriggeringCondition.OnAllySpawn);
		return true;
	}
	
	/**
	 * Plays card of player. Summons unit, or casts spell, depending of card type.
	 * @param c
	 * @param player
	 */
	public void playCard(BasicCard c, int player) {
		if(canPlayCard(c, player)) {
			
			// Drawing
			players.get(player).reciveAction("Playing " + c.name);
			players.get((player + 1) % 2).reciveAction("Opponent plays " + c.name);

			if(c.type == CardType.Unit) {
				if(!createUnit((UnitCard)c, player)) 
					players.get(player).reciveAction("Can't add that");
				else 
					playersData[player].playCard(c);
			} else if (c.type == CardType.Spell) {
				SpellCard card = (SpellCard)c;	
				if(card.validate(player)) {
					card.exequte(player);
					playersData[player].playCard(c);
				}
			}
			
			recalculateFieldModifiers();
			updateInfoForAll();
		}
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
		playersData[0].auras.calculateModifiers();
		playersData[1].auras.calculateModifiers();
		
		for(int i = 0; i < 2; i++) {
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
	
	public static Game currentGame;
	
	public static void main(String[] args) {		
		Game g = new Game();
		currentGame = g;
		
		DeckPackReader dpr = new DeckPackReader();
		//URL url = getClass().getResource("decks/NeutralsDeck.xml");
		Deck d1 = new Deck(dpr.parseFile("TestDeck.xml"));	
		d1.shuffleCards();
		Deck d2 = new Deck(dpr.parseFile("BotImbaDeck.xml"));
		d2.shuffleCards();
		RealPlayer p1 = new RealPlayer(new SwingVS(g));
		SimpleBot p2 = new SimpleBot();
		
		g.play(p1, p2, d1, d2, 15, 15);
	}
	
	/** For tests only */
	public void applyFieldSituation(FieldSituation newField) {
		field = newField;
	}
}
