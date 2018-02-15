package com.Jessy1237.DwarfCraft;

import java.util.ArrayList;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

public final class Messages
{

    public Messages()
    {
        Messages.tutorial.add( "&5&lWelcome to DwarfCraft!\n\n&0You have a set of skills that let you do certain tasks better. When you first start things may be more difficult than you're used to, but as you level your skills up you will be much more productive." );
        Messages.tutorial.add( "&5The Races of DwarfCraft\n\n&0There are four basic races in DwarfCraft. It is up to you to choose a race that best fits your play style. You will not be able to level any skills in DwarfCraft until you pick a race.\n" );
        Messages.tutorial.add("{\"text\":\"Read about the four races on the pages that follow, then type '/dc race' or click here to begin your journey.\",\"color\":\"dark_red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/dc race\"}}" );
        Messages.tutorial.add( "&3The Dwarf\n\n&0The Dwarf race specializes in mining. The skills in this race will allow you to mine more efficiently, gather additional ores, and are great excavators. The Dwarf plays an important role in providing resources for tools and equipment needed by the other races. Their race relies on you providing resources. If you enjoy mining, this race would be great for you." );
        Messages.tutorial.add( "&9The Gnome\n\n&0The Gnome race specializes in crafting and building. They will provide structures for you, craft your tools and equipment, and are all around masters of the crafting table. If you enjoy casually and creatively building or are a  master in redstone contraptions, this race would be great for you." );
        Messages.tutorial.add( "&4The Elf\n\n&0The Elf race specializes in all things combat. They deal extra damage to animals and mobs and get extra drops. They are great at gaining experience levels and protecting your fellow members. They are also great archers and have great exploration skills whether it be on foot or boat. If you enoy combat and exploring, the elven race is all about you." );
        Messages.tutorial.add( "&8The Human\n\n&0The Human race specializes in various support and utility skills such as smelting, cooking, fishing, and farming. They are great lumberjacks and are great for gathering wood and processing resources from the Dwarf for the Gnome and Elf. If you enjoy playing a casual support role, the Human race best fits you." );
        Messages.tutorial.add( "&5Skilling in DwarfCraft\n\n&0Now that you have learned about the various races to choose from and hopefully found one that fits you, it is time to learn how you can advance your character in that race. If you want to become the best of your class you will need to train by visiting trainers in the world. We will learn more about trainers in a bit. First lets talk about your skills and skillsheet." );
        Messages.tutorial.add( "&0At first your skills will be very poor which means you will perform tasks worse than normal gameplay. You will find your low level skills dropping less resources, crafting less items, taking longer, or requiring more tools. As you level you will begin to exceed the normal rates and excel in some tasks. Normal gameplay rates take effect around level %raceLevelLimit%. You also wont be able to excel in every skill to the highest level. The skills in which your race specializes in can be levelled to level %maxSkillLevel% while all other skills outside your race specialty will cap out at the normal rates." );
        Messages.tutorial.add( "[{\"text\":\"You can access your current skill levels at anytime with '/dc skillsheet'.\\n\",\"color\":\"dark_red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/dc skillsheet\"}},{\"text\":\"\\nYou can find all the skills which your character has yet to train by typing '/dc skillsheet full'.\",\"color\":\"dark_red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/dc skillsheet full\"}}]" );
        Messages.tutorial.add( "&0Now you know that basics of how skilling works in DwarfCraft. Lets discuss the final topic in DwarfCraft, which is how you train those skills." );
        Messages.tutorial.add( "&5DwarfCraft Trainers\n\n&0Trainers are experts in the skill they teach. They will share their knowledge with you in return for specific resources related to that skill." );
    }

    // String messages fixed for DwarfCraft, and backup messages when loading
    // fails.
    protected enum Fixed
    {
        WELCOME( "Welcome to a DwarfCraft world! Things are bit different here as this world focuses heavily on skilling. To learn more about what you can do here, type '&4/dc tutorial&2' to get started." ),
        GENERALINFO( "&dWelcome to DwarfCraft. You are a player with a set of skills that let you do certain tasks better. For more information see &4/dc tutorial&d. Original Authors: smartaleq, LexManos and RCarretta Authors: Jessy1237" );

