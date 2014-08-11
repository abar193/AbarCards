Hearthstone-inspired card game I'm working on.

Written in Java, right now I'm working on its engine, later I'm planning to implement some AI, visual interface and networking.

Things done so far: 
	* There are 2 players, each of them has his "deck" with a set of cards in randomised order. Those cards cannot be used, the player has to place them in "hand" by pulling the topmost card. At the beginning of every turn player pulls one card. If a player has more cards, then hand-limit allows, the card is discarded. If a player has no cards in the deck, he loses life instead.
	* Cards may be "played" to summon unit, or cast a spell. Units may attack from the next turn, or from this one, if they have charge. Units may attack units from the enemy side, unless there are units with taunt-quality to defend there.
	* Spells may affect single player-chosen target, all units from one side/both sides, or random units from either side.
	* So far spells may only affect unit properties like damage/health or qualities. 

What I'm working on for now: 
	* More spells effects - spells should affect player as well.
	* Aura-effects (from spells, or units) to apply some effect to all units, or their cost.
	* Units should be able to attack hero to drain its life - that's the point.
	* I'd like to add "buildings" capable of creating cards for the player. 