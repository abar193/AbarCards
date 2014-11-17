package decks;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
public class DeckPackReader extends DefaultHandler implements DeckXMLReaderInterface {

	private ArrayList<BasicCard> resultingCard;
	private UnitCard unit;
	private UnitCard helper;
	private UnitPower power;
	private SpellCard spell;
	private SpellXMLBuilder spellBuilder;
	private int deckNumber;
	
	private static final boolean IS_JAR = true;
	
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
    	if(localName.equals("Unit") || localName.equals("HelperUnit")) {
    		String n = atts.getValue("Name");
    		String d = atts.getValue("Description");
    		int dmg = Integer.parseInt(atts.getValue("Damage"));
    		int health = Integer.parseInt(atts.getValue("Health"));
    		int cost = Integer.parseInt(atts.getValue("Cost"));
    		UnitCard tmp;
    		if(atts.getValue("SpecID") != null) {
    			tmp = new cards.SpecialUnitCard(dmg, health, cost, n, d);
    			int i = Integer.parseInt(atts.getValue("SpecID"));
    			((SpecialUnitCard)tmp).specialUnitRef = SpecialUnit.fromInteger(i);
    		} else {
    			tmp = new UnitCard(dmg, health, cost, n, d);
    		}
    		
    		tmp.cardClass = atts.getValue("Class");
    		
    		if(localName.equals("Unit")) {
    			unit = tmp;
    			unit.deckNum = deckNumber;
    		} else {
    			helper = tmp;
    		}
    	} else if(localName.contains("Spell") || localName.contains("Buff") 
    	        || localName.contains("Condition")) 
    	{
    		SpellCard sc = spellBuilder.reciveOpenTag(localName, atts);
    		if(sc != null) {
    			spell = sc;
    			spell.deckNum = deckNumber;
    		}
    	} else if(localName.contains("Filter")) {
    		if(power != null) {
    			units.UnitFilter f = spellBuilder.reciveOpenFilterTag(localName, atts); 
    			if(f != null)
    				power.filter = f;
    		} else {
    			if(spellBuilder.reciveOpenFilterTag(localName, atts) != null)
					System.out.println("Error with filter tag");
    		}
    	} else if(localName.contains("Power")) {
    		power = spellBuilder.reciveOpenPowerTag(localName, atts);
    	} else if(localName.equals("Qualities")) {
    		if(helper != null)
    			helper.qualities = Integer.parseInt(atts.getValue("v"));
    		else 
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
    	} else if(!localName.equals("Deck")) {
    		System.out.println("Unknown tag  <" + localName);
    	} else if(localName.equals("Deck")) {
    		if(atts.getValue("num") != null)
    			deckNumber = Integer.parseInt(atts.getValue("num"));	
    	}
    }
    
    	
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException 
    {
    	if(unit != null && localName.equals("Unit")) {
    		resultingCard.add(unit);
    		unit = null;
    	} else if(localName.equals("CreateUnitSpell")) {
    		AbstractSpell s = spellBuilder.reciveCloseTag(localName);
    		if(s != null && s instanceof effects.CreateUnitSpell) {
    			((effects.CreateUnitSpell)s).myUnit = helper;
    			helper = null;
    		}
    	} else if(localName.contains("Spell") || localName.contains("Buff") || 
    	        localName.contains("Condition")) {
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
	        if(IS_JAR) {
	        	xmlReader.parse(getClass().getResource(filename).toString());
	        } else {
	        	xmlReader.parse("bin\\decks\\" + filename);
	        }
	        
	        Collections.sort(resultingCard, new Comparator<BasicCard>() {
	            @Override
	            public int compare(BasicCard arg0, BasicCard arg1) {
	                return arg0.cost - arg1.cost;
	            }

	        });
	        return resultingCard;
    	} catch (Exception e) {
    		System.out.println("Exception " + e.getMessage());
    		e.printStackTrace();
    		return null;
    	}
        
    }
    
	public static void main(String[] args) throws Exception {
        
		DeckPackReader dpr = new DeckPackReader();
		ArrayList<BasicCard> bc = dpr.parseFile("AliensDeck.xml");
		for(BasicCard c : bc) {
			System.out.format("%s %s %d %d\n", c.name, c.fullDescription, c.cost, c.deckNum);
		}
	}

}
