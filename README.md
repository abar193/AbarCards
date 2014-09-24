Hearthstone-inspired card game I'm working on.

Written in Java, right now I'm working on its engine, later I'm planning to implement some AI, visual interface and networking.

New GUI has been added. 

Now as you launch the game you will see deck picking screen. You may choose your race (so far only one) and build your deck with 15 cards. 

You may play against simple or passive bot, or click "Socket: vs Bot" to play with simple bot over sockets. To click last button and not to 
crash the entire game you will have to configure and launch server with https://github.com/abar193/CardsServer classses installed. Since it's only 
early prototype of the game, I don't have my own server, nor I need one.

Things done so far: 

	* There are 2 players, each of them has his "deck" with a set of cards in randomised order. Those cards cannot be used, the player has to place them in "hand" by pulling the topmost card. At the beginning of every turn player pulls one card. If a player has more cards, then hand-limit allows, the card is discarded. If a player has no cards in the deck, he loses life instead.

	* Cards may be "played" to summon unit, or cast a spell. Units cannot attack on their creation turn, unless they have charge. Units may target enemy units, if there are no units with taunt-quality to defend. Now players can also be attacked.

    * Aura effects have been implemented, they may be associated with the unit, or last X turn. Auras affect unit health/damage or cards cost.

    * Spells have been completely reworked. Now a single spell may do whatever you want and even more.  

    * Turns last 2 minutes (unless RealPlayer's read() blocks interruptions).

    * Simple bot, capable of spawning and attacking random units. 

    * Spells can target heroes.
	
	* GUI, capable of doing a bit less than Console output, but looking prettier.
	
	* Websockets use, allowing me to separate Player and Game server. Not fully implemented

What I'm working on for now: 
    
	1 Networking
	
	2 User Interface 
	
	? I'd like to extend mechanics of the game, add new reaces and stuff

    