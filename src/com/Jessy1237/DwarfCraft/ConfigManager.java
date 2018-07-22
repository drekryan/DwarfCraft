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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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
            if ( !readSkillsFile() || !readEffectsFile() || !readMessagesFile() || !readRacesFile() )
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

    protected String getDbPath()
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
            // Main Plugin Configuration
            plugin.saveDefaultConfig();
            if ( !readConfigFile() )
            {
                plugin.getUtil().consoleLog( Level.SEVERE, "Failed to Enable DwarfCraft configs" );
                plugin.getServer().getPluginManager().disablePlugin( plugin );
            }

            // Supporting Data Files
            String[] mfiles = { "skills.csv", "effects.csv", "messages.config", "dwarfcraft.db", "races.yml" };
            for ( String mfile : mfiles )
            {
                File file = new File( root, mfile );
                if ( !file.exists() )
                {
                    file.createNewFile();
                    CopyFile( "/default_files/" + mfile, file );
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

    private boolean readMessagesFile()
    {
        plugin.getUtil().consoleLog( Level.INFO, "Reading messages file: " + ChatColor.AQUA + configDirectory + "messages.config" );

        // Loads the messages class after the config is read but before all the
        // messages are read.
        new Messages();
        try
        {
            FileReader fr = new FileReader( configDirectory + "messages.config" );
            BufferedReader br = new BufferedReader( fr );

            String line = br.readLine();
            while ( line != null )
            {
                if ( line.length() == 0 )
                {
                    line = br.readLine();
                    continue;
                }
                if ( line.charAt( 0 ) == '#' )
                {
                    line = br.readLine();
                    continue;
                }

                if ( line.indexOf( ":" ) <= 0 )
                {
                    line = br.readLine();
                    continue;
                }

                String split[] = line.split( ":" );
                if ( split.length != 2 )
                {
                    line = br.readLine();
                    continue;
                }

                String name = split[0];
                String message = split[1];

                if ( message == null )
                {
                    plugin.getUtil().consoleLog( Level.WARNING, "Null Message: " + name + ", " + message );
                }
                else
                {
                    if ( !message.trim().equals( "" ) || !message.equals( null ) )
                    {
                        if ( name.equalsIgnoreCase( "Welcome prefix" ) )
                            Messages.welcomePrefix = message;
                        if ( name.equalsIgnoreCase( "Welcome" ) )
                            Messages.welcome = message;
                        if ( name.equalsIgnoreCase( "Announcement Message" ) )
                            Messages.announcementMessage = message;
                        if ( name.equalsIgnoreCase( "SkillSheet prefix" ) )
                            Messages.skillSheetPrefix = message;
                        if ( name.equalsIgnoreCase( "SkillSheet header" ) )
                            Messages.skillSheetHeader = message;
                        if ( name.equalsIgnoreCase( "SkillSheet skill line" ) )
                            Messages.skillSheetSkillLine = message;
                        if ( name.equalsIgnoreCase( "SkillSheet untrained skill header" ) )
                            Messages.skillSheetUntrainedSkillHeader = message;
                        if ( name.equalsIgnoreCase( "SkillSheet untrained skill line" ) )
                            Messages.skillSheetUntrainedSkillLine = message;

                        if ( name.equalsIgnoreCase( "SkillInfo header" ) )
                            Messages.skillInfoHeader = message;
                        if ( name.equalsIgnoreCase( "SkillInfo minor header" ) )
                            Messages.skillInfoMinorHeader = message;
                        if ( name.equalsIgnoreCase( "SkillInfo EffectID Prefix" ) )
                            Messages.skillInfoEffectIDPrefix = message;
                        if ( name.equalsIgnoreCase( "SkillInfo max skill level" ) )
                            Messages.skillInfoMaxSkillLevel = message;
                        if ( name.equalsIgnoreCase( "SkillInfo at trainer level" ) )
                            Messages.skillInfoAtTrainerLevel = message;
                        if ( name.equalsIgnoreCase( "SkillInfo train cost header" ) )
                            Messages.skillInfoTrainCostHeader = message;
                        if ( name.equalsIgnoreCase( "SkillInfo train cost" ) )
                            Messages.skillInfoTrainCost = message;

                        if ( name.equalsIgnoreCase( "EffectInfo prefix" ) )
                            Messages.effectInfoPrefix = message;

                        if ( name.equalsIgnoreCase( "Race check" ) )
                            Messages.raceCheck = message;
                        if ( name.equalsIgnoreCase( "Admin race check" ) )
                            Messages.adminRaceCheck = message;
                        if ( name.equalsIgnoreCase( "Already race" ) )
                            Messages.alreadyRace = message;
                        if ( name.equalsIgnoreCase( "Changed race" ) )
                            Messages.changedRace = message;
                        if ( name.equalsIgnoreCase( "Confirm race" ) )
                            Messages.confirmRace = message;
                        if ( name.equalsIgnoreCase( "Race does not exist" ) )
                            Messages.raceDoesNotExist = message;

                        if ( name.equalsIgnoreCase( "Choose a race" ) )
                            Messages.chooseARace = message;
                        if ( name.equalsIgnoreCase( "Train skill prefix" ) )
                            Messages.trainSkillPrefix = message;
                        if ( name.equalsIgnoreCase( "Race does not contain skill" ) )
                            Messages.raceDoesNotContainSkill = message;
                        if ( name.equalsIgnoreCase( "Race does not specialize" ) )
                            Messages.raceDoesNotSpecialize = message;
                        if ( name.equalsIgnoreCase( "Max skill level" ) )
                            Messages.maxSkillLevel = message;
                        if ( name.equalsIgnoreCase( "Trainer max level" ) )
                            Messages.trainerMaxLevel = message;
                        if ( name.equalsIgnoreCase( "Trainer level too high" ) )
                            Messages.trainerLevelTooHigh = message;
                        if ( name.equalsIgnoreCase( "No more item needed" ) )
                            Messages.noMoreItemNeeded = message;
                        if ( name.equalsIgnoreCase( "More item needed" ) )
                            Messages.moreItemNeeded = message;
                        if ( name.equalsIgnoreCase( "Training successful" ) )
                            Messages.trainingSuccessful = message;
                        if ( name.equalsIgnoreCase( "Deposit successful" ) )
                            Messages.depositSuccessful = message;
                        if ( name.equalsIgnoreCase( "Trainer GUI Title" ) )
                            Messages.trainerGUITitle = message;
                        if ( name.equalsIgnoreCase( "Trainer occupied" ) )
                            Messages.trainerOccupied = message;
                        if ( name.equalsIgnoreCase( "Trainer cooldown" ) )
                            Messages.trainerCooldown = message;

                        if ( name.equalsIgnoreCase( "Describe general" ) )
                            Messages.describeGeneral = message;
                        if ( name.equalsIgnoreCase( "Describe level blockdrop" ) )
                            Messages.describeLevelBlockdrop = message;
                        if ( name.equalsIgnoreCase( "Describe level mobdrop" ) )
                            Messages.describeLevelMobdrop = message;
                        if ( name.equalsIgnoreCase( "Describe level mobdrop no creature" ) )
                            Messages.describeLevelMobdropNoCreature = message;
                        if ( name.equalsIgnoreCase( "Describe level sword durability" ) )
                            Messages.describeLevelSwordDurability = message;
                        if ( name.equalsIgnoreCase( "Describe level pvp damage" ) )
                            Messages.describeLevelPVPDamage = message;
                        if ( name.equalsIgnoreCase( "Describe level pve damage" ) )
                            Messages.describeLevelPVEDamage = message;
                        if ( name.equalsIgnoreCase( "Describe level explosion damage more" ) )
                            Messages.describeLevelExplosionDamageMore = message;
                        if ( name.equalsIgnoreCase( "Describe level explosion damage less" ) )
                            Messages.describeLevelExplosionDamageLess = message;
                        if ( name.equalsIgnoreCase( "Describe level fire damage more" ) )
                            Messages.describeLevelFireDamageMore = message;
                        if ( name.equalsIgnoreCase( "Describe level fire damage less" ) )
                            Messages.describeLevelFireDamageLess = message;
                        if ( name.equalsIgnoreCase( "Describe level falling damage more" ) )
                            Messages.describeLevelFallingDamageMore = message;
                        if ( name.equalsIgnoreCase( "Describe level falling damage less" ) )
                            Messages.describeLevelFallingDamageLess = message;
                        if ( name.equalsIgnoreCase( "Describe level fall threshold" ) )
                            Messages.describeLevelFallThreshold = message;
                        if ( name.equalsIgnoreCase( "Describe level plow durability" ) )
                            Messages.describeLevelPlowDurability = message;
                        if ( name.equalsIgnoreCase( "Describe level tool durability" ) )
                            Messages.describeLevelToolDurability = message;
                        if ( name.equalsIgnoreCase( "Describe level rod durability" ) )
                            Messages.describeLevelRodDurability = message;
                        if ( name.equalsIgnoreCase( "Describe level eat" ) )
                            Messages.describeLevelEat = message;
                        if ( name.equalsIgnoreCase( "Describe level craft" ) )
                            Messages.describeLevelCraft = message;
                        if ( name.equalsIgnoreCase( "Describe level plow" ) )
                            Messages.describeLevelPlow = message;
                        if ( name.equalsIgnoreCase( "Describle level fish" ) )
                            Messages.describeLevelFish = message;
                        if ( name.equalsIgnoreCase( "Describe level brew" ) )
                            Messages.describeLevelBrew = message;
                        if ( name.equalsIgnoreCase( "Describe level dig time" ) )
                            Messages.describeLevelDigTime = message;
                        if ( name.equalsIgnoreCase( "Describe level bow attack" ) )
                            Messages.describeLevelBowAttack = message;
                        if ( name.equalsIgnoreCase( "Describe level vehicle drop" ) )
                            Messages.describeLevelVehicleDrop = message;
                        if ( name.equalsIgnoreCase( "Describe level vehicle move" ) )
                            Messages.describeLevelVehicleMove = message;
                        if ( name.equalsIgnoreCase( "Describe level smelt" ) )
                            Messages.describeLevelSmelt = message;
                        if ( name.equalsIgnoreCase( "Describe level shear" ) )
                            Messages.describeLevelShear = message;
                        if ( name.equalsIgnoreCase( "Effect level color greater than normal" ) )
                            Messages.effectLevelColorGreaterThanNormal = message;
                        if ( name.equalsIgnoreCase( "Effect level color equal to normal" ) )
                            Messages.effectLevelColorEqualToNormal = message;
                        if ( name.equalsIgnoreCase( "Effect level color less than normal" ) )
                            Messages.effectLevelColorLessThanNormal = message;
                        if ( name.equalsIgnoreCase( "Vanilla Race Message" ) )
                            Messages.vanillaRace = message;
                        if ( name.equalsIgnoreCase( "Tutorial Messages" ) )
                        {
                            ArrayList<String> tutorial = new ArrayList<String>();
                            if ( br.readLine().equalsIgnoreCase( "<TUTORIAL>" ) )
                            {
                                StringBuffer sb = new StringBuffer();
                                boolean foundEndTag = readTutorial( sb, br );

                                if ( !foundEndTag )
                                {
                                    plugin.getUtil().consoleLog( Level.SEVERE, "Unable to find the ending Tutorial XML tag. Using default tutorial." );
                                }
                                else
                                {
                                    tutorial = parseTutorialPages( sb );
                                }
                            }
                            else
                            {
                                plugin.getUtil().consoleLog( Level.SEVERE, "Unable to find the opening Tutorial XML tag. Using default tutorial." );
                            }

                            if ( !tutorial.isEmpty() )
                                Messages.tutorial = tutorial;
                        }
                    }
                    else
                    {
                        plugin.getUtil().consoleLog( Level.WARNING, "Null Message: " + name + ", " + message );
                    }
                }
                line = br.readLine();
            }

        }
        catch ( FileNotFoundException fN )
        {
            fN.printStackTrace();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Reads the tutorial from the buffered reader by character and inputs them into the string buffer
     * 
     * @param sb String buffer to fill
     * @param br The reader that is reading the config
     * @return True if the ending tutorial tag was found otherwise returns fails
     * @throws IOException when br.read fails
     */
    private boolean readTutorial( StringBuffer sb, BufferedReader br ) throws IOException
    {
        boolean foundEndTag = false;
        boolean possibleTag = false;
        boolean backSlash = false;
        char c = ( char ) br.read();
        StringBuffer endTag = new StringBuffer();
        while ( !foundEndTag && c != -1 )
        {
            if ( c == '<' )
                possibleTag = true;

            if ( backSlash )
            {
                checkSpecialChar( sb, br, c );
                backSlash = false;
                c = ( char ) br.read();
            }

            if ( c == '\\' )
                backSlash = true;

            if ( possibleTag )
            {
                endTag.append( c );
            }
            else if ( !backSlash )
            {
                sb.append( c );
            }

            if ( !"</TUTORIAL>".contains( endTag.toString() ) && possibleTag )
            {
                possibleTag = false;
                sb.append( endTag );
                endTag = new StringBuffer();
            }
            else if ( possibleTag && endTag.toString().equalsIgnoreCase( "</TUTORIAL>" ) )
            {
                foundEndTag = true;
            }
            c = ( char ) br.read();
        }

        return foundEndTag;
    }

    /**
     * Checks to see if the next read char would be able to combine with a '\' to become a special char
     * 
     * @param sb The String Buffer to add the special char to
     * @param br The buffered reader in which the text is being read from
     * @param c The most recent char that has been read
     * @throws IOException if br.read() fails
     */
    private void checkSpecialChar( StringBuffer sb, BufferedReader br, char c ) throws IOException
    {
        switch ( c )
        {
            case 'n':
                sb.append( '\n' );
                break;
            case 'r':
                sb.append( '\r' );
                break;
            case '"':
                sb.append( '\"' );
                break;
            case '\\':
                sb.append( '\\' );
                checkSpecialChar( sb, br, ( char ) br.read() );
                break;
            default:
                sb.append( "\\" + c );
                break;
        }
    }

    /**
     * Parses the String Buffer into an array list containing separate pages
     * 
     * @param sb The string buffer containing all the characters read from the config
     * @return An array list containing separate pages
     */
    private ArrayList<String> parseTutorialPages( StringBuffer sb )
    {
        ArrayList<String> tutorial = new ArrayList<String>();
        String pages = sb.toString();
        int numPages = 0;
        while ( pages != null )
        {
            int startIndex = pages.indexOf( "<PAGE>", 0 );
            int finalIndex = pages.indexOf( "</PAGE>", 0 );
            if ( startIndex == -1 || finalIndex == -1 )
            {
                plugin.getUtil().consoleLog( Level.SEVERE, "Could not find the Page XML tags. Stopped adding tutorial pages after page number " + numPages );
                break;
            }

            numPages++;

            tutorial.add( pages.substring( startIndex + 6, finalIndex ) );

            if ( finalIndex + 9 >= pages.length() )
            {
                pages = null;
            }
            else
            {
                pages = pages.substring( finalIndex + 8 );
            }
        }

        return tutorial;
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
