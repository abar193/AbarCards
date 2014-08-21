package src;

import cards.*;
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
	private UnitFactory factory;
	
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
		} else {
			players.add(p2);
			players.add(p1);
			playersData[0] = d2;
			playersData[1] = d1;
		}
		playersData[0].setPlayerNumber(0);
		playersData[1].setPlayerNumber(1);
		p1.setParentGame(this);
		p2.setParentGame(this);
	}
	
	/** Used only for test games now */
	private ArrayList<BasicCard> generateTestArrayList() {
		return null;
	}
	
	/** Launches the game */
	public void play() {
		DeckPackReader dpr = new DeckPackReader();
		ArrayList<BasicCard> bc = dpr.parseFile("C:\\Users\\Abar\\Documents\\Uni\\Workspace\\AbarCards\\src\\decks\\NeutralsDeck.xml");
		//bc.addAll(generateTestArrayList());
		
		factory = new UnitFactory();
		
		Deck d1 = new Deck(bc);	
		d1.shuffleCards();
		Deck d2 = new Deck(bc);
		d2.shuffleCards();
		RealPlayer p1 = new RealPlayer();
		PassiveBot p2 = new PassiveBot();
		
		PlayerData player = new PlayerData(d1, 10, p1);
		PlayerData bot = new PlayerData(d2, 10, p2);
		
		initPlayers((PlayerInterface)p1, (PlayerInterface)p2, player, bot);
		
		playersData[0].pullCard(2);
		playersData[1].pullCard(3);
		
		BuffSpell bs = new BuffSpell(new Buff(BuffType.AddDamage, 5));
		BuffSpell bs1 = new BuffSpell(new Buff(BuffType.AddHealth, 1));
		UnitPower up = new UnitPower(TriggeringCondition.OnDamage, bs);
		UnitPower up1 = new UnitPower(TriggeringCondition.OnAllyDamage, bs1);
		
		FieldSituation fs = new FieldSituation();
		fs.addUnit(new Unit(new UnitCard(5, 3, 1, "Raging", "  Tank"), 
				bot.playerNumber), bot.playerNumber);
		fs.playerUnits.get(bot.playerNumber).get(0).addPower(up);
		fs.addUnit(new Unit(new UnitCard(4, 2, 1, "Cannon", "Test unit"), 
				bot.playerNumber), bot.playerNumber);
		fs.addUnit(new Unit(new UnitCard(4, 2, 1, "Cannon", "Of Defence"), 
				bot.playerNumber), bot.playerNumber);
		fs.playerUnits.get(bot.playerNumber).get(2).addPower(up1);
		
		SpecialUnitCard su = new SpecialUnitCard(0, 5, 0, "Bear", "A = H");
		su.specialUnitRef = cards.SpecialUnitCard.SpecialUnit.DmgEqHealth;
		fs.addUnit(factory.createUnit(su, bot.playerNumber), bot.playerNumber);
		
		Unit tauntUnit = new Unit(new UnitCard(0, 1, 1, "Home", "Sweet home"), 
				bot.playerNumber);
		tauntUnit.setQuality(Quality.Taunt);
		tauntUnit.myCard.fullDescription = "Has taunt";
		fs.addUnit(tauntUnit, bot.playerNumber);
		
		field = fs;
		field.refreshUnits();
		int i = 0;
		while(playersData[0].getHealth() > 0 && playersData[1].getHealth() > 0) {
			for(Unit u : field.playerUnits.get(i%2)) {
				u.startTurn();
			}
			recalculateFieldModifiers();
			playersData[i%2].pullCard(1);
			playersData[i%2].newTurn();
			players.get(i%2).reciveInfo(playersData[i%2], field, 
					playersData[(i+1)%2].craeteOpenData());
			players.get(i%2).makeTurn();
			playersData[i%2].auras.removeOutdatedAuras();
			for(Unit u : field.playerUnits.get(i%2)) {
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
	 * @param playerT opponen players number
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
			if(field.unitForPlayer(target, playerT).hasQuality(Quality.Taunt)
					|| (field.tauntUnitsForPlayer(playerT) == 0)) 
				if(field.unitForPlayer(attacker, playerA).canAttack())
					return true;
		}
		return false;
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
				attacker.attackUnit(null);
				updateInfoForAll();
				return;
			}
			Unit target = field.unitForPlayer(t, pt);
			
			String s = attacker.myCard.name + 
					" VS " + target.myCard.name;
			informAll(s);
			
			attacker.attackUnit(target);
			if(target.isDead()) {
				informAll(target.myCard.name + " is dead");
				field.removeUnitOfPlayer(target, pt);
				if(playersData[pt].auras.unitDies(target)) 
					recalculateFieldModifiers();
			}
			if(attacker.isDead()) {
				informAll(attacker.myCard.name + " is dead"); 
				field.removeUnitOfPlayer(attacker, pa);
				if(playersData[pa].auras.unitDies(attacker)) 
					recalculateFieldModifiers();
			}
			updateInfoForAll();
		}
	}
	
	/**
	 * Sends same message to both players.
	 * @param m
	 */
	public void informAll(String m) {
		players.get(0).reciveAction(m);
		players.get(1).reciveAction(m);
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
			return playersData[player].canPlayCard(c) & ((SpellCard)c).spell.validate();
		else return false;
	}
	
	/**
	 * Plays card of player. Summons unit, or casts spell, depending of card type.
	 * @param c
	 * @param player
	 */
	public void playCard(BasicCard c, int player) {
		if(canPlayCard(c, player)) {
			playersData[player].playCard(c);
			// Drawing
			players.get(player).reciveAction("Playing " + c.name);
			players.get((player + 1) % 2).reciveAction("Opponent plays" + c.name);

			if(c.type == CardType.Unit) {
				Unit u = factory.createUnit((UnitCard)c, player);
				field.addUnit(u, player);
				u.respondToEvent(TriggeringCondition.OnCreate);
				this.passEventAboutUnit(u, TriggeringCondition.OnAllySpawn);

			} else if (c.type == CardType.Spell) {
				SpellCard card = (SpellCard)c;
				card.exequte(player);
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
		int p, u;
		System.out.println("Enter player to target (0 - you, 1 - he):");
		try {
			p = System.in.read();
			while(p != 48 && p != 49) {
				p = System.in.read();
			}
		} catch(java.io.IOException e){
			p = 0; 
		}
		System.out.println("Enter unit to target:");
		try {
			u = System.in.read();
			while(u < 48 || p > 57) {
				u = System.in.read();
			}
		} catch(java.io.IOException e){
			u = 0; 
		}
		p -= 48;
		u -= 48;
		return field.playerUnits.get((player + p) % 2).get(u);
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
	public void recalculateFieldModifiers() {
		playersData[0].auras.calculateModifiers();
		playersData[1].auras.calculateModifiers();
		
		for(int i = 0; i < 2; i++) {
			int[] modifiers = playersData[i].auras.getModifiers();
			Iterator<Unit> j = field.playerUnits.get(i).iterator();
			while(j.hasNext()) {
				Unit u = j.next();
				u.modDmg = modifiers[1];
				u.modHealth = modifiers[2];
				if(u.isDead()) {
					informAll(u.myCard.name + " is dead");
					//field.removeUnitOfPlayer(u, i);
					j.remove();
					if(playersData[i].auras.unitDies(u)) { 
						recalculateFieldModifiers();
						return;
					}
				}
			}
		}
	}
	
	/** Provides current FieldSituation for spell targeters */
	public FieldSituation provideFieldSituation() {
		return field;
	}
	
	/**
	 * Passes event for all unit of u's side, except u himself.
	 */
	public void passEventAboutUnit(Unit u, TriggeringCondition e) {
		ArrayList<Unit> side = null;
		if(field.playerUnits.get(0).contains(u)) 
			side = field.playerUnits.get(0);
		else if(field.playerUnits.get(1).contains(u))
			side = field.playerUnits.get(1);
		
		if(side != null) {
			for(Unit i : side) {
				if(!i.equals(u)) {
					i.respondToEvent(e);
				}
			}
		}
	}
	
	public static Game currentGame;
	
	public static void main(String[] args) {		
		Game g = new Game();
		currentGame = g;
		g.play();
	}
	
	/** For tests only */
	public void applyFieldSituation(FieldSituation newField) {
		field = newField;
	}
}
