package com.Jessy1237.DwarfCraft;

public class CommandInformation
{

    /**
     * Authors: Jessy1237
     */

    public enum Desc
    {
        DEBUG( "Sets the debug message threshold in console, from -10(everthing) to +10(critical only)." ),
        INFO( "Displays general information about the DwarfCraft plugin." ),
        RULES( "Displays server rules defined in the DwarfCraft messages config file." ),
        TUTORIAL( "Displays a series of guide messages to instruct in the basics of DwarfCraft." ),
        SKILLSHEET( "Displays a list of skills and levels for a Dwarf." ),
        SKILLINFO( "Displays a description of a dwarf's skill and training costs." ),
        EFFECTINFO( "Displays a description of a dwarf's effect information." ),
        RACE( "Checks a players race information, or changes it." ),
        SETSKILL( "Admin command to change a players skill level manually." ),
        CREATEGREETER( "Creates a new greeter where you are standing." ),
        CREATETRAINER( "Creates a new trainer where you are standing." ),
        LISTTRAINERS( "Displays a list of trainers and greeters on the server." ),
        RACES( "Displays a list of the races with descriptions" ),
        DMEM( "Displays a list of what type, and how many entites are loaded on the server." ),
        RELOAD( "Reloads the DwarfCraft plugin." );

        private String Desc;

        private Desc( String Desc )
        {
            this.Desc = Desc;
        }

        public String getDesc()
        {
            return Desc;
        }
    }

    public enum Usage
    {
        DEBUG( "/DwarfCraft debug <debug level>\nExample: /dcdebug 2 - sets the console debug printing threshold to 2" ),
        INFO( "/DwarfCraft info Displays general information about the DwarfCraft plugin." ),
        RULES( "/DwarfCraft rules Displays server rules defined in the DwarfCraft messages config file." ),
        TUTORIAL( " Displays a series of guide messages to instruct in the basics of DwarfCraft\n/DwarfCraft tutorial <page number>\nExample: /DwarfCraft tutorial 2 - Prints the second tutorial section" ),
        SKILLSHEET( " Displays a list of skills and levels for a Dwarf.\n/DwarfCraft skillsheet <full/-f)> <Player Name or blank>\nExample: /DwarfCraft skillsheet smartaleq - Prints smartaleq's skillsheet\nExample: /DwarfCraft ss -f - Prints your complete skillsheet with level 0 Skills" ),
        SKILLINFO(
                "Displays a description of a dwarf's skill and training costs\n/DwarfCraft skillinfo <player name> [Skill ID or Skill Name]\nExample: /DwarfCraft skillinfo 11 - Prints details about Excavation skill\nExample: /DwarfCraft skillinfo Dirt_Digging - Prints details about Dirt Digging skill\nExample: /DwarfCraft skillinfo smartaleq 3 - Prints details about Smartaleq's Axe use skill" ),
        EFFECTINFO( "Displays a description of a dwarf's effect information\n/DwarfCraft effectinfo <player name> [EffectID]\nExample: /DwarfCraft effectinfo smartaleq 131 - Prints details about Smartaleq's effect 131" ),
        RACE(
                "/DwarfCraft race <Race name> <confirm>\nExample: /DwarfCraft race - Displays the player's current race information.\nExample: /DwarfCraft race elf - Displays information about the elf race.\nExample: /DwarfCraft race elf confirm - Changes the player's race to elf and resets their skills.\nAdmin: /DwarfCraft race <Player> <Racename> <confirm> - Alters another player's race, use confirm. \n Admin: /DwarfCraft race <player> - shows a players race." ),
        SETSKILL( "/DwarfCraft setskill <player name> [Skill ID or Skill Name or All] [new skill level]" ),
        CREATEGREETER( "/DwarfCraft creategreeter <id> <DisplayName> <MessageId>" ),
        CREATETRAINER( "/DwarfCraft createtrainer <id> <DisplayName> <Skill ID or Skill Name>\n <Max Skill Level> <Min Skill Level> <EntityType>" ),
        LISTTRAINERS( "/DwarfCraft listtrainers [PageNumber]" ),
        RACES( "/DwarfCraft races" ),
        DMEM( "/DwarfCraft dmem" ),
        RELOAD( "/DwarfCraft reload" );

        private String Usage;

        private Usage( String Usage )
        {
            this.Usage = Usage;
        }

        public String getUsage()
        {
            return Usage;
        }
    }
}
