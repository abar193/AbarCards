package decks;

import org.xml.sax.Attributes;

import java.util.EmptyStackException;
import java.util.Stack;

import effects.*;
import cards.SpellCard;

import java.util.ArrayList;

public class SpellXMLBuilder {

	Stack<AbstractSpell> stack;
	Buff b;
	
	public SpellXMLBuilder() {
		stack = new Stack<AbstractSpell>();
	}
	
	public SpellCard reciveOpenTag(String tag, Attributes att) {

		switch(tag) {	
		case "Spell": {
			String n = att.getValue("Name");
    		String d = att.getValue("Description");
    		int cost = Integer.parseInt(att.getValue("Cost"));
			SpellCard sc = new SpellCard(n, d, cost, null);
			return sc;
		}
		case "TargedetSpell": {
			String targ = att.getValue("targeter");
			Targeter t = TargeterBuilder.fromName(targ, att);
			TargedetSpell ts = new TargedetSpell(t, null);
			stack.push(ts);
			break;
		}
		case "SpellContainer": {
			SpellContainer sc = new SpellContainer(new ArrayList<AbstractSpell>());
			stack.push(sc);
			break;
		}
		case "BuffSpell": {
			BuffSpell bs = new BuffSpell(null);
			stack.push(bs);
			break;
		} 
		case "PlayerValueSpell": {
			int value = Integer.parseInt(att.getValue("value"));
			int filter = Integer.parseInt(att.getValue("acept"));
			PlayerValueSpell pvs = new PlayerValueSpell(att.getValue("type"), value, filter);
			stack.push(pvs);
			break;
		}
		case "Buff": {
			String type = att.getValue("type");
			int v = Integer.parseInt(att.getValue("v"));
			b = new Buff(type, v);
			break;
		}
		default: { 
			System.out.println("Error! Unknown tag: " + tag);
		}
		}
		return null;
	}

	public AbstractSpell reciveCloseTag(String tag) {
		switch(tag) {	
		case "Spell": {
			return stack.pop();
		}
		case "TargedetSpell": 
		case "SpellContainer":
		case "BuffSpell": 
		case "PlayerValueSpell":
		{
			AbstractSpell ls = stack.pop();
			AbstractSpell ps;
			try{
				ps = stack.peek();
			} catch(EmptyStackException e) {
				stack.push(ls); // last spell in hierarchy, do nothing
				return null;
			}
			if(ps == null) {
				return null;
			} else if(ps instanceof TargedetSpell) {
				((TargedetSpell) ps).spell = ls;
				return null;
			} else if(ps instanceof SpellContainer) {
				((SpellContainer) ps).add(ls);
				return null;
			}
			break;
		} 
		case "Buff": {
			AbstractSpell a = stack.peek();
			if(a instanceof BuffSpell) {
				BuffSpell buff = (BuffSpell)a;
				buff.buff = b;
				b = null;
				return null;
			}
			break;
		}
		default: { 
			System.out.println("Error! Unknown tag: " + tag);
		}
		}
		System.out.println("Parsing Error! Corrupt XML hierarchy at tag " + tag);
		return null;
	}
	
}
