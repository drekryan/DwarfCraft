package com.Jessy1237.DwarfCraft;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.jbls.LexManos.CSV.CSVReader;
import org.jbls.LexManos.CSV.CSVRecord;

import com.Jessy1237.DwarfCraft.events.DwarfLoadRacesEvent;
import com.Jessy1237.DwarfCraft.events.DwarfLoadSkillsEvent;
import com.Jessy1237.DwarfCraft.models.DwarfEffect;
import com.Jessy1237.DwarfCraft.models.DwarfRace;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;
import com.Jessy1237.DwarfCraft.models.DwarfTrainingItem;

public final class ConfigManager
{

    private final DwarfCraft plugin;

    private final String configDirectory;

    private String configMainFileName;
    private String configWorldFileName;
    private String cfgBlockGroupsFile;
    private String dbpath;
    private Integer trainDelay;
    private Integer announcementInterval;
    private String announcementMessage;
    private Integer maxLevel;
    private Integer raceLevelLimit;
    private String prefixStr;

    private HashMap<Integer, DwarfSkill> skillsArray = new HashMap<Integer, DwarfSkill>();
    public ArrayList<World> worlds = new ArrayList<World>();
    private HashMap<String, ArrayList<Material>> blockGroups = new HashMap<String, ArrayList<Material>>();

    private ArrayList<DwarfRace> raceList = new ArrayList<DwarfRace>();
    private String defaultRace;

    public boolean sendGreeting = false;
    public boolean disableCacti = true;
    public boolean worldBlacklist = false;
    public boolean silkTouch = true;
    public boolean vanilla = true;
    public boolean buildingblocks = true;
    public boolean prefix = false;
    public boolean announce = false;
    public boolean byID = true;
    public boolean softcore = false;

