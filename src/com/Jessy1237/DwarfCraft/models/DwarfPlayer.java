package com.Jessy1237.DwarfCraft.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Jessy1237.DwarfCraft.DwarfCraft;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */
public class DwarfPlayer
{
    private final DwarfCraft plugin;
    private HashMap<Integer, DwarfSkill> skills;
    private Player player;
    private String race;
    private boolean raceMaster;

    public void setPlayer( Player player )
    {
        this.player = player;
    }

    public DwarfPlayer( final DwarfCraft plugin, Player player )
    {
        this.plugin = plugin;
        this.player = player;
        this.race = plugin.getConfigManager().getDefaultRace().trim();
        this.skills = plugin.getConfigManager().getAllSkills();
        this.raceMaster = false;
    }

    public DwarfPlayer( final DwarfCraft plugin, Player player, String race, boolean raceMaster )
    {
        this.plugin = plugin;
        this.player = player;
        this.race = race.trim();
        this.skills = plugin.getConfigManager().getAllSkills();
        this.raceMaster = raceMaster;
    }

    public List<List<ItemStack>> calculateTrainingCost( DwarfSkill skill )
    {
        int highSkills = countHighSkills();
        int dwarfLevel = getDwarfLevel();
        int quartileSize = Math.min( 4, highSkills / 4 );
        int quartileNumber = 1; // 1 = top, 2 = 2nd, etc.
        int[] levelList = new int[highSkills + 1];
        List<ItemStack> costToLevelStack = new ArrayList<ItemStack>();
        List<ItemStack> totalCostStack = new ArrayList<ItemStack>();
        int i = 0;

        // Creates an ordered list of skill levels and finds where in that
        // list
        // the skill is (what quartile)
        if ( DwarfCraft.debugMessagesThreshold < 0 )
            System.out.println( "DC0: starting skill ordering for quartiles" );
        for ( DwarfSkill s : getSkills().values() )
        {
            if ( s.getLevel() > plugin.getConfigManager().getRaceLevelLimit() )
            {
                levelList[i] = s.getLevel();
                i++;
            }
        }
        Arrays.sort( levelList );
        if ( levelList[highSkills - quartileSize] <= skill.getLevel() )
            quartileNumber = 1;
        else if ( levelList[highSkills - 2 * quartileSize] <= skill.getLevel() )
            quartileNumber = 2;
        else if ( levelList[highSkills - 3 * quartileSize] <= skill.getLevel() )
            quartileNumber = 3;
        if ( skill.getLevel() < plugin.getConfigManager().getRaceLevelLimit() )
            quartileNumber = 1; // low skills train full speed

        // calculate quartile penalties for 2nd/3rd/4th quartile
        double multiplier = Math.max( 1, Math.pow( 1.072, ( skill.getLevel() - plugin.getConfigManager().getRaceLevelLimit() ) ) );
        if ( quartileNumber == 2 )
            multiplier *= ( 1 + 1 * dwarfLevel / ( 100 + 3 * dwarfLevel ) );
        if ( quartileNumber == 3 )
            multiplier *= ( 1 + 2 * dwarfLevel / ( 100 + 3 * dwarfLevel ) );
        if ( quartileNumber == 4 )
            multiplier *= ( 1 + 3 * dwarfLevel / ( 100 + 3 * dwarfLevel ) );

        // create output item stack of new items
        int item1Amount = ( ( int ) Math.min( Math.ceil( ( skill.getLevel() + 1 ) * skill.Item1.Base * multiplier - .01 ), skill.Item1.Max ) ), item2Amount = ( ( int ) Math.min( Math.ceil( ( skill.getLevel() + 1 ) * skill.Item2.Base * multiplier - .01 ), skill.Item2.Max ) ),
                item3Amount = ( ( int ) Math.min( Math.ceil( ( skill.getLevel() + 1 ) * skill.Item3.Base * multiplier - .01 ), skill.Item3.Max ) );

        if ( plugin.isAuraActive )
        {
            double item1Decrease = Math.ceil( item1Amount * 0.10 );
            double item2Decrease = Math.ceil( item2Amount * 0.10 );
            double item3Decrease = Math.ceil( item3Amount * 0.10 );

            item1Amount = ( int ) ( item1Amount - item1Decrease );
            item2Amount = ( int ) ( item2Amount - item2Decrease );
            item3Amount = ( int ) ( item3Amount - item3Decrease );
        }

        totalCostStack.add( 0, new ItemStack( skill.Item1.Item.getType(), item1Amount, skill.Item1.Item.getDurability() ) );
        costToLevelStack.add( 0, new ItemStack( skill.Item1.Item.getType(), item1Amount - skill.getDeposit1(), skill.Item1.Item.getDurability() ) );

        if ( skill.Item2.Item.getType() != Material.AIR )
        {
            totalCostStack.add( 1, new ItemStack( skill.Item2.Item.getType(), item2Amount, skill.Item2.Item.getDurability() ) );
            costToLevelStack.add( 1, new ItemStack( skill.Item2.Item.getType(), item2Amount - skill.getDeposit2(), skill.Item2.Item.getDurability() ) );
        }
        if ( skill.Item3.Item.getType() != Material.AIR )
        {
            totalCostStack.add( 2, new ItemStack( skill.Item3.Item.getType(), item3Amount, skill.Item3.Item.getDurability() ) );
            costToLevelStack.add( 2, new ItemStack( skill.Item3.Item.getType(), item3Amount - skill.getDeposit3(), skill.Item3.Item.getDurability() ) );
        }
        List<List<ItemStack>> costs = new ArrayList<List<ItemStack>>();

        costs.add( 0, costToLevelStack );
        costs.add( 1, totalCostStack );
        return costs;
    }

