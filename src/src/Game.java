package src;

import cards.*;
import ui.SwingVS;
import units.Building;
import units.FieldObject;
import units.Unit;
import units.UnitFactory;
import units.TriggeringCondition;
import units.UnitPower;
import effects.*;

import java.util.ArrayList;
import java.util.Iterator;

import players.*;

/**
 * Main class, with everything necessary for playing the game. 
 * @author Abar
 *
 */
public class Game implements GameInterface, ProviderGameInterface {
	
	/* Stores 2 players. */
	protected ArrayList<PlayerInterface> players;
	/* Stores playerData for both players */
	protected PlayerData[] playersData;
	/* Field, where all units, building (and heroes?) are standing*/
	protected FieldSituation field;
	public UnitFactory factory;
	
	private Thread playerThread;
	protected int playerTurn;
	protected boolean playingCard = false;
	protected boolean gameRunning = true;
	
	protected boolean devMode = false;
	protected boolean devModeMana = false;
	
	public GameStatReceiver statReceiver;
	
	public void setDevGame() {
	    devMode = true;
	}
	
	public void addDevCards() {
	    if(players != null && players.size() >= 2) {
	        for(int i = 0; i < 2; i++) {
	            if(!(players.get(i) instanceof players.SimpleBot)) {
	                System.out.println(">>> DEV MODE: Add cards");
	                playersData[i].recieveCard(new cards.DevCard("Dev:InfiniteMana", ""));
	                playersData[i].recieveCard(new cards.DevCard("Dev:Draw5Cards", ""));
	            }
	        }
	    }
	}
	
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
			field.addHeroForSide(0, d1.representingUnit);
			d1.representingUnit.player = 0;
			field.addHeroForSide(1, d2.representingUnit);
			d2.representingUnit.player = 1;
		} else {
			players.add(p2);
			players.add(p1);
			playersData[0] = d2;
			playersData[1] = d1;
			field.addHeroForSide(0, d2.representingUnit);
			d2.representingUnit.player = 0;
			field.addHeroForSide(1, d1.representingUnit);
			d1.representingUnit.player = 1;
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
		
		playersData[0].pullCard(2, false);
		playersData[1].pullCard(3, false);
	}
	
	/** Launches the game */
	public void play() {
		
		factory = new UnitFactory();
		
		/* * * Game cycle * * */
		field.refreshObjects();
		int i = 0;
		this.gameRunning = true;
		while(playersData[0].getHealth() > 0 && playersData[1].getHealth() > 0 && gameRunning) {
		    field.globalEvent(units.TriggeringCondition.GlobalCondition.TurnStart);
			/* * * Init * * */
		    int player = i % 2;
		    int opponent = (i + 1) % 2;
			for(FieldObject u : field.allObjectsFromOneSide(player, true)) {
				u.startTurn();
			}
			recalculateFieldModifiers();
			ArrayList<BasicCard> cards = playersData[player].pullCard(1, false);
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
			field.globalEvent(units.TriggeringCondition.GlobalCondition.TurnEnd);
			/* * * End turn * * */
			
			playingCard = false;
			playersData[i%2].auras.removeOutdatedAuras();
			for(FieldObject u : field.allObjectsFromOneSide(i%2, true)) {
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
	 * @return true if attack is valid
	 */
	public boolean attackIsValid(int attacker, int target, int playerA) {
	    if(playerA != playerTurn || !gameRunning) return false;
	    
		int playerT = (playerA + 1) % 2;
		if(attacker < 0) return false;
		
	    if(field.objectExist(attacker, playerA) && field.objectExist(target, playerT)) {
	        FieldObject tarObj = field.objectForPlayer(target, playerT); 
	        if(tarObj.canBeTargedet()) {
	            return ((Unit)field.objectForPlayer(attacker, playerA)).canAttack();
	        }
	    }
		return false;
	}
	
	public boolean attackIsValid(Unit attacker, FieldObject target) {
		if(field.containsOnDifferentSides(attacker, target) && gameRunning) {
		    return target.canBeTargedet() && attacker.canAttack();
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
	public void commitAttack(Unit attacker, FieldObject target) {
		String s = attacker.card.name + 
				" VS " + target.card.name;
		informAll(s);
		
		attacker.attackUnit(target);
		if(target.isDead()) {
			informAll(target.card.name + " is dead");
			this.triggerUnitEvents(target, units.TriggeringCondition.Condition.Die);
			field.removeObjectOfPlayer(target, target.player); 
		}
		if(attacker.isDead()) {
			informAll(attacker.card.name + " is dead"); 
			this.triggerUnitEvents(attacker, units.TriggeringCondition.Condition.Die);
			field.removeObjectOfPlayer(attacker, attacker.player); 
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
			Unit attacker = (Unit)field.objectForPlayer(a, pa);
			FieldObject target = field.objectForPlayer(t, pt);
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
	    
	    if(c.type == CardType.Building || c.type == CardType.Unit) {
	        return playersData[player].canPlayCard(c) 
	                && field.isSpaceAvailable(player, c.type == CardType.Building);
	    } 
		else if(c.type == CardType.Spell) 
			return playersData[player].canPlayCard(c) 
			        & ((SpellCard)c).spell.validate(player, this);
		else if(c.type == CardType.Developer && devMode) return true;
		else if(c.type == CardType.Energy) return true;
		else return false;
	}
	
	public FieldObject addObject(FieldObject o, int player) {
	    try {
            if(field.canObjectBeAdded(o, player)) {
                o.respondToEvent(new TriggeringCondition(TriggeringCondition.Condition.BeforeSpawn), null);
                field.addObject(o, player);
                this.triggerUnitEvents(o, units.TriggeringCondition.Condition.Spawn);
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
        
        return o;
	}
	
	public FieldObject createObject(BasicCard bc, int player) {
	    FieldObject o;
		if(bc instanceof BuildingCard) { 
		    o = factory.createBuilding((BuildingCard)bc, player, this);
		} else if(bc instanceof UnitCard) {
		    o = factory.createUnit((UnitCard)bc, player, this);
		} else {
		    System.err.println("Can't create unit: Card is neither Building nor Unit");
		    return null;
		}
		return addObject(o, player);
	}
	
    @Override
    public boolean canUseBuilding(int building, int player) {
        if(player != playerTurn || playingCard || !gameRunning) return false;
        if(building < 0) building = Math.abs(building) - 1;
        
        if(field.allBuildingsFromOneSide(player).size() > building) {
            if(field.allBuildingsFromOneSide(player).get(building) instanceof Building) {
                Building b = (Building)field.allBuildingsFromOneSide(player).get(building);
                return b.productAvailable() && b.productCost() <= playersData[player].getAvailableMana();
            }
        }
        return false;
    }

    @Override
    public void useBuildingCard(int building, int player) {
        if(building < 0) building = Math.abs(building) - 1;
        if(!canUseBuilding(building, player)) return;
        
        if(field.allBuildingsFromOneSide(player).size() > building) {
            if(field.allBuildingsFromOneSide(player).get(building) instanceof Building) {
                field.globalEvent(units.TriggeringCondition.GlobalCondition.CardPlayed);
                Building b = (Building)field.allBuildingsFromOneSide(player).get(building);
                BasicCard c = b.takeProduct();
                playersData[player].forceReceive(c);
                playCard(c, player);
            }
        }
    }
	
	/**
	 * Plays card of player. Summons unit, or casts spell, depending of card type.
	 * @param c
	 * @param player
	 */
	public void playCard(BasicCard c, int player) {
	    synchronized(playersData[player]) {
    		if(canPlayCard(c, player)) {
    		    field.globalEvent(units.TriggeringCondition.GlobalCondition.CardPlayed);
    		    playingCard = true;
    			// Drawing
    			players.get(player).reciveAction("Playing " + c.name);
    			players.get((player + 1) % 2).reciveAction("Opponent plays " + c.name);
    			
    			if(c.type == CardType.Building || c.type == CardType.Unit) {
    			    FieldObject fo = createObject(c, player);
    			    
                    if(fo == null) 
                        players.get(player).reciveAction("Can't add that");
                    else {
                        playersData[player].playCard(c);
                    }
    			} else if (c.type == CardType.Spell) {
    			    field.globalEvent(units.TriggeringCondition.GlobalCondition.SpellUsed);
    				SpellCard card = (SpellCard)c;	
    				if(card.validate(player, this)) {
    					card.exequte(player, this);
    					playersData[player].playCard(c);
    				}
    			} else if (c.type == CardType.Developer) {
    			    switch(((DevCard)c).devType) {
                        case Draw1Card:
                            playersData[player].pullCard(1, false);
                            break;
                        case Draw5Cards:
                            playersData[player].pullCard(5, false);
                            break;
                        case InfiniteMana:
                            playersData[player].playCard(c);
                            playersData[player].devModeMana();
                            playersData[(player + 1) % 2].devModeMana();
                            break;
                        default:
                            break;   
    			    }
    			} else if(c.type == CardType.Energy) {
    			    playersData[player].recieveEnergySpell(c);
    			}
    		}		
	    }
		recalculateFieldModifiers();
		updateInfoForAll();
		playingCard = false;	
	    
	}
	
	/**
	 * Triggers you-condition for FieldObject and otherUnit-condition for all remaining objects. 
	 * @param u unit
	 * @param c condition
	 */
	public void triggerUnitEvents(FieldObject u, TriggeringCondition.Condition c) {
	    TriggeringCondition youc = new TriggeringCondition(c);
	    u.respondToEvent(youc, null);
	    TriggeringCondition ouc = TriggeringCondition.otherFromYouCondition(youc, u);
	    field.passEventAboutObject(u, ouc);
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
	
	public FieldObject askPlayerForTarget(int player) {
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
	    boolean shouldRepeat = true;
	    while(shouldRepeat) {
	        shouldRepeat = false;
    		for(int i = 0; i < 2; i++) {
    		    playersData[i].auras.calculateModifiers();
    		    if(field.playerLost(i)) endGame(i);
    		        
    			int[] modifiers = playersData[i].auras.getModifiers();
    			ArrayList<FieldObject> objects = field.allObjectsFromOneSide(i, true);
    			for(int j = 0; j < objects.size(); j++) {
    			    FieldObject u = objects.get(j);
    				if(u.isDead()) {
    					informAll(u.card.name + " is dead");
    					field.removeObjectOfPlayer(u, i);
    					this.triggerUnitEvents(u, units.TriggeringCondition.Condition.Die);
    					
    					if(playersData[i].auras.unitDies(u)) { 
    						recalculateFieldModifiers();
    						return;
    					}
    					j = 0;
    					objects = field.allObjectsFromOneSide(i, true);
    				}
    				u.applyNewModifiers(modifiers[2], modifiers[1]);
                    u.modQualities = 0;
    			}
    			objects = field.allObjectsFromOneSide(i, true);
    			TriggeringCondition alw = new TriggeringCondition(units.TriggeringCondition.GlobalCondition.Always);
                for(int j = 0; j < objects.size(); j++) { // Iteration 2: trigger UnitPowers with condition "always"
    			    FieldObject u = objects.get(j);
    				u.respondToEvent(alw, null);
    			}
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
	public void passEventAboutUnit(FieldObject u, TriggeringCondition e) {
		field.passEventAboutObject(u, e);  
	}
	
	/** For tests only */
	public void applyFieldSituation(FieldSituation newField) {
		field = newField;
	}

	private void endGame(int looser) {
	    if(!gameRunning) return;
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
	
	public boolean gameStillRunning() {
	    return gameRunning;
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

    @Override
    public void pullBaseCard(int player) {
        playersData[player].pullCard(1, true);
        updateInfoForAll();
    }

    @Override
    public int getResourceForPlayer(int player, boolean energy) {
        return (energy) ? playersData[player].getAvailableEnergy() : playersData[player].getAvailableMana();
    }

    @Override
    public boolean drainResourceForPlayer(int player, boolean energy, int value) {
        if(energy) {
            if(playersData[player].getAvailableEnergy() >= value) {
                playersData[player].drainResource(true, value);
                return true;
            } 
        } else {
            if(playersData[player].getAvailableMana() >= value) {
                playersData[player].drainResource(false, value);
                return true;
            }
        }
        return false;
    }
    
    public void removeObjectAura(FieldObject o) {
        if(playersData[o.player].auras.unitDies(o)) { 
            recalculateFieldModifiers();
            return;
        }
    }
}
