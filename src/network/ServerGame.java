package network;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import cards.BasicCard;
import cards.CardJSONOperations;
import cards.UnitCard;
import src.GameInterface;
import src.MenuController;
import src.ProviderGameInterface;
import units.TriggeringCondition;
import units.Unit;

import javax.websocket.*;

import org.json.simple.*;
import org.json.*;

import effects.AbstractSpell;
import effects.AuraEffect;
import players.PlayerData;
import players.PlayerOpenData;
import src.FieldSituation;

/**
 * Represents client side of the GameInterface, revives user input and sends it to websocket server. 	
 * @author Abar
 *
 */

@ClientEndpoint
public class ServerGame implements GameInterface, ProviderGameInterface {
	
	private static ServerGame instance;
	private static boolean initialising = false;
	
	private Object o = new Object();
	private CardJSONOperations jsonop = new CardJSONOperations();
	private Session s;
	private players.PlayerInterface pli;
	private FieldSituation latestSituation;
	private MenuController controller; 
	
	EnumMap<PossibleServerActions, String> response = new EnumMap<PossibleServerActions, String>(PossibleServerActions.class);
			
	public static ServerGame instance() {
		if(!initialising) {
			createServerGame();
		}
		while(instance == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	public static void createServerGame() {
		try {
			initialising = true;
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			String uri = "ws://localhost:8080/sock";
			container.connectToServer(ServerGame.class, URI.create(uri));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ServerGame() {
		instance = this;
	}
	
	public void setPI(players.PlayerInterface pli) {
		this.pli = pli;
	}
	
	public void setMenuController(MenuController mc) {
		controller = mc;
	}
	
	@OnOpen
	public void onOpen(Session session) {
		s = session;
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("Got msg: " + message);
		
		if(message.equals("Hi")) return; // connected
		
		try {
			final JSONObject jobj = (JSONObject) JSONValue.parse(message);
			if(jobj.containsKey("target")) {
				if(((String)jobj.get("target")).equals("player")) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							playerAnalyser(jobj);
						}
					}).start();
				} else if(((String)jobj.get("target")).equals("menu")) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							menuAnalyser(jobj);
						}
					}).start();
				}
				return; 
			} else if(jobj.containsKey("response")) {
				response.put(PossibleServerActions.fromString((String)jobj.get("response")),
						(String)jobj.get("status"));
			}
		} catch (Exception e) {
			System.err.println("Error in message: " + message);
		}
	}

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println(closeReason.toString());
    }
    
	private String sendMessageAndAwaitAnswer(String message, String action) {
		System.out.println("Sending: " + message);
		synchronized(s) {
			if (s.isOpen()) {
				try {
					response.put(PossibleServerActions.fromString(action), null);
					s.getBasicRemote().sendText(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else System.err.println("Session closed");
		}
		
		try {
			while(response.get(PossibleServerActions.fromString(action)) == null) {
				Thread.sleep(100);
			}
			return response.get(PossibleServerActions.fromString(action));
		} catch (InterruptedException e) {
		}
		return null;
	}
	
	private void sendMessage(String message) {
		System.out.println("Sending: " + message);
		synchronized(s) {
			if (s.isOpen()) {
				try {
					s.getBasicRemote().sendText(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else System.err.println("Session closed");
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean validateDeck(ArrayList<BasicCard> c, String enemy) {
		JSONArray jarr = new JSONArray();
		Iterator<BasicCard> i = c.iterator();
		while(i.hasNext()) {
			jarr.add(jsonop.mapFromCard(i.next()));
		}
		
		JSONObject obj = new JSONObject();
		obj.put("deck", jarr);
		obj.put("opponent", enemy);
		
		String resp = sendMessageAndAwaitAnswer(JSONValue.toJSONString(obj), "deck");
		System.out.println("Validate got " + resp);
		
		switch(resp) {
			case ServerResponses.ResponseOk: 
				controller.deckApproved();
				return true;
			case ServerResponses.ResponseFail: 
				return false;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean canPlayCard(BasicCard c, int player) {
		JSONObject jobj = new JSONObject();
		jobj.put("action", "canPlayCard");
		jobj.put("card", jsonop.mapFromCard(c));
		jobj.put("player", Integer.toString(player));
		
		String resp = sendMessageAndAwaitAnswer(JSONValue.toJSONString(jobj), "canPlayCard");
		System.out.println("Resp got" + resp);
		if(resp.equals(ServerResponses.ResponseIllegal)) {
			System.err.println("Illegal move in canPlayCard with output msg = " + JSONValue.toJSONString(jobj));
		}
		return resp.equals(ServerResponses.ResponseTrue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void playCard(BasicCard c, int player) {
		JSONObject jobj = new JSONObject();
		jobj.put("action", "playCard");
		jobj.put("card", jsonop.mapFromCard(c));
		jobj.put("player", Integer.toString(player));
		String resp = sendMessageAndAwaitAnswer(JSONValue.toJSONString(jobj), "playCard");
		if(!resp.equals(ServerResponses.ResponseOk)) {
			System.err.println("Not ok in playCard, response: " + resp);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean attackIsValid(int attacker, int target, int playerA) {
		JSONObject jobj = new JSONObject();
		jobj.put("action", "attackIsValid");
		jobj.put("attacker", Integer.toString(attacker));
		jobj.put("target", Integer.toString(target));
		jobj.put("player", Integer.toString(playerA));
		
		String resp = sendMessageAndAwaitAnswer(JSONValue.toJSONString(jobj), "attackIsValid");
		if(resp.equals(ServerResponses.ResponseIllegal)) {
			System.err.println("Illegal move in attackIsValid with output msg = " + JSONValue.toJSONString(jobj));
		}
		return resp.equals(ServerResponses.ResponseTrue);
	}

	@Override
	public void commitAttack(Unit attacker, Unit target) {
		System.err.println("CommitAttack(Unit, Unit) for ServerGame not implemented!");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void commitAttack(int a, int t, int pa) {
		JSONObject jobj = new JSONObject();
		jobj.put("action", "commitAttack");
		jobj.put("attacker", Integer.toString(a));
		jobj.put("target", Integer.toString(t));
		jobj.put("player", Integer.toString(pa));
		
		String resp = sendMessageAndAwaitAnswer(JSONValue.toJSONString(jobj), "commitAttack");
		if(!resp.equals(ServerResponses.ResponseOk)) {
			System.err.println("Not ok in playCard, response: " + resp);
		}
	}

	@Override	
	public void endTurn(int player) {		
		JSONObject jobj = new JSONObject();
		jobj.put("action", "endTurn");
		jobj.put("player", Integer.toString(player));
		sendMessageAndAwaitAnswer(JSONValue.toJSONString(jobj), "endTurn"); 
	}

	/** Passes to player "select player" request. */
	public void selectTarget() {
		pli.selectTarget();
	}
	
	@SuppressWarnings("unchecked")
    public void play() {
        JSONObject obj = new JSONObject();
        obj.put("action", "play");
        
        String resp = sendMessageAndAwaitAnswer(JSONValue.toJSONString(obj), "play");
        System.out.println("Play got " + resp);
        if(resp.equals(ServerResponses.ResponseIllegal)) {
            System.err.println("Illegal move in play with output msg = " + JSONValue.toJSONString(obj));
        }
        if(resp.equals(ServerResponses.ResponseOk))
            controller.gameCreated();
        else if(resp.equals(ServerResponses.ResponseWait))
            controller.waitForGame();
    }
	
	/** Analyses responses, that are coming for MenuController. */
	public void menuAnalyser(JSONObject jobj) {
		switch((String)jobj.get("action")) {
			case "play": {
			    String status = (String)jobj.get("status");
			    if(status.equals(ServerResponses.ResponseOk)) {
			        controller.gameCreated();
			    } else if(status.equals(ServerResponses.ResponseWait)) {
			        controller.waitForGame();
			    }
				break;
			}
		}
	}
	
	/** Analyses responses, that are coming for RealPlayer. */
	public void playerAnalyser(JSONObject jobj) {
		while(pli == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		switch((String)jobj.get("action")) {
			case "run":
				pli.run();
				break;
			case "reciveAction":
				pli.reciveAction((String)jobj.get("message"));
				break;
			case "reciveInfo": {
				PlayerData pd = new PlayerData((Map)jobj.get("yourData"), this);
				FieldSituation fs = new FieldSituation((Map)jobj.get("field"), this);
				latestSituation = fs;
				PlayerOpenData pod = new PlayerOpenData((Map)jobj.get("enemyData"));
				pli.reciveInfo(pd, fs, pod);
				break;
			}
			case "selectTarget": {
				Unit u = pli.selectTarget();
				JSONObject resObj = new JSONObject();
				resObj.put("return", "selectTarget");
				resObj.put("side", Integer.toString(u.myPlayer));
				resObj.put("position", Integer.toString(latestSituation.unitPosition(u)));
				sendMessage(JSONValue.toJSONString(resObj));
			}
		}
	}

    @Override
    public FieldSituation provideFieldSituation() {
        System.err.println("On serverGame should not triggered ProviderGameInterface methods");
        return latestSituation;
    }

    @Override
    public Unit createUnit(UnitCard uc, int player, boolean fromSpell) {
        System.err.println("On serverGame should not triggered ProviderGameInterface methods");
        return null;
    }

    @Override
    public Unit askPlayerForTarget(int player) {
        System.err.println("On serverGame should not triggered ProviderGameInterface methods");
        return null;
    }

    @Override
    public void applySpellToPlayer(int player, AbstractSpell spell) {
        System.err.println("On serverGame should not triggered ProviderGameInterface methods");
    }

    @Override
    public void informAll(String m) {
        System.err.println("On serverGame should not triggered ProviderGameInterface methods");
    }

    @Override
    public void addAuraForPlayer(int player, AuraEffect ae) {
        System.err.println("On serverGame should not triggered ProviderGameInterface methods");
    }

    @Override
    public void informLostCards(ArrayList<BasicCard> cards, int player) {
        System.err.println("On serverGame should not triggered ProviderGameInterface methods");
    }

    @Override
    public void passEventAboutUnit(Unit u, TriggeringCondition e) {
        System.err.println("On serverGame should not triggered ProviderGameInterface methods");
    }	
}
