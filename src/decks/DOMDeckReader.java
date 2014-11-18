package decks;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import units.UnitFilter;
import units.UnitPower;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EmptyStackException;

import cards.BasicCard;
import cards.BuildingCard;
import cards.SpecialUnitCard;
import cards.SpellCard;
import cards.UnitCard;
import cards.SpecialUnitCard.SpecialUnit;
import effects.*;
import units.TriggeringCondition;

public class DOMDeckReader implements DeckXMLReaderInterface {

    public DOMDeckReader() {

    }
    
    private AbstractSpell parseSpell(Element n) {
        switch(n.getNodeName()) {
            case "TargedetSpell": {
                String targ = n.getAttribute("targeter");
                Targeter t = TargeterBuilder.fromDOMElements(targ, n);
                TargedetSpell ts = new TargedetSpell(t, null);
                
                for(int i = 0; i < n.getChildNodes().getLength(); i++) {
                    Node s = n.getChildNodes().item(i); 
                    if(s.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element)s;
                        if(e.getNodeName().contains("Spell")) {
                            ts.spell = parseSpell(e);
                        } else if(e.getNodeName().contains("Filter")) {
                            ts.setFilter(parseFilter(e));
                        }  else {
                            System.err.println("Unknown element " + e.getNodeName() 
                                    + " inside " + n.getNodeName());
                        } 
                    }
                }
                return ts;
            }
            case "SpellContainer": {
                SpellContainer sc = new SpellContainer(new ArrayList<AbstractSpell>());
                for(int i = 0; i < n.getChildNodes().getLength(); i++) {
                    Node s = n.getChildNodes().item(i); 
                    if(s.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element)s;
                        if(e.getNodeName().contains("Spell")) {
                            sc.add(parseSpell(e));
                        } else if(e.getNodeName().contains("Filter")) {
                            sc.setFilter(parseFilter(e));
                        }  else {
                            System.err.println("Unknown element " + e.getNodeName() 
                                    + " inside " + n.getNodeName());
                        } 
                    }
                }
                return sc;
            }
            case "ConditionSpell": {
                ConditionSpell cs = new ConditionSpell();
                for(int i = 0; i < n.getChildNodes().getLength(); i++) {
                    Node s = n.getChildNodes().item(i); 
                    if(s.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element)s;
                        if(e.getNodeName().contains("Condition")) {
                            String type = e.getAttribute("type");
                            String count = e.getAttribute("count");
                            Condition c = new Condition(type, count);
                            cs.addCondition(c);
                        } else if(e.getNodeName().contains("Spell")) {
                            cs.addSpell(parseSpell(e));
                        } else if(e.getNodeName().contains("Filter")) {
                            cs.setFilter(parseFilter(e));
                        } else {
                            System.err.println("Unknown element " + e.getNodeName() 
                                    + " inside " + n.getNodeName());
                        } 
                    }
                }
                return cs;
            }
            case "BuffSpell": {
                BuffSpell bs = new BuffSpell(null);
                String type = n.getAttribute("type");
                bs.buff = new Buff(type, n.getAttribute("v"));
                return bs;
            } 
            case "PlayerValueSpell": {
                int value = Integer.parseInt(n.getAttribute("value"));
                int filter = Integer.parseInt(n.getAttribute("acept"));
                return new PlayerValueSpell(n.getAttribute("type"), value, filter);
            }
            case "CreateUnitSpell": {
                int side = Integer.parseInt(n.getAttribute("side"));
                CreateUnitSpell cus = new CreateUnitSpell(null, n.getAttribute("count"), side);
                for(int i = 0; i < n.getChildNodes().getLength(); i++) {
                    Node s = n.getChildNodes().item(i); 
                    if(s.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element)s;
                        if(e.getNodeName().contains("HelperUnit")) {
                            cus.myUnit = parseUnit(e);
                        } else {
                            System.err.println("Unknown element " + e.getNodeName() 
                                    + " inside " + n.getNodeName());
                        } 
                    } 
                }
                
                return cus;
            }
            default: {
                System.err.println("Unknown spell-tag <" + n.getNodeName() + ">");
                return null;
            }

        }
    }
    
    private UnitFilter parseFilter(Element f) {
        return new UnitFilter(f.getAttribute("type"), f.getAttribute("v"));
    }
    
    private UnitCard parseUnit(Element n) {
        String name = n.getAttribute("Name");
        String d = n.getAttribute("Description");
        int dmg = Integer.parseInt(n.getAttribute("Damage"));
        int health = Integer.parseInt(n.getAttribute("Health"));
        int cost = Integer.parseInt(n.getAttribute("Cost"));
        UnitCard tmp;
        if(n.getAttribute("SpecID") != "") {
            SpecialUnitCard uc = new SpecialUnitCard(dmg, health, cost, name, d);
            int i = Integer.parseInt(n.getAttribute("SpecID"));
            uc.specialUnitRef = SpecialUnit.fromInteger(i);
            tmp = uc;
        } else {
            tmp = new UnitCard(dmg, health, cost, name, d);
        }
        
        tmp.cardClass = n.getAttribute("Class");
        
        for(int i = 0; i < n.getChildNodes().getLength(); i++) {
            Node s = n.getChildNodes().item(i); 
            if(s.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element)s;
                if(e.getNodeName().contains("Power")) {
                    tmp.power = parsePower(e);
                } else if(e.getNodeName().contains("Fulldesc")) {
                    tmp.fullDescription = e.getAttribute("txt");
                } else if(e.getNodeName().contains("Qualities")) {
                    tmp.qualities = Integer.parseInt(e.getAttribute("v"));
                } else if(e.getNodeName().contains("Aura")) {
                    if(tmp.auraEffects == null) {
                        tmp.auraEffects = new AuraEffect[1];
                    } else {
                        AuraEffect[] ae = new AuraEffect[tmp.auraEffects.length + 1];
                        System.arraycopy(tmp.auraEffects, 0, ae, 0, tmp.auraEffects.length);
                    }
                    int type = Integer.parseInt(e.getAttribute("type"));
                    int value = Integer.parseInt(e.getAttribute("value"));
                    AuraEffect aura = new AuraEffect(AuraType.fromInt(type), value, null);
                    tmp.auraEffects[tmp.auraEffects.length-1] = aura;
                } else {
                    System.err.println("Unknown element " + e.getNodeName() 
                            + " inside " + n.getNodeName());
                } 
            }
        }
        
        return tmp;
    }
    
    public UnitPower parsePower(Element n) {
        UnitPower up = new UnitPower(TriggeringCondition.fromString(n.getAttribute("condition")));
        for(int i = 0; i < n.getChildNodes().getLength(); i++) {
            Node s = n.getChildNodes().item(i); 
            if(s.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element)s;
                if(e.getNodeName().contains("Spell")) {
                    up.applySpell(parseSpell(e));
                } else if(e.getNodeName().contains("Filter")) {
                    up.filter = parseFilter(e);
                }  else {
                    System.err.println("Unknown element " + e.getNodeName() 
                            + " inside " + n.getNodeName());
                } 
            }
        }
        return up;
    }
    
    public SpellCard parseSpellCard(Element n) {
        int cost = Integer.parseInt(n.getAttribute("Cost"));
        SpellCard sc = new SpellCard(n.getAttribute("Name"), n.getAttribute("Description"), cost, null);
        for(int i = 0; i < n.getChildNodes().getLength(); i++) {
            Node s = n.getChildNodes().item(i); 
            if(s.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element)s;
                if(e.getNodeName().contains("Spell")) {
                    sc.spell = parseSpell(e);
                } else if(e.getNodeName().contains("Fulldesc")) {
                    sc.fullDescription = e.getAttribute("txt");
                } else if(e.getNodeName().contains("Filter")) {
                    sc.setFilter(parseFilter(e));
                }
            }
        }
        return sc;
    }
    
    public BuildingCard parseBuilingCard(Element n) {
        int cost = Integer.parseInt(n.getAttribute("Cost"));
        int dmg = 0;
        if(!n.getAttribute("Damage").equals("")) 
            dmg = Integer.parseInt(n.getAttribute("Damage"));
        int health = Integer.parseInt(n.getAttribute("Health"));
        BuildingCard bc = new BuildingCard(dmg, health, cost, n.getAttribute("Name"), 
                n.getAttribute("Description"));
        bc.productionTime = Integer.parseInt(n.getAttribute("Production"));
        bc.turnProgress = Integer.parseInt(n.getAttribute("Progress"));
        
        for(int i = 0; i < n.getChildNodes().getLength(); i++) {
            Node s = n.getChildNodes().item(i); 
            if(s.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element)s;
                if(e.getNodeName().contains("Spell")) {
                    bc.product = parseSpellCard(e);
                } else if(e.getNodeName().contains("Unit")) {
                    bc.product = parseUnit(e);
                } else if(e.getNodeName().contains("Fulldesc")) {
                    bc.fullDescription = e.getAttribute("txt");
                } else if(e.getNodeName().contains("Qualities")) {
                    bc.qualities = Integer.parseInt(e.getAttribute("v"));
                } else if(e.getNodeName().contains("Aura")) {
                    if(bc.auraEffects == null) {
                        bc.auraEffects = new AuraEffect[1];
                    } else {
                        AuraEffect[] ae = new AuraEffect[bc.auraEffects.length + 1];
                        System.arraycopy(bc.auraEffects, 0, ae, 0, bc.auraEffects.length);
                    }
                    int type = Integer.parseInt(e.getAttribute("type"));
                    int value = Integer.parseInt(e.getAttribute("value"));
                    AuraEffect aura = new AuraEffect(AuraType.fromInt(type), value, null);
                    bc.auraEffects[bc.auraEffects.length-1] = aura;
                } else {
                    System.err.println("Unknown element " + e.getNodeName() 
                            + " inside " + n.getNodeName());
                } 
            }
        }
        
        return bc;
    }
    
    @Override
    public ArrayList<BasicCard> parseFile(String filename) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(getClass().getResource(filename).toURI().toString());
            doc.getDocumentElement().normalize();
            
            ArrayList<BasicCard> cards = new ArrayList<BasicCard>(20);
            
            NodeList nList = doc.getElementsByTagName("Deck").item(0).getChildNodes();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if(nNode.getNodeType() == Node.ELEMENT_NODE) 
                {
                    Element e = (Element) nNode;
                    if(e.getNodeName().equals("Spell")) {
                        cards.add(parseSpellCard(e));
                    } else if(e.getNodeName().equals("Unit")) { 
                        cards.add(parseUnit(e));
                    } else if(e.getNodeName().equals("Building")) { 
                        cards.add(parseBuilingCard(e));
                    } else {
                        System.out.println(e.getNodeName());
                    }
                }   
            }
            
            Collections.sort(cards, new Comparator<BasicCard>() {
                @Override
                public int compare(BasicCard arg0, BasicCard arg1) {
                    return arg0.cost - arg1.cost;
                }
            });
            return cards;
        } catch (ParserConfigurationException | SAXException | IOException | URISyntaxException e){
            
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args) throws Exception {    
        DOMDeckReader dpr = new DOMDeckReader();
        ArrayList<BasicCard> bc = dpr.parseFile("MachinesDeck.xml");
        if(bc == null) return;
        for(BasicCard c : bc) {
            System.out.format("Card %s %s %s", c, c.description, c.fullDescription);
            if(c instanceof SpellCard) {
                System.out.println(((SpellCard)c).spell);
            } else if(c instanceof UnitCard) {
                UnitCard uc = (UnitCard)c;
                if(uc.power != null) System.out.println(uc.power.toString());
                else System.out.println();
            }
        }
    }

}
