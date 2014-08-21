package decks;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

import cards.*;
import cards.SpecialUnitCard.SpecialUnit;
import units.UnitPower;
import effects.AuraEffect;
import effects.AuraType;
import effects.AbstractSpell;

/** 
 * Reads XML file with deck info. For example see NeutralsDeck.xml
 * @author Abar
 *
 */
public class DeckPackReader extends DefaultHandler {

	private ArrayList<BasicCard> resultingCard;
	private UnitCard unit;
	private UnitPower power;
	private SpellCard spell;
	private SpellXMLBuilder spellBuilder;
	
	public DeckPackReader() {
		resultingCard = new ArrayList<BasicCard>();
		unit = null;
		spell = null;
		spellBuilder = null;
		power = null;
	}
	

	@Override
    public void startDocument() throws SAXException {
		resultingCard = new ArrayList<BasicCard>();
		unit = null;
		spell = null;
		spellBuilder = new SpellXMLBuilder();
		power = null;
    }
    
    @Override
    public void endDocument() throws SAXException {
        unit = null;
        spell = null;
        power = null;
    }
    
    @Override
    public void startElement(String namespaceURI, String localName, String qName, 
            Attributes atts) throws SAXException 
    {
    	if(localName.equals("Unit")) {
    		String n = atts.getValue("Name");
    		String d = atts.getValue("Description");
    		int dmg = Integer.parseInt(atts.getValue("Damage"));
    		int health = Integer.parseInt(atts.getValue("Health"));
    		int cost = Integer.parseInt(atts.getValue("Cost"));
    		
    		if(atts.getValue("SpecID") != null) {
    			unit = new cards.SpecialUnitCard(dmg, health, cost, n, d);
    			int i = Integer.parseInt(atts.getValue("SpecID"));
    			((SpecialUnitCard)unit).specialUnitRef = SpecialUnit.fromInteger(i);
    		} else {
    			unit = new UnitCard(dmg, health, cost, n, d);
    		}
    	} else if(localName.contains("Spell") || localName.contains("Buff")) {
    		SpellCard sc = spellBuilder.reciveOpenTag(localName, atts);
    		if(sc != null) {
    			spell = sc;
    		}
    	} else if(localName.contains("Power")) {
    		power = spellBuilder.reciveOpenPowerTag(localName, atts);
    	} else if(localName.equals("Qualities")) {
    		unit.qualities = Integer.parseInt(atts.getValue("v"));
    	} else if(localName.equals("Fulldesc")) {
    		if(unit != null) {
    			unit.fullDescription = atts.getValue("txt");
    		} else if(spell != null) {
    			spell.fullDescription = atts.getValue("txt");
    		}
    	} else if(localName.equals("Aura")) {
    		if(unit.auraEffects == null) {
    			unit.auraEffects = new AuraEffect[1];
    		} else {
    			AuraEffect[] ae = new AuraEffect[unit.auraEffects.length + 1];
    			System.arraycopy(unit.auraEffects, 0, ae, 0, unit.auraEffects.length);
    		}
    		int type = Integer.parseInt(atts.getValue("type"));
    		int value = Integer.parseInt(atts.getValue("value"));
    		AuraEffect aura = new AuraEffect(AuraType.fromInt(type), value, null);
    		unit.auraEffects[unit.auraEffects.length-1] = aura;
    	}
    }
    
    	
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException 
    {
    	if(unit != null && localName.equals("Unit")) {
    		resultingCard.add(unit);
    		unit = null;
    	} else if(localName.contains("Spell") || localName.contains("Buff")) {
    		AbstractSpell s = spellBuilder.reciveCloseTag(localName);
    		if(s != null) {
    			spell.spell = s;
    			resultingCard.add(spell);
    			spell = null;
    		}
    	} else if(localName.contains("Power")) {
    		AbstractSpell s = spellBuilder.reciveCloseTag(localName);
    		if(s != null) {
    			power.applySpell(s);
    			unit.power = power;
    			power = null;
    		}
    	}
    }
    
    
    public ArrayList<BasicCard> parseFile(String filename) {
    	try {
	    	SAXParserFactory spf = SAXParserFactory.newInstance();
	        spf.setNamespaceAware(true);
	        SAXParser saxParser = spf.newSAXParser();
	        XMLReader xmlReader = saxParser.getXMLReader();
	        xmlReader.setContentHandler(this);
	        xmlReader.parse(filename);//
	        return resultingCard;
    	} catch (Exception e) {
    		System.out.println("Exception " + e.getMessage());
    		return null;
    	}
        
    }
    
	public static void main(String[] args) throws Exception {
        
		DeckPackReader dpr = new DeckPackReader();
		ArrayList<BasicCard> bc = dpr.parseFile("C:\\Users\\Abar\\Documents\\Uni\\Workspace\\AbarCards\\src\\decks\\NeutralsDeck.xml");
		for(BasicCard c : bc) {
			System.out.format("%s %s %d\n", c.name, c.fullDescription, c.cost);
		}
	}

}
