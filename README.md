Hearthstone-inspired card game I'm working on.

Written in Java, right now I'm working on its engine, later I'm planning to implement some AI, visual interface and networking.

Things done so far: 

	* There are 2 players, each of them has his "deck" with a set of cards in randomised order. Those cards cannot be used, the player has to place them in "hand" by pulling the topmost card. At the beginning of every turn player pulls one card. If a player has more cards, then hand-limit allows, the card is discarded. If a player has no cards in the deck, he loses life instead.

	* Cards may be "played" to summon unit, or cast a spell. Units cannot attack on their creation turn, unless they have charge. Units may target enemy units, if there are no units with taunt-quality to defend. Now players can also be attacked.

    * Aura effects have been implemented, they may be associated with the unit, or last X turn. Auras affect unit health/damage or cards cost.

    * Spells have been completely reworked. Now a single spell may do whatever you want and even more.  

What I'm working on for now: 

	* I'd like to add "buildings" capable of creating cards for the player. 

    * Turns should last some time.

    * It would be nice to write some bots.

    * Networking?