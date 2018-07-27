/*
 * Copyright (c) 2018.
 *
 * DwarfCraft is an RPG plugin that allows players to improve their characters
 * skills and capabilities through training, not experience.
 *
 * Authors: Jessy1237 and Drekryan
 * Original Authors: smartaleq, LexManos and RCarretta
 */

package com.Jessy1237.DwarfCraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jbls.LexManos.CSV.CSVReader;
import org.jbls.LexManos.CSV.CSVRecord;

import com.Jessy1237.DwarfCraft.events.DwarfLoadRacesEvent;
import com.Jessy1237.DwarfCraft.events.DwarfLoadSkillsEvent;
import com.Jessy1237.DwarfCraft.models.DwarfEffect;
import com.Jessy1237.DwarfCraft.models.DwarfItemHolder;
import com.Jessy1237.DwarfCraft.models.DwarfRace;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;
import com.Jessy1237.DwarfCraft.models.DwarfTrainingItem;

public final class ConfigManager
{

    private final DwarfCraft plugin;

    private final String configDirectory;

    public String dbType;
    public String host;
    public int port;
    public String database;
    public String username;
    public String password;

    private String dbpath;
    private Integer trainDelay;
    private Integer announcementInterval;
    private ArrayList<String> skillLevelCommands;
    private ArrayList<String> skillMasteryCommands;
    private ArrayList<String> skillMaxCapeCommands;
    private Integer maxLevel;
    private Integer raceLevelLimit;
    private String prefixStr;

    private HashMap<Integer, DwarfSkill> skillsArray = new HashMap<>();
    public ArrayList<World> worlds = new ArrayList<>();

    private ArrayList<DwarfRace> raceList = new ArrayList<>();
    private String defaultRace;

    public boolean sendGreeting = false;
    public boolean disableCacti = true;
    public boolean worldBlacklist = false;
    public boolean silkTouch = true;
    public boolean vanilla = true;
    public boolean prefix = false;
    public boolean announce = false;
    public boolean byID = true;
    public boolean hardcorePenalty = true;
    public boolean spawnTutorialBook = true;