    @SuppressWarnings( "unchecked" )
    protected ConfigManager( DwarfCraft plugin, String directory, String paramsFileName )
    {
        this.plugin = plugin;
        if ( !directory.endsWith( File.separator ) )
            directory += File.separator;
        configDirectory = directory;
        configMainFileName = paramsFileName;
        checkFiles( configDirectory );

        try
        {
            if ( !readSkillsFile() || !readEffectsFile() || !readMessagesFile() || !readWorldFile() || !readRacesFile() || !readBlockGroupsFile() )
            {
                System.out.println( "[SEVERE] Failed to Enable DwarfCraft configs" );
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
            System.out.println( "[SEVERE] Failed to Enable DwarfCraft configs" );
            plugin.getServer().getPluginManager().disablePlugin( plugin );
        }

    }

    public HashMap<Integer, DwarfSkill> getAllSkills()
    {
        HashMap<Integer, DwarfSkill> newSkillsArray = new HashMap<Integer, DwarfSkill>();
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

    private void getDefaultValues()
    {
        if ( configWorldFileName == null )
            configWorldFileName = "world-blacklist.config";
        if ( dbpath == null )
            dbpath = "dwarfcraft.db";
        if ( cfgBlockGroupsFile == null )
            cfgBlockGroupsFile = "block-groups.config";
        if ( defaultRace == null )
            defaultRace = "NULL";
        if ( trainDelay == null )
            trainDelay = 2;
        if ( maxLevel == null )
            maxLevel = 30;
        if ( raceLevelLimit == null )
            raceLevelLimit = 5;
        if ( announcementInterval == null )
            announcementInterval = 5;
        if ( prefixStr == null )
            prefixStr = "[%racename%]";
        if ( announcementMessage == null )
            announcementMessage = "%playername% has just leveled %skillname% to level %level%!";
    }

    private void checkFiles( String path )
    {
        File root = new File( path );
        if ( !root.exists() )
            root.mkdirs();
        try
        {
            File file = new File( root, "DwarfCraft.config" );
            if ( !file.exists() )
            {
                file.createNewFile();
                CopyFile( "/default_files/DwarfCraft.config", file );
            }

            if ( !readConfigFile() )
            {
                System.out.println( "[SEVERE] Failed to Enable DwarfCraft configs" );
                plugin.getServer().getPluginManager().disablePlugin( plugin );
            }
            getDefaultValues();

            String[] mfiles = { "skills.csv", "effects.csv", "messages.config", "dwarfcraft.db", "world-blacklist.config", "races.config", "block-groups.config" };
            for ( String mfile : mfiles )
            {
                file = new File( root, mfile );
                if ( !file.exists() )
                {
                    file.createNewFile();
                    CopyFile( "/default_files/" + mfile, file );
                }
            }
        }
        catch ( Exception e )
        {
            System.out.println( "DC: ERROR: Could not verify files: " + e.toString() );
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

    @SuppressWarnings( "resource" )
    private boolean readConfigFile()
    {
        try
        {
            System.out.println( "[DwarfCraft] Reading Config File: " + configDirectory + configMainFileName );
            getDefaultValues();
            FileReader fr = new FileReader( configDirectory + configMainFileName );
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
                String[] theline = line.split( ":" );
                if ( theline.length != 2 )
                {
                    line = br.readLine();
                    continue;
                }
                if ( theline[0].equalsIgnoreCase( "Database File Name" ) )
                    dbpath = theline[1].trim();
                if ( theline[0].equalsIgnoreCase( "Debug Level" ) )
                    DwarfCraft.debugMessagesThreshold = Integer.parseInt( theline[1].trim() );
                if ( theline[0].equalsIgnoreCase( "Send Login Greet" ) )
                    sendGreeting = Boolean.parseBoolean( theline[1].trim() );
                if ( theline[0].equalsIgnoreCase( "Disable Farm Exploits" ) )
                    disableCacti = Boolean.parseBoolean( theline[1].trim() );
                if ( theline[0].equalsIgnoreCase( "World Blacklist" ) )
                    worldBlacklist = Boolean.parseBoolean( theline[1].trim() );
                if ( theline[0].equalsIgnoreCase( "Train Delay" ) )
                    trainDelay = Integer.parseInt( theline[1].trim() );
                if ( theline[0].equalsIgnoreCase( "Silk Touch" ) )
                    silkTouch = Boolean.parseBoolean( theline[1].trim() );
                if ( theline[0].equalsIgnoreCase( "Group Equivalent Building Blocks" ) )
                    buildingblocks = Boolean.parseBoolean( theline[1].trim() );
                if ( theline[0].equalsIgnoreCase( "Default Race" ) )
                    defaultRace = theline[1].trim();
                if ( theline[0].equalsIgnoreCase( "Vanilla Race Enabled" ) )
                    vanilla = Boolean.parseBoolean( theline[1].trim() );
                if ( theline[0].equalsIgnoreCase( "Prefix Enabled" ) )
                    prefix = Boolean.parseBoolean( theline[1].trim() );
                if ( theline[0].equalsIgnoreCase( "Prefix" ) )
                    prefixStr = theline[1].trim();
                if ( theline[0].equalsIgnoreCase( "Max Skill Level" ) )
                    maxLevel = Integer.parseInt( theline[1].trim() );
                if ( theline[0].equalsIgnoreCase( "Race Level Limit" ) )
                    raceLevelLimit = Integer.parseInt( theline[1].trim() );
                if ( theline[0].equalsIgnoreCase( "Announce Level Up" ) )
                    announce = Boolean.parseBoolean( theline[1].trim() );
                if ( theline[0].equalsIgnoreCase( "Announcement Interval" ) )
                    announcementInterval = Integer.parseInt( theline[1].trim() );
                if ( theline[0].equalsIgnoreCase( "Announcement Message" ) )
                    announcementMessage = theline[1].trim();
                if ( theline[0].equalsIgnoreCase( "Sort DwarfTrainers by Unique ID" ) )
                    byID = Boolean.parseBoolean( theline[1].trim() );
                if ( theline[0].equalsIgnoreCase( "Softcore race skill reset" ) )
                    softcore = Boolean.parseBoolean( theline[1].trim() );

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

    @SuppressWarnings( "resource" )
    private boolean readWorldFile()
    {
        System.out.println( "[DwarfCraft] Reading world blacklist file: " + configDirectory + configWorldFileName );

        FileReader fr;
        try
        {
            fr = new FileReader( configDirectory + configWorldFileName );
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
                String[] theline = line.split( "-" );
                if ( theline.length > 2 )
                {
                    line = br.readLine();
                    continue;
                }

                if ( theline[0].equalsIgnoreCase( " " ) )
                    worlds.add( Bukkit.getServer().getWorld( theline[1].trim() ) );

                line = br.readLine();
            }
        }
        catch ( FileNotFoundException e )
        {
            e.printStackTrace();
            return false;
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean readEffectsFile()
    {
        System.out.println( "[DwarfCraft] Reading effects file: " + configDirectory + "effects.csv" );
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

    @SuppressWarnings( "resource" )
    protected boolean readRacesFile()
    {
        System.out.println( "[DwarfCraft] Reading races file: " + configDirectory + "races.config" );

        if ( vanilla )
        {
            raceList.add( new DwarfRace( "Vanilla", new ArrayList<>(), "The all round balanced race (vanilla).", Material.GRASS ) );
            System.out.println( "[DwarfCraft] Loaded vanilla race: Vanilla" );
        }

        try
        {
            FileReader fr = new FileReader( configDirectory + "races.config" );
            BufferedReader br = new BufferedReader( fr );
            String line = br.readLine();
            boolean name = false;
            boolean desc = false;
            boolean skills = false;
            boolean prefix = false;
            boolean hasIcon = false;
            DwarfRace race = null;
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
                String[] theline = line.split( ":" );
                if ( theline.length != 2 )
                {
                    line = br.readLine();
                    continue;
                }
                if ( theline[0].equalsIgnoreCase( "Name" ) )
                {
                    race = new DwarfRace( theline[1].trim() );
                    name = true;
                    line = br.readLine();
                }
                if ( theline[0].equalsIgnoreCase( "SkillIDs" ) )
                {
                    String ids[] = theline[1].trim().split( "," );
                    race.setSkills( new ArrayList<Integer>() );
                    for ( int i = 0; i < ids.length; i++ )
                    {
                        race.getSkills().add( Integer.parseInt( ids[i].trim() ) );
                    }

                    skills = true;
                    line = br.readLine();
                }
                if ( theline[0].equalsIgnoreCase( "Description" ) )
                {
                    race.setDesc( theline[1].trim() );

                    desc = true;
                    line = br.readLine();
                }
                if ( theline[0].equalsIgnoreCase( "Prefix Colour" ) )
                {
                    race.setPrefixColour( theline[1].trim() );

                    prefix = true;
                    line = br.readLine();
                }
                if ( theline[0].equalsIgnoreCase( "Material Icon" ) )
                {
                    Material icon = Material.matchMaterial( theline[1].trim() );
                    if ( icon != null )
                    {
                        if ( icon != Material.AIR )
                        {
                            race.setIcon( icon );
                            hasIcon = true;
                            line = br.readLine();
                        }
                    }
                }
                if ( name && desc && skills && prefix && hasIcon )
                {
                    if ( raceList.size() < 9 )
                    {
                        raceList.add( race );
                        name = false;
                        desc = false;
                        skills = false;
                        System.out.println( "[DwarfCraft] Loaded race: " + race.getName() );
                    }
                    else
                    {
                        System.out.println( "[DwarfCraft] Did not load race: " + race.getName() + " as already at cap of 9 races" );
                    }
                    continue;
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        if ( defaultRace == null )
        {
            defaultRace = "NULL";
        }
        else
        {
            if ( !checkRace( defaultRace ) )
                defaultRace = "NULL";
        }
        return true;
    }

    @SuppressWarnings( { "resource", "null" } )
    private boolean readMessagesFile()
    {
        System.out.println( "[DwarfCraft] Reading messages file: " + configDirectory + "messages.config" );

        // Loads the messages class after the config is read but before all the
        // messages are read.
        new Messages( plugin );
        try
        {
            getDefaultValues();
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

                if ( message != null || !message.trim().equals( "" ) || !message.equals( null ) )
                {
                    if ( name.equalsIgnoreCase( "Welcome prefix" ) )
                        Messages.welcomePrefix = message;
                    if ( name.equalsIgnoreCase( "Welcome" ) )
                        Messages.welcome = message;
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
                }
                else
                {
                    System.out.println( "Null Message: " + name + ", " + message );
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

    private boolean readSkillsFile()
    {
        System.out.println( "[DwarfCraft] Reading skills file: " + configDirectory + "skills.csv" );
        try
        {
            CSVReader csv = new CSVReader( configDirectory + "skills.csv" );
            Iterator<CSVRecord> records = csv.getRecords();
            while ( records.hasNext() )
            {
                CSVRecord item = records.next();

                DwarfSkill skill = new DwarfSkill( item.getInt( "ID" ), item.getString( "Name" ), 0, new ArrayList<DwarfEffect>(), new DwarfTrainingItem( plugin.getUtil().parseItem( item.getString( "Item1" ) ), item.getDouble( "Item1Base" ), item.getInt( "Item1Max" ) ), new DwarfTrainingItem( plugin
                        .getUtil().parseItem( item.getString( "Item2" ) ), item.getDouble( "Item2Base" ), item.getInt( "Item2Max" ) ), new DwarfTrainingItem( plugin.getUtil().parseItem( item.getString( "Item3" ) ), item.getDouble( "Item3Base" ), item.getInt( "Item3Max" ) ), Material
                                .matchMaterial( item.getString( "Held" ) ) );

                skillsArray.put( skill.getId(), skill );

            }
            return true;
        }
        catch ( FileNotFoundException fN )
        {
            fN.printStackTrace();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return false;
    }

    private boolean readBlockGroupsFile()
    {
        System.out.println( "[DwarfCraft] Reading Block Groups file: " + configDirectory + cfgBlockGroupsFile );

        try
        {
            FileReader fr = new FileReader( configDirectory + cfgBlockGroupsFile );
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
                if ( line.indexOf( ':' ) <= 0 )
                {
                    line = br.readLine();
                    continue;
                }

                String[] split = line.split( ":" );

                if ( split.length > 2 || split.length == 0 || split == null )
                {
                    line = br.readLine();
                    continue;
                }

                if ( split[0] == null || split[0] == "" )
                {
                    line = br.readLine();
                    continue;
                }

                String[] ints = split[1].split( "," );
                ArrayList<Material> blocks = new ArrayList<Material>();

                if ( ints.length == 0 || ints == null )
                {
                    line = br.readLine();
                    continue;
                }

                for ( int i = 0; i < ints.length; i++ )
                {
                    Material mat = Material.matchMaterial( ints[i].trim() );
                    if ( mat != null )
                        blocks.add( mat );
                }

                blockGroups.put( split[0].trim(), blocks );
                line = br.readLine();
            }
            br.close();
            return true;
        }
        catch ( IOException e )
        {
            e.printStackTrace();
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

    public String getAnnouncementMessage()
    {
        return announcementMessage;
    }

    public HashMap<String, ArrayList<Material>> getBlockGroups()
    {
        return blockGroups;
    }
}