    /**
     * Counts skills greater than level the race level limit, used for training costs
     */
    private int countHighSkills()
    {
        int highCount = 0;
        for ( DwarfSkill s : getSkills().values() )
        {
            if ( s.getLevel() > plugin.getConfigManager().getRaceLevelLimit() )
                highCount++;
        }
        return highCount;
    }

    /**
     * Calculates the dwarf's total level for display. Value is the total of all skill level above the race level limit, or the highest skill level when none are above the race level limit.
     * 
     * @return
     */
    public int getDwarfLevel()
    {
        int playerLevel = plugin.getConfigManager().getRaceLevelLimit();
        int highestSkill = 0;
        for ( DwarfSkill s : getSkills().values() )
        {
            if ( s.getLevel() > highestSkill )
                highestSkill = s.getLevel();

            if ( s.getLevel() > plugin.getConfigManager().getRaceLevelLimit() )
                playerLevel = playerLevel + s.getLevel() - plugin.getConfigManager().getRaceLevelLimit();
        }
        if ( playerLevel == plugin.getConfigManager().getRaceLevelLimit() )
            playerLevel = highestSkill;
        return playerLevel;
    }

    /**
     * Retrieves an effect from a player based on its effectId.
     * 
     * @param effectId
     * @return
     */
    public DwarfEffect getEffect( int effectId )
    {
        DwarfSkill skill = getSkill( effectId / 10 );
        for ( DwarfEffect effect : skill.getEffects() )
        {
            if ( effect.getId() == effectId )
                return effect;
        }
        return null;
    }

    public Player getPlayer()
    {
        return player;
    }

    /**
     * Gets a dwarf's skill from an effect
     * 
     * @param effect (does not have to be this dwarf's effect, only used for ID#)
     * @return DwarfSkill or null if none found
     */
    public DwarfSkill getSkill( DwarfEffect effect )
    {
        for ( DwarfSkill skill : skills.values() )
        {
            if ( skill.getId() == effect.getId() / 10 )
                return skill;
        }
        return null;
    }

    /**
     * Gets a dwarf's skill by id
     * 
     * @param skillId
     * @return DwarfSkill or null if none found
     */
    public DwarfSkill getSkill( int skillId )
    {
        DwarfSkill skill = skills.get( skillId );
        return skill;
    }

    /**
     * Gets a dwarf's skill by name or id number(as String)
     * 
     * @param skillName
     * @return DwarfSkill or null if none found
     */
    public DwarfSkill getSkill( String skillName )
    {
        try
        {
            return getSkill( Integer.parseInt( skillName ) );
        }
        catch ( NumberFormatException n )
        {
            for ( DwarfSkill skill : getSkills().values() )
            {
                if ( skill.getDisplayName() == null )
                    continue;
                if ( skill.getDisplayName().equalsIgnoreCase( skillName ) )
                    return skill;
                if ( skill.toString().equalsIgnoreCase( skillName ) )
                    return skill;
                if ( skill.getDisplayName().toLowerCase().regionMatches( 0, skillName.toLowerCase(), 0, 8 ) )
                    return skill;
                if ( skill.toString().toLowerCase().regionMatches( 0, skillName.toLowerCase(), 0, 8 ) )
                    return skill;
            }

        }
        return null;
    }

    public HashMap<Integer, DwarfSkill> getSkills()
    {
        return skills;
    }

    /**
     * Calculates the Dwarf's total Level
     * 
     * @return total level
     */
    public int level()
    {
        int playerLevel = plugin.getConfigManager().getRaceLevelLimit();
        int highestSkill = 0;
        for ( DwarfSkill s : getSkills().values() )
        {
            if ( s.getLevel() > highestSkill )
                highestSkill = s.getLevel();
            if ( s.getLevel() > plugin.getConfigManager().getRaceLevelLimit() )
                playerLevel += s.getLevel() - plugin.getConfigManager().getRaceLevelLimit();
            ;
        }
        if ( playerLevel == plugin.getConfigManager().getRaceLevelLimit() )
            playerLevel = highestSkill;
        return playerLevel;
    }

