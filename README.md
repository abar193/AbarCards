Hearthstone-inspired card game I'm working on.

Written in Java, right now I'm working on its engine, later I'm planning to implement some AI, visual interface and networking.

How to play: 

As you launch the game you will be chosen as first or second player. Depending on that you will either make the first turn with 3 cards, or 
go after bot player, but with 4 cards. The interface has 4 parts: 
    
    * Action-messages. Begin with "> ". Examples: "> You go second", "> You take 100 damage!"
    
    * Opponent-relevant information. Informs how many cards, currency or health your opponent has. Example: 
    "Enemy has 1/1 $ and 15 health. Enemy hand has 3 cards, deck 12 cards."
    
    * Field. Consist of 2 parts - top one contains enemy units, bottom one - yours. Units are being represented as cards. Some examples:
    0     Medic| - Top line contains the unit's number and name. You will use that number to select yours units, or target opponens
    i       H1A| - Second line contains short description. If it begins with 'i', then that unit has a full description (see below)
    |Cost:    2| - Third line is useless line with unit's cost. I'm considering throwing that away.
    | 0/  3 d/h| - Last one displays unit damage / health. As health drops below 0 unit dies.
    Some units may have in description line something like this: "<C>". It means unit has special qualities. There are 3 qualities so far: 
        * <S>tealth - unit cannot be attacked. Unit loses this quality after first strike he makes.
        * <C>hardge - unit can attack as soon as he spawned. 
        * <T>aunt - other units and hero cannot be attacked, while this one is alive. This quality is ignored, if unit has both Stealth and Taut.
    
    * Your hand. Shows what cards you have. Example: "q    Marine, 1/1, Un  1$|". This means that you have 1 *Un*it card, and you may play it for 1$ if you press 'q'. 
    Another example: "|w Airstrike, 5rt, Sp  4$|" - you have *Sp*ell card, and it costs 4$.
Once your turn starts you may give a command. You type them in a line, then as you press enter they will execute. You may type one order at the 
time, or everything in a line - it doesn't matter. 
    
    * To force your unit to attack opponents enter your unit number to select it, and opponents unit number to attack. To choose an enemy hero 
    type '-'.

    * To play your card enter its command char like 'q', 'w', etc. Some spells may ask you to chose a target. 
    
    * Type 'i' to view full information about units and cards you see. 

    * Type 'h' for help

The goal of the game is to drop enemy health under 0. 


Things done so far: 

	* There are 2 players, each of them has his "deck" with a set of cards in randomised order. Those cards cannot be used, the player has to place them in "hand" by pulling the topmost card. At the beginning of every turn player pulls one card. If a player has more cards, then hand-limit allows, the card is discarded. If a player has no cards in the deck, he loses life instead.

	* Cards may be "played" to summon unit, or cast a spell. Units cannot attack on their creation turn, unless they have charge. Units may target enemy units, if there are no units with taunt-quality to defend. Now players can also be attacked.

    * Aura effects have been implemented, they may be associated with the unit, or last X turn. Auras affect unit health/damage or cards cost.

    * Spells have been completely reworked. Now a single spell may do whatever you want and even more.  

    * Turns last 2 minutes (unless RealPlayer's read() blocks interruptions).

    * Simple bot, capable of spawning and attacking random units. 

What I'm working on for now: 

    * Spell targeters should be able to target heroes as well

    * UI updates
    
	* I'd like to add "buildings" capable of creating cards for the player. 

    * Networking?