        private String message;

        Fixed( String message )
        {
            this.message = message;
        }

        public String getMessage()
        {
            return message;
        }
    }

    // String messages loaded from messages.config
    public static String welcomePrefix = "&6[DwarfCraft] ";
    public static String welcome = "&fWelcome, &9%playerrace% &6%playername%";
    public static String skillSheetPrefix = "&6[&dSS&6] ";
    public static String skillSheetHeader = "&6Skill Sheet for &9%playername% &6[&9%playerrace% &6- &6Lvl &3%playerlevel%&6]";
    public static String skillSheetSkillLine = "&6[&3%skilllevel%&6] &b%skillname%";
    public static String skillSheetUntrainedSkillHeader = "&6Untrained Skills%colon%";
    public static String skillSheetUntrainedSkillLine = "|&7%skillname%&6| ";
    public static String skillInfoHeader = "&6Skillinfo for &b%playername%&6 || &b%skillname%&6 [&b%skillid%&6] || Your level &3%skilllevel%/%maxskilllevel%";
    public static String skillInfoMinorHeader = "&6[&5EffectID&6]&f------&6[Effect]&f------";
    public static String skillInfoEffectIDPrefix = "&6[&5%effectid%&6] ";
    public static String skillInfoMaxSkillLevel = "&6---This skill is maximum level, no training available---";
    public static String skillInfoAtTrainerLevel = "&6---You're as skilled as me, you need a more advanced trainer!--";
    public static String skillInfoTrainCostHeader = "&6---Train costs for level &3%nextskilllevel%";
    public static String skillInfoTrainCost = " &6--  &2%depositedamount% of %totalcost% %itemtype%&6  --";
    public static String effectInfoPrefix = "&6[&5%effectid%&6] ";
    public static String raceCheck = "You are a %playerrace%";
    public static String adminRaceCheck = "%playername% is a %playerrace%";
    public static String alreadyRace = "You are already a %playerrace%";
    public static String changedRace = "You are now a %racename%";
    public static String confirmRace = "You need to confirm this command with confirm at the end. (Note: This will reset all their skills)";
    public static String raceDoesNotExist = "The race %racename% doesn't exist";
    public static String chooseARace = "&cPlease choose a race!";
    public static String trainSkillPrefix = "&6[Train &b%skillid%&6] ";
    public static String raceDoesNotContainSkill = "&cYour race doesn't have this skill!";
    public static String raceDoesNotSpecialize = "&cYour race doesn't specialize in this skill! Max level is (%racelevellimit%)!";
    public static String maxSkillLevel = "&cYour skill is max level (%maxskilllevel%)!";
    public static String trainerMaxLevel = "&cI can't teach you any more, find a higher level trainer";
    public static String trainerLevelTooHigh = "&cI can't teach a low level like you, find a lower level trainer";
    public static String noMoreItemNeeded = "&aNo more &2%itemname% &ais needed";
    public static String moreItemNeeded = "&cAn additional &2%costamount% %itemname% &cis required";
    public static String trainingSuccessful = "&6Training Successful!";
    public static String depositSuccessful = "&6Deposit Successful!";
    public static String trainerGUITitle = "&8%skillname%&6 [&b%skillid%&6] || &3%skilllevel%/%maxskilllevel%";
    public static String trainerOccupied = "&6Please wait. I am talking to someone else.";
    public static String trainerCooldown = "&6Sorry, I need time to recuperate.";
    public static String describeGeneral = "Effect Block Trigger: %initiator% Block Output: %output%. Effect value ranges from %effectamountlow% - %effectamounthigh% for levels 0 to 30. Non specialists have the effect %minoramount% , as if they were level %normallevel%. Tools affected: %tooltype%.";
    public static String describeLevelBlockdrop = "&6Break a &2%initiator% &6and %effectlevelcolor%%effectamount% &2%output%&6 are created";
    public static String describeLevelMobdrop = "&6%creaturename% drop about %effectlevelcolor%%effectamount% &2%output%";
    public static String describeLevelMobdropNoCreature = "&6Enemies that drop &2%output% &6leave about %effectlevelcolor%%effectamount%&6";
    public static String describeLevelSwordDurability = "&6Using &2%tooltype% &6removes about %effectlevelcolor%%effectamount% &6durability";
    public static String describeLevelPVPDamage = "&6You do %effectlevelcolor%%effectdamage%&6% &6of normal &2%tooltype% &6damage when fighting players";
    public static String describeLevelPVEDamage = "&6You do %effectlevelcolor%%effectdamage%&6% &6of normal &2%tooltype% &6damage when fighting mobs";
    public static String describeLevelExplosionDamageMore = "&6You take %effectlevelcolor%%effecttakedamage%% more &6damage from explosions";
    public static String describeLevelExplosionDamageLess = "&6You take %effectlevelcolor%%effecttakedamage%% less &6damage from explosions";
    public static String describeLevelFireDamageMore = "&6You take %effectlevelcolor%%effecttakedamage%% more &6damage from fire";
    public static String describeLevelFireDamageLess = "&6You take %effectlevelcolor%%effecttakedamage%% less &6damage from fire";
    public static String describeLevelFallingDamageMore = "&6You take %effectlevelcolor%%effecttakedamage%%% more &6damage from falling";
    public static String describeLevelFallingDamageLess = "&6You take %effectlevelcolor%%effecttakedamage%% less &6damage from falling";
    public static String describeLevelFallThreshold = "&6Fall damage less than %effectlevelcolor%%effectamountint% &6does not affect you.";
    public static String describeLevelPlowDurability = "&6Using &2%tooltype% &6removes about %effectlevelcolor%%effectamount% &6durability";
    public static String describeLevelToolDurability = "&6Using &2%tooltype% &6removes about %effectlevelcolor%%effectamount% &6durability";
    public static String describeLevelRodDurability = "&6Using &2%tooltype% &6removes about %effectlevelcolor%%effectamount% &6durability";
    public static String describeLevelEat = "&6You gain %effectlevelcolor%%effectamountfood% &6Hunger instead of &e%originalfoodlevel%&6 when you eat &2%initiator%";
    public static String describeLevelCraft = "&6You craft %effectlevelcolor%%.0%effectamount% &2%output% &6instead of &e%minoramount%";
    public static String describeLevelPlow = "&6You gain %effectlevelcolor%%effectamount% &6seeds instead of &e%minoramount% &6when you plow grass";
    public static String describeLevelFish = "&6You catch %effectlevelcolor%%effectamount% &6fish instead of &e%minoramount% &6when you fish";
    public static String describeLevelBrew = "&6You brew %effectlevelcolor%%effectamount% &6potion(s) instead of &e%minoramount% &6when you're brewing potions";
    public static String describeLevelDigTime = "&a%effectamountdig%%&6 of the time &2%tooltype% &6break &2%initiator% &6instantly";
    public static String describeLevelBowAttack = "&6Your Arrows (Fully Charge Bow) do %effectlevelcolor%%effectbowdamage% &6hp damage (half hearts)";
    public static String describeLevelVehicleDrop = "&6When you break a boat &6approx. %effectlevelcolor%%effectamount% &2%output%&6 are created";
    public static String describeLevelVehicleMove = "&6Your boat travels %effectlevelcolor%%effecttakedamage%% &6faster than normal";
    public static String describeLevelSmelt = "&6Smelt a &2%initiator% &6and %effectlevelcolor%%effectamount% &2%output%&6 are created as well";
    public static String describeLevelShear = "&6Shear a %creaturename% and %effectlevelcolor%%effectamount% &6%output% are dropped instead of &e%minoramount%";
    public static String effectLevelColorGreaterThanNormal = "&a";
    public static String effectLevelColorEqualToNormal = "&e";
    public static String effectLevelColorLessThanNormal = "&c";
    public static String vanillaRace = "Sorry but you are the vanilla race. Change your race to use DwarfCraft";
    public static ArrayList<String> tutorial = new ArrayList<String>();
}