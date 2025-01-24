My Fantasy -> Game-like Rest api :)

**Postman Collection attached for easier use and testing.**

**H2 file mode used for simplicity -> that means if application is running then context spinnup test will fail**

**Shop** -- TradeController

- In order to use shop firstly items needs to be generated in the shop and then we can buy them if given character has
  money and is on the SHOP location.
- Players can trade with each other if they are in the same location

**Fight** -- ActionController

- Player can fight on location MONSTER if monster is alive and locationBiome is not VILLAGE.
- Threat level is a good indicator if in given location there is a monster to fight
- Player statistics are not changed with armor and weapon, although best armor and weapon are taken into calculation of
  damage.
- During the fight attacks can miss. Calculation is based on agility of attacker and defender
- Players cannot fight with each other
- Damage is calculated based on attack and defenses
- RESTING NOT IMPLEMENTED, you cannot get hp back.

**Navigate** -- ActionController

- Player can navigate in 4 main directions of the world

**Characters** -- CharacterController
- Characters need to be created
- Shopkeeper is also a character (you cannot play with him :))

**Items** -- ItemsController
- Items can be viewed.
- Only way to get items is by earning money through fight and buying them from shop
- HP potions are added but cannot be used :( (level up and you will be healed:D ) 


