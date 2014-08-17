package src;

import cards.*;
import units.Unit;
import units.Unit.Quality;
import units.UnitFactory;
import effects.*;

import java.util.ArrayList;

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
		final UnitCard specialCard = new UnitCard(1, 2, 2, "Sergeant", "1/2 +1Aura");
		specialCard.auraEffects = new effects.AuraEffect[1];
		specialCard.auraEffects[0] = new AuraEffect(AuraType.UnitDamage, 1, 0);
		
		final UnitCard corporal = new UnitCard(2, 1, 2, "Corporal", "");
		corporal.qualities = Quality.Charge.getValue();
		return new ArrayList<BasicCard>() {{
			add(new UnitCard(1, 1, 1, "Private", "1/1")); 
			add(corporal);
			add(specialCard);
			add(new UnitCard(3, 3, 2, "Officier", "3/3"));
			add(new UnitCard(5, 5, 3, "Lieutenant", "5/5"));
			add(new cards.SpellCard("Buff", "+5 damage", 0, new AllUnitsTargeter(-1), 
					new BuffUnit(new Buff(BuffType.Damage, 5))));
		}};
	}
	
	/** Launches the game */
	public void play() {
		DeckPackReader dpr = new DeckPackReader();
		ArrayList<BasicCard> bc = dpr.parseFile("C:\\Users\\Abar\\Documents\\Uni\\Workspace\\AbarCards\\src\\decks\\NeutralsDeck.xml");
		
		factory = new UnitFactory();
		
		Deck d1 = new Deck(bc);	
		d1.shuffleCards();
		Deck d2 = new Deck(bc);
		d2.shuffleCards();
		RealPlayer p1 = new RealPlayer();
		PassiveBot p2 = new PassiveBot();
		
		PlayerData player = new PlayerData(d1, 30, p1);
		PlayerData bot = new PlayerData(d2, 30, p2);
		
		initPlayers((PlayerInterface)p1, (PlayerInterface)p2, player, bot);
		
		playersData[0].pullCard(2);
		playersData[1].pullCard(3);
		
		FieldSituation fs = new FieldSituation();
		fs.addUnit(new Unit(new UnitCard(5, 3, 1, "Tank", "Test unit")), bot.playerNumber);
		fs.addUnit(new Unit(new UnitCard(4, 2, 1, "Cannon", "Test unit")), bot.playerNumber);
		fs.addUnit(new Unit(new UnitCard(4, 2, 1, "Cannon", "Test unit")), bot.playerNumber);
		
		SpecialUnitCard su = new SpecialUnitCard(0, 5, 0, "Bear", "A = H");
		su.specialUnitRef = cards.SpecialUnitCard.SpecialUnit.DmgEqHealth;
		fs.addUnit(factory.createUnit(su, bot.playerNumber), bot.playerNumber);
		
		Unit tauntUnit = new Unit(new UnitCard(0, 1, 1, "Home", "Sweet home"));
		tauntUnit.setQuality(Quality.Taunt);
		fs.addUnit(tauntUnit, bot.playerNumber);
		field = fs;
		field.refreshUnits();
		int i = 0;
		while(playersData[0].getHealth() > 0 && playersData[1].getHealth() > 0) {
			recalculateFieldModifiers();
			playersData[i%2].pullCard(1);
			playersData[i%2].newTurn();
			players.get(i%2).reciveInfo(playersData[i%2], field, 
					playersData[(i+1)%2].craeteOpenData());
			players.get(i%2).makeTurn();
			playersData[i%2].auras.removeOutdatedAuras();
			field.refreshUnits();
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
			return playersData[player].canPlayCard(c) & ((SpellCard)c).targeter.
					hasTargets(field, player);
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
				UnitCard uc = (UnitCard)c;
				Unit u = new Unit(uc);
				field.addUnit(u, player);

			} else if (c.type == CardType.Spell) {
				SpellCard card = (SpellCard)c;
				Unit[] arr = card.targeter.selectTargets(field, player);
				for(AbstractEffect ae : card.effect) {
					if(ae.type == EffectType.UnitEffect) {
						for(Unit u : arr) {
							((UnitEffect)ae).applyEffect(u);
						}
					}
				}
			}
			
			recalculateFieldModifiers();
			updateInfoForAll();
		}
	}
	
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
	
	public void recalculateFieldModifiers() {
		playersData[0].auras.calculateModifiers();
		playersData[1].auras.calculateModifiers();
		
		int[] modifiers = playersData[0].auras.getModifiers();
		for(Unit u : field.playerUnits.get(0)) {
			u.modDmg = modifiers[1];
			u.modHealth = modifiers[2];
		}
		
		modifiers = playersData[1].auras.getModifiers();
		for(Unit u : field.playerUnits.get(1)) {
			u.modDmg = modifiers[1];
			u.modHealth = modifiers[2];
		}
	}
	
	public static Game currentGame;
	
	public static void main(String[] args) {		
		Game g = new Game();
		currentGame = g;
		g.play();
	}
}
