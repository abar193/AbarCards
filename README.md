Hearthstone-inspired card game I'm working on.

Written in Java, this is my hobby-project. It's for learning purposes only, and should not be used for commercial purposes. If you are somehow 
going to use/fork/distribute this project, please contact me, I'll be happy. (Who am I kidding? No one will use that ever.) 

*** 
News: 

New mechanics added: Buildings. Like units, buildings may be played from the hand, but they can't attack: instead they produce a card 
once in X turns. Once building's card is used it will start creating new one. 

***  

Implemented testing framework. Custom classes in tests.framework package are allowing fully authomated real-game test. In test cases 
user may define order of cards in players decks, bot-player actions, and end-turn situations. If at some point exception will occur, or 
situation at the end of the turn will not match the one described in TestingSituation test will be failed. Example of using that framework 
may be seen in tests.TestMachinesDeck class. 

BTW, that framework may even be used for Tutorial-games, if I will implement them.
*** 

Older news: 

New GUI has been added. 

Now as you launch the game you will see deck picking screen. You may choose your race (so far only one) and build your deck with 15 cards. 

You may play against simple or passive bot, or click "Socket: vs Bot/vs Player" to play with simple bot/some other player over sockets. To click last button and not to crash the entire game you will have to configure and launch server from https://github.com/abar193/CardsServer. Since it's only early prealpha of the game, I don't have my own server online, but I'm working towards it.
*** 

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

    * Buildings

What I'm working on for now: 
    
	1 Networking
	
	2 User Interface 
	
	3 Extending game mechanics by adding new races and cards, and improving current ones.

    