package network;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import cards.BasicCard;
import cards.CardJSONOperations;
import src.GameInterface;
import units.Unit;

import javax.websocket.*;

import org.json.simple.*;
import org.json.*;

import players.PlayerData;
import players.PlayerOpenData;
import src.FieldSituation;

/**
 * Represents client side of the GameInterface, revives user input and sends it to websocket server. 	
 * @author Abar
 *
 */

@ClientEndpoint
public class ServerGame implements GameInterface {
	
	private static ServerGame instance;
	private static boolean initialising = false;
	
	Object o = new Object();
	String responseMessage;
	CardJSONOperations jsonop = new CardJSONOperations();
	Session s;
	players.PlayerInterface pli;

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
	
	@OnOpen
	public void onOpen(Session session) {
		s = session;
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("Got msg: " + message);
		
		if(message.equals("Hi")) return; // connected
		
		if(message.contains("target")) {
			JSONObject jobj = (JSONObject) JSONValue.parse(message);
			if(((String)jobj.get("target")).equals("player")) {
				playerAnalyser(jobj);
			}
			
			return;
		}
		while(responseMessage != null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		responseMessage = message;
	}


    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println(closeReason.toString());
    }
    
	private String sendMessageAndAwaitAnswer(String message) {
		System.out.println("Sending: " + message);
		synchronized(o) {
			try {
				responseMessage = null;
				s.getBasicRemote().sendText(message);
				while(responseMessage == null) {
					Thread.sleep(100);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				
			}
		}
		return responseMessage;
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
		
		String resp = sendMessageAndAwaitAnswer(JSONValue.toJSONString(obj));
	
		return resp.equals(ServerResponses.ResponseOk);
	}
	
	@SuppressWarnings("unchecked")
	public boolean play() {
		JSONObject obj = new JSONObject();
		obj.put("action", "play");
		
		String resp = sendMessageAndAwaitAnswer(JSONValue.toJSONString(obj));
		if(resp.equals(ServerResponses.ResponseIllegal)) {
			System.err.println("Illegal move in play with output msg = " + JSONValue.toJSONString(obj));
		}
		
		return resp.equals(ServerResponses.ResponseTrue);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean canPlayCard(BasicCard c, int player) {
		JSONObject jobj = new JSONObject();
		jobj.put("action", "canPlayCard");
		jobj.put("card", jsonop.mapFromCard(c));
		jobj.put("player", player);
		
		String resp = sendMessageAndAwaitAnswer(JSONValue.toJSONString(jobj));
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
		jobj.put("player", player);
		String resp = sendMessageAndAwaitAnswer(JSONValue.toJSONString(jobj));
		if(!resp.equals(ServerResponses.ResponseOk)) {
			System.err.println("Not ok in playCard, response: " + resp);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean attackIsValid(int attacker, int target, int playerA) {
		JSONObject jobj = new JSONObject();
		jobj.put("action", "attackIsValid");
		jobj.put("attacker", attacker);
		jobj.put("target", target);
		jobj.put("player", playerA);
		
		String resp = sendMessageAndAwaitAnswer(JSONValue.toJSONString(jobj));
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
		jobj.put("attacker", a);
		jobj.put("target", t);
		jobj.put("player", pa);
		
		String resp = sendMessageAndAwaitAnswer(JSONValue.toJSONString(jobj));
		if(!resp.equals(ServerResponses.ResponseOk)) {
			System.err.println("Not ok in playCard, response: " + resp);
		}
	}
	
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
			case "reciveInfo":
				PlayerData pd = new PlayerData((Map)jobj.get("yourData"));
				FieldSituation fs = new FieldSituation((Map)jobj.get("field"));
				PlayerOpenData pod = new PlayerOpenData((Map)jobj.get("enemyData"));
				pli.reciveInfo(pd, fs, pod);
				break;
		}
	}
	
	public static void main(String[] args) {
		ServerGame.instance().canPlayCard(null, 0);
	}
}