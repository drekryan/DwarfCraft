package com.Jessy1237.DwarfCraft.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Jessy1237.DwarfCraft.CommandException;
import com.Jessy1237.DwarfCraft.CommandException.Type;
import com.Jessy1237.DwarfCraft.CommandInformation;
import com.Jessy1237.DwarfCraft.CommandParser;
import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.events.DwarfRaceChangeEvent;
import com.Jessy1237.DwarfCraft.guis.RaceGUI;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;

public class CommandRace extends Command
{
    private final DwarfCraft plugin;

    public CommandRace( final DwarfCraft plugin )
    {
        super( "Race" );
        this.plugin = plugin;
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( DwarfCraft.debugMessagesThreshold < 1 )
            System.out.println( "DC1: started command 'race'" );

        try
        {
            if ( args.length == 0 && sender instanceof Player )
            {
                Player p = ( Player ) sender;
                RaceGUI gui = new RaceGUI( plugin, plugin.getDataManager().find( p ) );
                plugin.getDwarfInventoryListener().addDwarfGUI( p, gui );
                return true;
            }
            else if ( args.length == 0 )
            {
                throw new CommandException( plugin, Type.CONSOLECANNOTUSE );
            }
            else if ( args[0].equalsIgnoreCase( "?" ) )
            {
                plugin.getOut().sendMessage( sender, CommandInformation.Desc.RACE.getDesc() );
                return true;
            }
            else if ( !( sender instanceof Player ) || plugin.getPermission().has( sender, "dwarfcraft.op.race" ) )
            {
                CommandParser parser = new CommandParser( plugin, sender, args );
                List<Object> desiredArguments = new ArrayList<Object>();
                List<Object> outputList = null;

                DwarfPlayer dCPlayer = new DwarfPlayer( plugin, null );
                String newRace = "";
                boolean confirm = false;
                desiredArguments.add( dCPlayer );
                desiredArguments.add( "Name" );
                desiredArguments.add( new Boolean( false ) );

                try
                {
                    outputList = parser.parse( desiredArguments, false );
                    dCPlayer = ( DwarfPlayer ) outputList.get( 0 );
                    newRace = ( String ) outputList.get( 1 );
                    confirm = ( ( Boolean ) outputList.get( 2 ) );
                }
                catch ( CommandException dce )
                {

                    if ( dce.getType() == Type.TOOFEWARGS && args.length > 1 )
                    {
                        desiredArguments.remove( 2 );
                        outputList = parser.parse( desiredArguments, true );
                        dCPlayer = ( DwarfPlayer ) outputList.get( 0 );
                        newRace = ( String ) outputList.get( 1 );
                        plugin.getOut().confirmRace( sender, dCPlayer, newRace );
                        return true;
                    }
                    else if ( dce.getType() == Type.TOOFEWARGS )
                    {
                        desiredArguments.remove( 2 );
                        desiredArguments.remove( 1 );
                        outputList = parser.parse( desiredArguments, true );
                        dCPlayer = ( DwarfPlayer ) outputList.get( 0 );
                        plugin.getOut().adminRace( sender, dCPlayer );
                        return true;

                    }
                    else
                        throw dce;
                }

                race( newRace, confirm, dCPlayer, sender );
                return true;
            }
            else
            {
                plugin.getOut().sendMessage( sender, CommandInformation.Usage.RACE.getUsage() );
                return true;
            }

        }
        catch ( CommandException e )
        {
            e.describe( sender );
            sender.sendMessage( CommandInformation.Usage.RACE.getUsage() );
            return false;
        }
    }

    private void race( String newRace, boolean confirm, DwarfPlayer dCPlayer, CommandSender sender )
    {
        if ( dCPlayer.getRace() == newRace )
        {
            plugin.getOut().alreadyRace( sender, dCPlayer, newRace );
        }
        else
        {
            if ( confirm )
            {
                if ( plugin.getConfigManager().getRace( newRace ) != null )
                {
                    DwarfRaceChangeEvent e = new DwarfRaceChangeEvent( dCPlayer, plugin.getConfigManager().getRace( newRace ) );
                    plugin.getServer().getPluginManager().callEvent( e );

                    if ( !e.isCancelled() )
                    {
                        plugin.getOut().changedRace( sender, dCPlayer, plugin.getConfigManager().getRace( e.getRace().getName() ).getName() );
                        dCPlayer.changeRace( e.getRace().getName() );
                    }
                }
            }
            else
            {
                plugin.getOut().confirmRace( sender, dCPlayer, newRace );
            }
        }
    }
}
