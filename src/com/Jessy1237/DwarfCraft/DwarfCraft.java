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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.Jessy1237.DwarfCraft.commands.*;
import com.Jessy1237.DwarfCraft.data.DataManager;
import com.Jessy1237.DwarfCraft.listeners.DwarfBlockListener;
import com.Jessy1237.DwarfCraft.listeners.DwarfEntityListener;
import com.Jessy1237.DwarfCraft.listeners.DwarfInventoryListener;
import com.Jessy1237.DwarfCraft.listeners.DwarfListener;
import com.Jessy1237.DwarfCraft.listeners.DwarfPlayerListener;
import com.Jessy1237.DwarfCraft.listeners.DwarfVehicleListener;
import com.Jessy1237.DwarfCraft.models.DwarfTrainerTrait;

import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.TraitInfo;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class DwarfCraft extends JavaPlugin implements TabCompleter
{

    private final DwarfBlockListener blockListener = new DwarfBlockListener( this );
    private final DwarfPlayerListener playerListener = new DwarfPlayerListener( this );
    private final DwarfEntityListener entityListener = new DwarfEntityListener( this );
    private final DwarfVehicleListener vehicleListener = new DwarfVehicleListener( this );
    private final DwarfInventoryListener inventoryListener = new DwarfInventoryListener( this );
    private final DwarfListener dwarfListener = new DwarfListener( this );
    private NPCRegistry npcr;
    private ConfigManager cm;
    private DataManager dm;
    private Out out;
    private Consumer consumer = null;
    private Util util;
    private PlaceholderParser placeHolderParser;
    private Permission perms = null;
    private Chat chat = null;
    private TraitInfo trainerTrait;
    private HashMap<String, Command> normCommands = new HashMap<>();
    private HashMap<String, Command> opCommands = new HashMap<>();
    public boolean isAuraActive = false;

    public static int debugMessagesThreshold = 10;

    public NPCRegistry getNPCRegistry()
    {
        return npcr;
    }

    public ConfigManager getConfigManager()
    {
        return cm;
    }

    public DataManager getDataManager()
    {
        return dm;
    }

    public Out getOut()
    {
        return out;
    }

    public Consumer getConsumer()
    {
        return consumer;
    }

    public Util getUtil()
    {
        return util;
    }

    public PlaceholderParser getPlaceHolderParser()
    {
        return placeHolderParser;
    }

    public DwarfEntityListener getDwarfEntityListener()
    {
        return entityListener;
    }

    public DwarfInventoryListener getDwarfInventoryListener()
    {
        return inventoryListener;
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration( Permission.class );
        perms = rsp.getProvider();
        return perms != null;
    }

    private boolean isPermissionEnabled()
    {
        return perms != null;
    }

    public Permission getPermission()
    {
        return perms;
    }

    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration( Chat.class );
        chat = rsp.getProvider();
        return chat != null;
    }

    public boolean isChatEnabled()
    {
        return chat != null;
    }

    public Chat getChat()
    {
        return chat;
    }

    public TraitInfo getTrainerTrait()
    {
        return trainerTrait;
    }

    private boolean checkPermission( CommandSender sender, String name, String type )
    {

        if ( perms == null )
            return false;

        if ( sender instanceof Player )
        {
            switch ( type )
            {
                case "op":
                    return perms.has( ( Player ) sender, ( "DwarfCraft.op." + name ).toLowerCase() ) || sender.isOp();
                case "norm":
                    return perms.has( ( Player ) sender, ( "DwarfCraft.norm." + name ).toLowerCase() ) || sender.isOp();
                case "all":
                    return perms.has( ( Player ) sender, "DwarfCraft.*".toLowerCase() ) || sender.isOp();
            }
        }

        return true;
    }

    private Command getSubCommand( String name )
    {
        return ( normCommands.get( name ) == null ? opCommands.get( name ) : normCommands.get( name ) );
    }

    private void initCommands()
    {
        normCommands.put( "skillsheet", new CommandSkillSheet( this ) );
        normCommands.put( "tutorial", new CommandTutorial( this ) );
        normCommands.put( "info", new CommandInfo( this ) );
        normCommands.put( "skillinfo", new CommandSkillInfo( this ) );
        normCommands.put( "race", new CommandRace( this ) );
        normCommands.put( "effectinfo", new CommandEffectInfo( this ) );
        opCommands.put( "debug", new CommandDebug( this ) );
        opCommands.put( "list", new CommandList( this ) );
        opCommands.put( "setskill", new CommandSetSkill( this ) );
        opCommands.put( "create", new CommandCreate( this ) );
        opCommands.put( "reload", new CommandReload( this ) );
    }

    /**
     * Allows companion plugins to add sub commands that require normal permissions
     * 
     * @param name The name of the command
     * @param cmd An instance of the command
     */
    public void addNormCommand( String name, Command cmd )
    {
        normCommands.put( name, cmd );
    }

    /**
     * Allows companion plugins to add sub commands that require admin permissions
     * 
     * @param name The name of the command
     * @param cmd An instance of the command
     */
    public void addOpCommand( String name, Command cmd )
    {
        opCommands.put( name, cmd );
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String commandLabel, String[] args )
    {
        Command cmd;
        String name = command.getName();
        boolean hasNorm = checkPermission( sender, name, "norm" );
        boolean hasOp = checkPermission( sender, name, "op" );
        boolean hasAll = checkPermission( sender, name, "all" );
        boolean isOp = false;
        String[] cArgs = new String[0];

        if ( name.equalsIgnoreCase( "dwarfcraft" ) && this.isEnabled() )
        {
            if ( hasNorm || hasAll )
            {
                if ( args.length == 0 )
                {
                    new CommandHelp( this );
                }
                else
                {
                    // Converts the variables to work with the old command method
                    name = args[0].toLowerCase();
                    cArgs = new String[args.length - 1];
                    for ( int i = 1; i < args.length; i++ )
                    {
                        cArgs[i - 1] = args[i];
                    }
                    hasNorm = checkPermission( sender, name, "norm" );
                    hasOp = checkPermission( sender, name, "op" );
                    hasAll = checkPermission( sender, name, "all" );
                }
            }
            else
            {
                sender.sendMessage( ChatColor.DARK_RED + "You do not have permission to do that." );
                return true;
            }
        }
        else
        {
            return false;
        }

        cmd = normCommands.get( name );
        if ( cmd == null )
        {
            cmd = opCommands.get( name );
            isOp = true;
        }

        if ( cmd == null )
        {
            cmd = new CommandHelp( this );
            return cmd.execute( sender, commandLabel, cArgs );
        }
        else
        {
            if ( ( ( hasNorm || hasAll ) && !isOp ) || ( isOp && ( hasAll || hasOp ) ) )
            {
                return cmd.execute( sender, commandLabel, cArgs );
            }
            else
            {
                sender.sendMessage( ChatColor.DARK_RED + "You do not have permission to do that." );
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete( CommandSender sender, Command command, String alias, String[] args )
    {
        if ( !command.getName().equalsIgnoreCase( "dwarfcraft" ) || !this.isEnabled() )
            return null;

        List<String> matches = new ArrayList<>();

        if ( args.length <= 1 || args[0].equalsIgnoreCase( "" ) )
        {
            matches = new CommandHelp( this ).onTabComplete( sender, command, alias, args );
        }
        else
        {
            Command cmd = getSubCommand( args[0].toLowerCase() );
            if ( cmd != null )
                if ( cmd instanceof TabCompleter )
                    matches = ( ( TabCompleter ) cmd ).onTabComplete( sender, command, alias, args );

        }

        return matches;
    }

    /**
     * Called upon disabling the plugin.
     */
    @Override
    public void onDisable()
    {
        if ( util != null )
            util.removePlayerPrefixes();

        if(dm != null)
        dm.dbFinalize();
    }

    /**
     * Called upon enabling the plugin
     */
    @Override
    public void onEnable()
    {
        onEnable( false );
    }

    public void onEnable( boolean reload )
    {
        PluginManager pm = getServer().getPluginManager();
        util = new Util( this ); //Need to initialise Util earlier if going to use it in the enabling method

        // We are not backwards compatible
        if ( getDescription().getAPIVersion() == null || !Bukkit.getBukkitVersion().startsWith( getDescription().getAPIVersion() ) )
        {
            getUtil().consoleLog( Level.SEVERE, getDescription().getName() + " " + getDescription().getVersion() + " is not compatible with Minecraft " + Bukkit.getBukkitVersion() + ". Please try a different version of DwarfCraft." );
            pm.disablePlugin( this );
            return;
        }

        if ( pm.getPlugin( "Vault" ) == null || !pm.getPlugin( "Vault" ).isEnabled() )
        {
            getUtil().consoleLog( Level.SEVERE, "Something went wrong! Couldn't find Vault!" );
            getUtil().consoleLog( Level.SEVERE, "Disabling DwarfCraft..." );
            pm.disablePlugin( this );
            return;
        }

        try
        {
            if ( setupPermissions() )
                getUtil().consoleLog( Level.INFO, ChatColor.GREEN + "Success! Hooked into a Vault permissions plugin!" );

            if ( setupChat() )
                getUtil().consoleLog( Level.INFO, ChatColor.GREEN + "Success! Hooked into a Vault chat plugin!" );

        }
        catch ( Exception e )
        {
            getUtil().consoleLog( Level.SEVERE, "Something went wrong! Unable to find a permissions plugin." );
            pm.disablePlugin( this );
            return;
        }

        if ( !isPermissionEnabled() )
        {
            getUtil().consoleLog( Level.SEVERE, "Something went wrong! Unable to find a permissions plugin." );
            pm.disablePlugin( this );
            return;
        }

        cm = new ConfigManager( this, getDataFolder().getAbsolutePath() );

        if ( pm.getPlugin( "Citizens" ) == null || !pm.getPlugin( "Citizens" ).isEnabled() )
        {
            getUtil().consoleLog( Level.SEVERE, "Something went wrong! Couldn't find Citizens!" );
            getUtil().consoleLog( Level.SEVERE, "Disabling DwarfCraft..." );
            pm.disablePlugin( this );
            return;
        }
        getUtil().consoleLog( Level.INFO, ChatColor.GREEN + "Success! Hooked into Citizens!" );
        npcr = CitizensAPI.getNPCRegistry();

        dm = new DataManager( this, cm );
        dm.dbInitialize();

        out = new Out( this );

        placeHolderParser = new PlaceholderParser( this );

        pm.registerEvents( playerListener, this );
        pm.registerEvents( entityListener, this );
        pm.registerEvents( blockListener, this );
        pm.registerEvents( vehicleListener, this );
        pm.registerEvents( inventoryListener, this );
        pm.registerEvents( dwarfListener, this );

        // Creates the citizen trait for the DwarfTrainers
        if ( !reload )
        {
            trainerTrait = TraitInfo.create( DwarfTrainerTrait.class ).withName( "DwarfTrainer" );
            CitizensAPI.getTraitFactory().registerTrait( trainerTrait );
        }
        else
        {
            // Untested assumed to work
            util.reloadTrainers();
            this.getConfigManager().clearCommands();
        }

        getUtil().removePlayerPrefixes();

        for ( Player player : getServer().getOnlinePlayers() )
        {
            getUtil().setPlayerPrefix( player );
        }

        if ( pm.getPlugin( "LogBlock" ) != null )
        {
            consumer = ( ( LogBlock ) pm.getPlugin( "LogBlock" ) ).getConsumer();
            getUtil().consoleLog( Level.INFO, ChatColor.GREEN + "Success! Hooked into LogBlock!" );
        }

        if ( pm.getPlugin( "PlaceholderAPI" ) != null )
        {
            PlaceholderParser parser = new PlaceholderParser( this );
            parser.new PlaceholderExpansionHook().register();
            getUtil().consoleLog( Level.INFO, ChatColor.GREEN + "Success! Hooked into PlaceholderAPI!" );
        }

        if ( isEnabled() )
            initCommands();

        getUtil().consoleLog( Level.INFO, ChatColor.GREEN + getDescription().getName() + " " + getDescription().getVersion() + " is enabled!" );

        // Log warning if the build is a Snapshot/Development build
        if ( this.getDescription().getVersion().contains("-SNAPSHOT") )
            getUtil().consoleLog( Level.SEVERE, "*** WARNING: This is a development build. Please keep backups and update frequently. ***" );
    }
}