    /**
     * @param skills the skills to set
     */
    public void setSkills( HashMap<Integer, DwarfSkill> skills )
    {
        this.skills = skills;
    }

    public int getSkillLevel( int id )
    {
        for ( DwarfSkill s : getSkills().values() )
            if ( s.getId() == id )
                return s.getLevel();
        return 0;
    }

    public void changeRace( String race )
    {
        final String oldRace = this.race;
        this.race = race;
        DwarfSkill[] dCSkills = new DwarfSkill[skills.size()];

        int I = 0;
        // Resets the players skills
        for ( DwarfSkill skill : skills.values() )
        {
            if ( plugin.getConfigManager().hardcorePenalty )
            {
                if ( race.equalsIgnoreCase( "Vanilla" ) )
                {
                    skill.setLevel( 5 );
                }
                else
                {
                    skill.setLevel( 0 );
                }

                skill.setDeposit1( 0 );
                skill.setDeposit2( 0 );
                skill.setDeposit3( 0 );
            }
            else
            {
                if ( race.equalsIgnoreCase( "Vanilla" ) )
                {
                    skill.setLevel( 5 );
                }
                else
                {
                    if ( !plugin.getConfigManager().getRace( race ).getSkills().contains( Integer.valueOf( skill.getId() ) ) && skill.getLevel() > plugin.getConfigManager().getRaceLevelLimit() )
                    {
                        skill.setLevel( plugin.getConfigManager().getRaceLevelLimit() );
                        skill.setDeposit1( 0 );
                        skill.setDeposit2( 0 );
                        skill.setDeposit3( 0 );
                    }
                }
            }
            dCSkills[I] = skill;
            I++;
        }

        // Resets the players prefix
        if ( plugin.isChatEnabled() )
            if ( !oldRace.equalsIgnoreCase( "NULL" ) )
                if ( plugin.getChat().getPlayerPrefix( getPlayer() ).contains( plugin.getUtil().getPlayerPrefix( oldRace ) ) )
                    plugin.getChat().setPlayerPrefix( getPlayer(), plugin.getChat().getPlayerPrefix( getPlayer() ).replace( plugin.getUtil().getPlayerPrefix( oldRace ), plugin.getUtil().getPlayerPrefix( this ) ) );

        plugin.getDataManager().saveDwarfData( this, dCSkills );
    }

    public String getRace()
    {
        return race.trim();
    }

    public void setRace( String race )
    {
        this.race = race.trim();
    }

    public boolean isRaceMaster()
    {
        return raceMaster;
    }

    public void setRaceMaster( boolean raceMaster )
    {
        this.raceMaster = raceMaster;
    }

    public boolean isMax()
    {
        boolean isMax = true;
        for ( DwarfSkill s : skills.values() )
        {
            if ( s.getLevel() < plugin.getUtil().getMaxLevelForSkill( this, s ) )
            {
                isMax = false;
            }
        }

        return isMax;
    }

    public boolean isDwarfCraftDev()
    {
        return player.getPlayer().getName().trim().equalsIgnoreCase( "Drekryan" ) || player.getPlayer().getName().trim().equalsIgnoreCase( "Jessy1237" );
    }

    public void runLevelUpCommands( DwarfSkill skill )
    {
        if ( plugin.getConfigManager().getSkillLevelCommands().size() > 0 )
        {
            ArrayList<String> commands;

            if ( !plugin.getConfigManager().getAllSkills().values().contains( skill ) )
            {
                return;
            }

            if ( isMax() )
            {
                commands = plugin.getConfigManager().getSkillMaxCapeCommands();
            }
            else if ( skill.getLevel() >= plugin.getUtil().getMaxLevelForSkill( this, skill ) )
            {
                commands = plugin.getConfigManager().getSkillMasteryCommands();
            }
            else
            {
                commands = plugin.getConfigManager().getSkillLevelCommands();
            }

            for ( String command : commands )
            {
                String playerPosition = player.getLocation().getX() + " " + player.getLocation().getY() + " " + player.getLocation().getZ();

                command = command.replaceAll( "%playerpos%", playerPosition ).replaceAll( "%world%", player.getWorld().getName() );
                command = plugin.getPlaceHolderParser().parseByDwarfPlayerAndDwarfSkill( command, this, skill );
                command = ChatColor.translateAlternateColorCodes( '&', command );

                plugin.getServer().dispatchCommand( plugin.getServer().getConsoleSender(), command );
            }
        }
    }
}
