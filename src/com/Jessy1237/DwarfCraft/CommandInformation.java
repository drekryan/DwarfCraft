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
        TUTORIAL( "Displays a series of guide messages to instruct in the basics of DwarfCraft." ),
        SKILLSHEET( "Displays a list of skills and levels for a Dwarf." ),
        SKILLINFO( "Displays a description of a dwarf's skill and training costs." ),
        EFFECTINFO( "Displays a description of a dwarf's effect information." ),
        RACE( "Checks a players race information, or changes it." ),
        SETSKILL( "Admin command to change a players skill level manually." ),
        CREATE( "Creates a new trainer where you are standing." ),
        LIST( "Displays a list of trainers on the server." ),
        RACES( "Displays a list of the races with descriptions" ),
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
        DEBUG( "/dwarfcraft debug <debug level>\nExample: /dcdebug 2 - sets the console debug printing threshold to 2" ),
        INFO( "/dwarfcraft info Displays general information about the DwarfCraft plugin." ),
        TUTORIAL( " Displays a series of guide messages to instruct in the basics of DwarfCraft\n/dwarfcraft tutorial <page number>\nExample: /dwarfcraft tutorial 2 - Prints the second tutorial section" ),
        SKILLSHEET( " Displays a list of skills and levels for a Dwarf.\n/DwarfCraft skillsheet <full/-f)> <Player Name or blank>\nExample: /dwarfcraft skillsheet smartaleq - Prints smartaleq's skillsheet\nExample: /dwarfcraft ss -f - Prints your complete skillsheet with level 0 Skills" ),
        SKILLINFO(
                "Displays a description of a dwarf's skill and training costs\n/dwarfcraft skillinfo <player name> [Skill ID or Skill Name]\nExample: /dwarfcraft skillinfo 11 - Prints details about Excavation skill\nExample: /dwarfcraft skillinfo Dirt_Digging - Prints details about Dirt Digging skill\nExample: /dwarfcraft skillinfo smartaleq 3 - Prints details about Smartaleq's Axe use skill" ),
        EFFECTINFO( "Displays a description of a dwarf's effect information\n/dwarfcraft effectinfo <player name> [EffectID]\nExample: /dwarfcraft effectinfo smartaleq 131 - Prints details about Smartaleq's effect 131" ),
        RACE(
                "/dwarfcraft race <Race name> <confirm>\nExample: /dwarfcraft race - Displays the player's current race information.\nExample: /dwarfcraft race elf - Displays information about the elf race.\nExample: /dwarfcraft race elf confirm - Changes the player's race to elf and resets their skills.\nAdmin: /dwarfcraft race <Player> <racename> <confirm> - Alters another player's race, use confirm. \n Admin: /dwarfcraft race <player> - shows a players race." ),
        SETSKILL( "/dwarfcraft setskill <player name> [Skill ID or Skill Name or All] [new skill level]" ),
        CREATE( "/dwarfcraft create <id> <DisplayName> <Skill ID or Skill Name>\n <Max Skill Level> <Min Skill Level> <EntityType>" ),
        LIST( "/dwarfcraft list [Page]" ),
        RACES( "/dwarfcraft races" ),
        RELOAD( "/dwarfcraft reload" );

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
