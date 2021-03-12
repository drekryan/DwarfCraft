---
name: "🏆 Skill Suggestion"
about: Suggest a new DwarfCraft skill
labels: Skill, Enhancement

---

#### Issue type:

- :trophy: Skill Suggestion

____

#### Skill name:

<!--A name you would like to suggest the skill be called.-->

#### Short description:

<!--A clear and concise description of the suggested new skill.-->

#### Training items (limited to 3 items):

<!--
A list of items the skill should require.
The base amount is essentially the minimum amount you must give a trainer
and this will scale up to the maximum amount for the highest level of the skill. For example if the Base is 2 and the Max is 10, the trainer to level up for the first time will ask for an amount of 2 for that item and as the player levels will gradually increase that to 10. The trainer will never ask for more than the maximum. 
-->
- Cobblestone/Blackstone
  - Base: 30 <!--The base amount required for leveling-->
  - Max: 1000 <!--The maximum amount required for leveling the skill.-->
- Item 2
  - Base: 4 <!--The base amount required for leveling-->
  - Max: 300 <!--The maximum amount required for leveling the skill.-->
- Item 3
  - Base: 1 <!--The base amount required for leveling-->
  - Max: 30 <!--The maximum amount required for leveling the skill.-->

#### Effects:
<!--
Effects are what gives value to your skill. You can have these start
at a value less than normal to make the player have to level the skill
up to a normal value first before getting more than vanilla. This adds
challenge to the game early on and encourages levelling. Add as many as you would like.
-->

- Effects the number of seeds dropped when tall_grass is broken
  - Type: BLOCKDROP <!-- This is what the effect is based on. Look at the bottom of this page for a list of Effect Types.-->
  - Origin: tall_grass<!--The Block/Item/Entity that triggers the effect. If the Type is MOBDROP or SHEAR then a mob can be specified. If no mob is specified then it applies the effect on all mobs that drop the `Output`.-->
  - Output: seeds<!--The output item of the effect once the block is broken or the item is activated.-->
  - Base: 0.6 <!--The Base Value of the effect. e.g. Tall_Grass drops 0.6 (1.0 is 100%) seeds when broken.-->
  - Level Increase: 0.1<!--This is the increment that is added to the base value of the effect depending on your level. This can be a negative value for example in tool durability you wouldn't want your tool to be destroyed more so the increment is a negative value.-->
  - Level Increase (Novice): 0.05<!--This is the increment that is added to the base value of the effect up until the player is a novice in the skill which is usually level 5 where a player is considered to be at vanilla effects. After novice level the above Level Increase is used. Can be be zero or significantly smaller to avoid increases in the effect until after novice.-->
  - Min: 0 <!--The minimum that the effects value can reach e.g. (BaseValue + LevelIncrease) * skill can't be less than 5. e.g. 0 seeds is the least amount that will be dropped.-->
  - Max: 5 <!--The maximum that the effects value can reach. e.g. 5 seeds is the most that will be dropped even at the highest level of the skill-->
  - Floor: true/false <!--If `true` floors the effect value. i.e. always rounds down to a whole integer.-->
  - Exception: true/false <!--If true then between the Low skill level and the High Skill level it will only give the ExceptionValue for the effects value. (i.e. between lvls 0-5 only 0 iron ingots are dropped).-->
  - ExceptionLow: 0 <!--The lower integer of the interval for the ExceptionValue to be applied to the effect.-->
  - ExceptionHigh: 5 <!--The higher integer of the interval for the ExceptionValue to be applied to the effect.-->
  - ExceptionValue: 0 <!--The value that will replace the effect value when the players skill level is in between the ExceptionLow and ExceptionHigh interval.-->
  - NormalLevel: 5 <!--The vanilla level of the effect e.g. 1 cobblestone is dropped when stone is broken. This is sometimes called the Novice level. At which point the player is considered a Novice.-->
  - RequireTool: true/false <!-- `true` if your skill/effect requires a tool to activate e.g. Mining - Pickaxe.-->
  - Tools: stone_hoe, iron_hoe <!--the list of tools that can activate the effect if true.-->

  <!-- Effect Types:
  BLOCKDROP - An itemstack is dropped when a block is broken
  MOBDROP - An itemstack is dropped when a mob is killed
  SWORDDURABILITY - Sword durability
  PVPDAMAGE - Player vs. Player Damage, all damage effect types either increase of decrease the damage taken.
  PVEDAMAGE - Player vs. Entity Damage
  EXPLOSIONDAMAGE - Explosion Damage
  FIREDAMAGE - Fire Damage
  FALLDAMAGE - Fall Damage
  FALLTHRESHOLD - Unknown
  PLOWDURARBILTY - Durability of hoes
  TOOLDURABILITY - General Tool durabilty
  EAT - When a player eats determines how much hunger is gained
  CRAFT - When crafting determines the amount is crafted per recipe
  PLOW - An itemstack is dropped when grass is plowed
  DIGTIME - Something to do with digging.
  BOWATTACK - When a player attacks with its bow, how much damage is done.
  VEHICLEDROP - If your boat or minecraft is dropped or destroyed.
  VEHICLEMOVE - How fast or if you can move your vehicle.
  FISH - When a player fishes with their rod an itemstack can be dropped as well as their fish.
  RODDURABILITY - The amount of durability that is lost when a player successfully catches fish.
  SMELT - The amount of items dropped out of the furnace after smelting.
  SHEAR - The amount of items dropped when shearing animals-->