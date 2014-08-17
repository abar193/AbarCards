package decks;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.ArrayList;
import java.util.Arrays;

import java.io.*;

import cards.*;
import cards.SpecialUnitCard.SpecialUnit;

import effects.AuraEffect;
import effects.AuraType;

/** 
 * Reads XML file with deck info. For example see NeutralsDeck.xml
 * @author Abar
 *
 */
public class DeckPackReader extends DefaultHandler {

	private ArrayList<BasicCard> resultingCard;
	private UnitCard unit;
	private SpellCard spell;
	
	public DeckPackReader() {
		resultingCard = new ArrayList<BasicCard>();
		unit = null;
		spell = null;
	}
	

	@Override
    public void startDocument() throws SAXException {
		resultingCard = new ArrayList<BasicCard>();
		unit = null;
		spell = null;
    }
    
    @Override
    public void endDocument() throws SAXException {
        unit = null;
        spell = null;
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
    			unit = new cards.SpecialUnitCard(health, dmg, cost, n, d);
    			int i = Integer.parseInt(atts.getValue("SpecID"));
    			((SpecialUnitCard)unit).specialUnitRef = SpecialUnit.fromInteger(i);
    		} else {
    			unit = new UnitCard(dmg, health, cost, n, d);
    		}
    	} else if(localName.equals("Spell")) {
    		
    	} else if(localName.equals("Qualities")) {
    		unit.qualities = Integer.parseInt(atts.getValue("v"));
    	} else if(localName.equals("Fulldesc")) {
    		unit.fullDescription = atts.getValue("txt");
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
    	} else if(spell != null && localName.equals("Spell")) {
    		resultingCard.add(spell);
    		spell = null;
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
    		return null;
    	}
        
    }
    
	public static void main(String[] args) throws Exception {
        
        
	}

}