    @SuppressWarnings( "unchecked" )
    protected ConfigManager( DwarfCraft plugin, String directory )
    {
        this.plugin = plugin;
        if ( !directory.endsWith( File.separator ) )
            directory += File.separator;
        configDirectory = directory;
        checkFiles( configDirectory );

        try
        {
            if ( !readSkillsFile() || !readEffectsFile() || !readLocaleFile() || !readRacesFile() )
            {
                plugin.getUtil().consoleLog( Level.SEVERE, "Failed to Enable DwarfCraft configs" );
                plugin.getServer().getPluginManager().disablePlugin( plugin );
            }
            else
            {
                // Runs the proceeding events after all the config files are
                // read so that the skillArray is complete with effects
                DwarfLoadSkillsEvent e = new DwarfLoadSkillsEvent( ( HashMap<Integer, DwarfSkill> ) skillsArray.clone() );
                plugin.getServer().getPluginManager().callEvent( e );
                skillsArray = e.getSkills();

                DwarfLoadRacesEvent event = new DwarfLoadRacesEvent( ( ArrayList<DwarfRace> ) raceList.clone() );
                plugin.getServer().getPluginManager().callEvent( event );
                raceList = event.getRaces();

            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            plugin.getUtil().consoleLog( Level.SEVERE, "Failed to Enable DwarfCraft configs" );
            plugin.getServer().getPluginManager().disablePlugin( plugin );
        }

    }

    public HashMap<Integer, DwarfSkill> getAllSkills()
    {
        HashMap<Integer, DwarfSkill> newSkillsArray = new HashMap<>();
        for ( DwarfSkill s : skillsArray.values() )
        {
            if ( newSkillsArray.containsKey( s.getId() ) )
                continue;
            newSkillsArray.put( s.getId(), s.clone() );
        }
        return newSkillsArray;
    }

    public DwarfRace getRace( String Race )
    {
        for ( DwarfRace r : raceList )
        {
            if ( r != null )
            {
                if ( r.getName().equalsIgnoreCase( Race ) )
                {
                    return r;
                }
            }
        }
        return null;
    }

    public ArrayList<Integer> getAllSkills( String Race )
    {
        DwarfRace r = getRace( Race );
        if ( r != null )
            return r.getSkills();
        return null;
    }

    public DwarfSkill getGenericSkill( int skillId )
    {

        for ( DwarfSkill s : skillsArray.values() )
        {
            if ( s.getId() == skillId )
                return s.clone();

        }
        return null;
    }

    public String getDbPath()
    {
        return configDirectory + dbpath;
    }

    private void checkFiles( String path )
    {
        File root = new File( path );
        if ( !root.exists() )
            root.mkdirs();
        try
        {
            // Create Main Config File
            plugin.saveDefaultConfig();
            if ( !readConfigFile() )
            {
                plugin.getUtil().consoleLog( Level.SEVERE, "Failed to Enable DwarfCraft configs" );
                plugin.getServer().getPluginManager().disablePlugin( plugin );
            }

            // Create Data Files
            String[] mfiles = { "skills.csv", "effects.csv", "dwarfcraft.db", "races.yml" };
            for ( String mfile : mfiles )
            {
                File file = new File( root, mfile );
                if ( !file.exists() )
                {
                    file.createNewFile();
                    CopyFile( "/default_files/" + mfile, file );
                }
            }

            // Create locale directory and locale files
            root = new File( path + "/locale" );
            if ( !root.exists() )
                root.mkdirs();

            String[] localeFiles = { "en_US.yml" };
            for ( String localeFile : localeFiles )
            {
                File file = new File( root, localeFile );
                if ( !file.exists() )
                {
                    file.createNewFile();
                    CopyFile( "/default_files/locale/" + localeFile, file );
                }
            }
        }
        catch ( Exception e )
        {
            plugin.getUtil().consoleLog( Level.SEVERE, "Could not verify files: " + e.toString() );
            e.printStackTrace();
        }
    }

    private void CopyFile( String name, File toFile ) throws Exception
    {
        InputStream ins = ConfigManager.class.getResourceAsStream( name );
        OutputStream out = new FileOutputStream( toFile );

        byte[] buf = new byte[1024];
        int len;
        while ( ( len = ins.read( buf ) ) > 0 )
        {
            out.write( buf, 0, len );
        }
        out.flush();
        ins.close();
        out.close();
    }

    private boolean readConfigFile()
    {
        plugin.getUtil().consoleLog( Level.INFO, "Reading config File: " + ChatColor.AQUA + configDirectory + "config.yml" );

        skillLevelCommands = new ArrayList<>();
        skillMasteryCommands = new ArrayList<>();
        skillMaxCapeCommands = new ArrayList<>();

        FileConfiguration config = plugin.getConfig();

        dbType = config.getString( "Database Type" );
        host = config.getString( "MySQL Hostname" );
        port = config.getInt( "MySQL Port" );
        database = config.getString( "MySQL Database" );
        username = config.getString( "MySQL Username" );
        password = config.getString( "MySQL Password" );

        dbpath = config.getString( "Database File Name" );
        DwarfCraft.debugMessagesThreshold = config.getInt( "Debug Level" );
        sendGreeting = config.getBoolean( "Send Login Greet" );
        disableCacti = config.getBoolean( "Disable Farm Exploits" );
        worldBlacklist = config.getBoolean( "World Blacklist" );
        trainDelay = config.getInt( "Train Delay" );
        silkTouch = config.getBoolean( "Silk Touch" );
        defaultRace = config.getString( "Default Race" );
        vanilla = config.getBoolean( "Vanilla Race Enabled" );
        prefix = config.getBoolean( "Prefix Enabled" );
        prefixStr = config.getString( "Prefix" );
        maxLevel = config.getInt( "Max Skill Level" );
        raceLevelLimit = config.getInt( "Non-Racial Level Limit" );
        announce = config.getBoolean( "Announce Level Up" );
        announcementInterval = config.getInt( "Announcement Interval" );
        byID = config.getBoolean( "Sort DwarfTrainers by Unique ID" );
        hardcorePenalty = config.getBoolean( "Hardcore Race Change Penalty" );
        spawnTutorialBook = config.getBoolean( "Spawn Tutorial Book" );

        List<String> worldStrings = config.getStringList( "Disabled Worlds" );
        for ( String world : worldStrings )
            worlds.add( Bukkit.getServer().getWorld( world ) );

        clearCommands();

        skillLevelCommands.addAll( config.getStringList( "Skill Level Commands" ) );
        skillMasteryCommands.addAll( config.getStringList( "Skill Mastery Commands" ) );
        skillMaxCapeCommands.addAll( config.getStringList( "Skill Max Cape Commands" ) );

        return true;
    }

    private boolean readEffectsFile()
    {
        plugin.getUtil().consoleLog( Level.INFO, "Reading effects file: " + ChatColor.AQUA + configDirectory + "effects.csv" );
        try
        {
            CSVReader csv = new CSVReader( configDirectory + "effects.csv" );
            Iterator<CSVRecord> records = csv.getRecords();
            while ( records.hasNext() )
            {
                CSVRecord item = records.next();
                DwarfEffect effect = new DwarfEffect( item, plugin );
                DwarfSkill skill = skillsArray.get( effect.getId() / 10 );
                if ( skill != null )
                {
                    skill.getEffects().add( effect );
                }
            }
            return true;
        }
        catch ( FileNotFoundException fN )
        {
            fN.printStackTrace();
            return false;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return false;
        }
    }

    private boolean readRacesFile()
    {
        plugin.getUtil().consoleLog( Level.INFO, "Reading races file: " + ChatColor.AQUA + configDirectory + "races.yml" );

        if ( vanilla )
        {
            raceList.add( new DwarfRace( "Vanilla", new ArrayList<>(), "The all around balanced race (vanilla).", Material.GRASS ) );
            plugin.getUtil().consoleLog( Level.INFO, "Loaded vanilla race: Vanilla" );
        }

        FileConfiguration racesConfig = YamlConfiguration.loadConfiguration( new File( plugin.getDataFolder(), "races.yml" ) );
        Set<String> raceNames = racesConfig.getKeys( false );

        for ( String name : raceNames )
        {
            if ( name == null || name.equals( "" ) )
                continue;

            DwarfRace race = new DwarfRace( name );
            String[] raceIds = racesConfig.getString( name + ".SkillIDs" ).trim().split( "," );
            race.setSkills( new ArrayList<>() );

            for ( String raceId : raceIds )
                race.getSkills().add( Integer.parseInt( raceId.trim() ) );

            if ( race.getSkills().size() <= 0 )
                continue;

            race.setDesc( racesConfig.getString( name + ".Description", "" ) );
            race.setIcon( Material.matchMaterial( racesConfig.getString( name + ".Material Icon", "AIR" ) ) );
            race.setPrefixColour( racesConfig.getString( name + ".Prefix Colour", "&f" ) );

            if ( race.getIcon() == null || race.getIcon() == Material.AIR )
                continue;

            int maxAllowed = vanilla ? 44 : 45;
            if ( raceList.size() < maxAllowed )
            {
                raceList.add( race );
                plugin.getUtil().consoleLog( Level.INFO, "Loaded race: " + ChatColor.AQUA + race.getName() );
            }
            else
            {
                plugin.getUtil().consoleLog( Level.WARNING, "Did not load race: " + race.getName() + " as already at cap of " + maxAllowed + " races" );
            }
        }

        if ( defaultRace == null )
        {
            defaultRace = "";
        }
        else
        {
            if ( !checkRace( defaultRace ) )
                defaultRace = "";
        }

        return true;
    }

    private boolean readLocaleFile()
    {
        plugin.getUtil().consoleLog( Level.INFO, "Reading locale file: " + ChatColor.AQUA + configDirectory + "locale" + File.separator + "en_US.yml" );

        new Messages();
        FileConfiguration localeConfig = YamlConfiguration.loadConfiguration( new File( plugin.getDataFolder() + File.separator + "locale", "en_US.yml" ) );

        // Welcome Messages
        Messages.welcomePrefix = localeConfig.getString( "Welcome prefix" );
        Messages.welcome = localeConfig.getString( "Welcome" );
        Messages.announcementMessage = localeConfig.getString( "Announcement Message" );

        // Skillsheet Messages
        Messages.skillSheetHeader = localeConfig.getString( "Skillsheet.Header" );
        Messages.skillSheetSkillLine = localeConfig.getString( "Skillsheet.Skill Line" );
        Messages.skillSheetUntrainedSkillHeader = localeConfig.getString( "Skillsheet.Untrained Skill Header" );
        Messages.skillSheetUntrainedSkillLine = localeConfig.getString( "Skillsheet.Untrained Skill Line" );

        // Skill Info Messages
        Messages.skillInfoHeader = localeConfig.getString( "Skill Info.Header" );
        Messages.skillInfoMinorHeader = localeConfig.getString( "Skill Info.Subheader" );
        // SkillInfo EffectID Prefix
        Messages.skillInfoMaxSkillLevel = localeConfig.getString( "Skill Info.Max Skill Level" );
        Messages.skillInfoAtTrainerLevel = localeConfig.getString( "Skill Info.Max Trainer Level" );
        Messages.skillInfoTrainCostHeader = localeConfig.getString( "Skill Info.Train Cost Header" );
        Messages.skillInfoTrainCost = localeConfig.getString( "Skill Info.Train Cost" );
        // EffectInfo prefix

        // Race Messages
        Messages.raceCheck = localeConfig.getString( "Race Messages.Race Info" );
        Messages.adminRaceCheck = localeConfig.getString( "Race Messages.Admin Race Info" );
        Messages.alreadyRace = localeConfig.getString( "Race Messages.Already Race" );
        Messages.changedRace = localeConfig.getString( "Race Messages.Changed Race" );
        Messages.confirmRace = localeConfig.getString( "Race Messages.Confirm Race" );
        Messages.raceDoesNotExist = localeConfig.getString( "Race Messages.Race Failed" );

        // Trainer Messages
        Messages.chooseARace = localeConfig.getString( "Trainer Messages.Choose Race" );
        Messages.trainSkillPrefix = localeConfig.getString( "Trainer Messages.Train Skill Prefix" );
        Messages.raceDoesNotContainSkill = localeConfig.getString( "Trainer Messages.Skill Blocked" );
        Messages.raceDoesNotSpecialize = localeConfig.getString( "Trainer Messages.Non-Racial Skill" );
        Messages.maxSkillLevel = localeConfig.getString( "Trainer Messages.Max Skill Level" );
        Messages.trainerMaxLevel = localeConfig.getString( "Trainer Messages.Max Level" );
        Messages.trainerLevelTooHigh = localeConfig.getString( "Trainer Messages.Level Too High" );
        Messages.noMoreItemNeeded = localeConfig.getString( "Trainer Messages.No More Item Needed" );
        Messages.moreItemNeeded = localeConfig.getString( "Trainer Messages.More Item Needed" );
        Messages.trainingSuccessful = localeConfig.getString( "Trainer Messages.Training Successful" );
        Messages.depositSuccessful = localeConfig.getString( "Trainer Messages.Deposit Successful" );
        Messages.trainerGUITitle = localeConfig.getString( "Trainer Messages.GUI Title" );
        Messages.trainerOccupied = localeConfig.getString( "Trainer Messages.Occupied" );
        Messages.trainerCooldown = localeConfig.getString( "Trainer Messages.Cooldown" );

        // Effect Messages
        Messages.describeGeneral = localeConfig.getString( "Effect Descriptions.General" );
        Messages.describeLevelBlockdrop = localeConfig.getString( "Effect Descriptions.Block Drop" );
        Messages.describeLevelMobdrop = localeConfig.getString( "Effect Descriptions.Mob Drop" );
        Messages.describeLevelMobdropNoCreature = localeConfig.getString( "Effect Descriptions.Mob Drop (no creature)" );
        Messages.describeLevelSwordDurability = localeConfig.getString( "Effect Descriptions.Sword Durability" );
        Messages.describeLevelPVPDamage = localeConfig.getString( "Effect Descriptions.PVP Damage" );
        Messages.describeLevelPVEDamage = localeConfig.getString( "Effect Descriptions.PVE Damage" );
        Messages.describeLevelExplosionDamageMore = localeConfig.getString( "Effect Descriptions.Explosion Damage (more)" );
        Messages.describeLevelExplosionDamageLess = localeConfig.getString( "Effect Descriptions.Explosion Damage (less)" );
        Messages.describeLevelFireDamageMore = localeConfig.getString( "Effect Descriptions.Fire Damage (more)" );
        Messages.describeLevelFireDamageLess = localeConfig.getString( "Effect Descriptions.Fire Damage (less)" );
        Messages.describeLevelFallingDamageMore = localeConfig.getString( "Effect Descriptions.Fall Damage (more)" );
        Messages.describeLevelFallingDamageLess = localeConfig.getString( "Effect Descriptions.Fall Damage (less)" );
        Messages.describeLevelFallThreshold = localeConfig.getString( "Effect Descriptions.Fall Threshold" );
        Messages.describeLevelPlowDurability = localeConfig.getString( "Effect Descriptions.Hoe Durability" );
        Messages.describeLevelToolDurability = localeConfig.getString( "Effect Descriptions.Tool Durability" );
        Messages.describeLevelRodDurability = localeConfig.getString( "Effect Descriptions.Rod Durability" );
        Messages.describeLevelEat = localeConfig.getString( "Effect Descriptions.Eat" );
        Messages.describeLevelCraft = localeConfig.getString( "Effect Descriptions.Craft" );
        Messages.describeLevelPlow = localeConfig.getString( "Effect Descriptions.Hoe" );
        Messages.describeLevelFish = localeConfig.getString( "Effect Descriptions.Fish" );
        Messages.describeLevelBrew = localeConfig.getString( "Effect Descriptions.Brew" );
        Messages.describeLevelDigTime = localeConfig.getString( "Effect Descriptions.Dig Time" );
        Messages.describeLevelBowAttack = localeConfig.getString( "Effect Descriptions.Bow Attack" );
        Messages.describeLevelVehicleDrop = localeConfig.getString( "Effect Descriptions.Vehicle Drop" );
        Messages.describeLevelVehicleMove = localeConfig.getString( "Effect Descriptions.Vehicle Move" );
        Messages.describeLevelSmelt = localeConfig.getString( "Effect Descriptions.Smelt" );
        Messages.describeLevelShear = localeConfig.getString( "Effect Descriptions.Shear" );
        Messages.effectLevelColorGreaterThanNormal = localeConfig.getString( "Effect Descriptions.Level Color (greater)" );
        Messages.effectLevelColorEqualToNormal = localeConfig.getString( "Effect Descriptions.Level Color (equal)" );
        Messages.effectLevelColorLessThanNormal = localeConfig.getString( "Effect Descriptions.Level Color (less)" );

        List<String> tutorialPages = localeConfig.getStringList( "Tutorial Pages" );

        // If there is at least one tutorial page, reset default messages and add custom messages
        if ( tutorialPages.size() > 0 )
        {
            Messages.tutorial.clear();
            Messages.tutorial.addAll( tutorialPages );
        }

        return true;
    }

    private boolean readSkillsFile()
    {
        plugin.getUtil().consoleLog( Level.INFO, "Reading skills file: " + ChatColor.AQUA + configDirectory + "skills.csv" );
        try
        {
            CSVReader csv = new CSVReader( configDirectory + "skills.csv" );
            Iterator<CSVRecord> records = csv.getRecords();
            while ( records.hasNext() )
            {
                CSVRecord item = records.next();
                DwarfItemHolder dih1 = plugin.getUtil().getDwarfItemHolder( item, "Item1" );
                DwarfItemHolder dih2 = plugin.getUtil().getDwarfItemHolder( item, "Item2" );
                DwarfItemHolder dih3 = plugin.getUtil().getDwarfItemHolder( item, "Item3" );

                if ( dih1.getMaterials().isEmpty() || dih1.getMaterials().isEmpty() || dih1.getMaterials().isEmpty() ) // Skip the Skill if the tag reading fails TODO: Improve the error msg
                {
                    plugin.getUtil().consoleLog( Level.INFO, "Skipping skill " + item.getString( "Name" + " as couldn't find one of the tags" ) );
                    continue;
                }

                DwarfTrainingItem item1 = new DwarfTrainingItem( dih1, item.getDouble( "Item1Base" ), item.getInt( "Item1Max" ) );
                DwarfTrainingItem item2 = new DwarfTrainingItem( dih2, item.getDouble( "Item2Base" ), item.getInt( "Item2Max" ) );
                DwarfTrainingItem item3 = new DwarfTrainingItem( dih3, item.getDouble( "Item3Base" ), item.getInt( "Item3Max" ) );

                DwarfSkill skill = new DwarfSkill( item.getInt( "ID" ), item.getString( "Name" ), 0, new ArrayList<>(), item1, item2, item3, Material.matchMaterial( item.getString( "Held" ) ) );
                skillsArray.put( skill.getId(), skill );
            }
            return true;
        }
        catch ( Exception fN )
        {
            fN.printStackTrace();
        }
        return false;
    }

    public String getDefaultRace()
    {
        return defaultRace;
    }

    public ArrayList<DwarfRace> getRaceList()
    {
        return raceList;
    }

    public boolean checkRace( String name )
    {
        for ( DwarfRace r : raceList )
        {
            if ( r != null )
            {
                if ( r.getName().equalsIgnoreCase( name ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public String getPrefix()
    {
        return prefixStr;
    }

    public int getTrainDelay()
    {
        return trainDelay;
    }

    public int getMaxSkillLevel()
    {
        return maxLevel;
    }

    public int getRaceLevelLimit()
    {
        return raceLevelLimit;
    }

    public int getAnnouncementInterval()
    {
        return announcementInterval;
    }

    public ArrayList<String> getSkillLevelCommands()
    {
        return this.skillLevelCommands;
    }

    public ArrayList<String> getSkillMasteryCommands()
    {
        return this.skillMasteryCommands;
    }

    public ArrayList<String> getSkillMaxCapeCommands()
    {
        return this.skillMaxCapeCommands;
    }

    public void clearCommands()
    {
        this.skillLevelCommands = new ArrayList<>();
        this.skillMasteryCommands = new ArrayList<>();
        this.skillMaxCapeCommands = new ArrayList<>();
    }
}
