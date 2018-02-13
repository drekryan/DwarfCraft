package com.Jessy1237.DwarfCraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Jessy1237.DwarfCraft.CommandInformation;
import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.events.DwarfRaceChangeEvent;
import com.Jessy1237.DwarfCraft.guis.RaceGUI;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;

public class CommandRace extends Command
{
    private final DwarfCraft plugin;

    public CommandRace( final DwarfCraft plugin )
    {
        super( "DwarfRace" );
        this.plugin = plugin;
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( DwarfCraft.debugMessagesThreshold < 1 )
            System.out.println( "DC1: started command 'race'" );

        if ( args.length == 0 && sender instanceof Player )
        {
            Player p = ( Player ) sender;
            RaceGUI gui = new RaceGUI( plugin, plugin.getDataManager().find( p ) );
            plugin.getDwarfInventoryListener().addDwarfGUI( p, gui );
        }
        else if ( !( sender instanceof Player ) || plugin.getPermission().has( sender, "dwarfcraft.op.race" ) )
        {
            if ( args.length == 1 )
            {
                Player p = plugin.getServer().getPlayer( args[0] );
                if ( p == null )
                {
                    plugin.getOut().sendMessage( sender, CommandInformation.Usage.RACE.getUsage() );
                }
                else
                {
                    plugin.getOut().adminRace( sender, p );
                }
            }
            else if ( args[0].equalsIgnoreCase( "?" ) )
            {
                plugin.getOut().sendMessage( sender, CommandInformation.Desc.RACE.getDesc() );
            }
            else if ( args.length == 3 )
            {
                String newRace = args[1];

                Player p = plugin.getServer().getPlayer( args[0] );
                DwarfPlayer dCPlayer = null;
                if ( p == null )
                {
                    plugin.getOut().sendMessage( sender, "Not a valid Player Name." );
                    return true;
                }
                else
                {
                    dCPlayer = plugin.getDataManager().find( p );
                }

                if ( plugin.getConfigManager().getRace( newRace ) == null )
                    plugin.getOut().dExistRace( sender, dCPlayer, newRace );

                boolean confirmed = false;
                if ( args[2].equalsIgnoreCase( "confirm" ) )
                {
                    confirmed = true;
                }

                if ( sender instanceof Player )
                {
                    if ( plugin.getPermission().has( sender, "dwarfcraft.op.race" ) )
                    {
                        race( newRace, confirmed, dCPlayer, ( CommandSender ) plugin.getServer().getPlayer( args[0] ) );
                    }
                }
                else
                {
                    race( newRace, confirmed, dCPlayer, sender );
                }
            }
            else if ( args.length == 2 )
            {
                String newRace = args[1];
                Player p = plugin.getServer().getPlayer( args[0] );
                DwarfPlayer dCPlayer = null;
                if ( p == null )
                {
                    plugin.getOut().sendMessage( sender, "Not a valid Player Name." );
                    return true;
                }
                else
                {
                    dCPlayer = plugin.getDataManager().find( p );
                }

                if ( plugin.getConfigManager().getRace( newRace ) == null )
                    plugin.getOut().dExistRace( sender, dCPlayer, newRace );

                boolean confirmed = false;
                if ( sender instanceof Player )
                {
                    if ( plugin.getPermission().has( sender, "dwarfcraft.op.race" ) )
                    {
                        race( newRace, confirmed, dCPlayer, ( CommandSender ) plugin.getServer().getPlayer( args[0] ) );
                    }
                }
                else
                {
                    race( newRace, confirmed, dCPlayer, sender );
                }
            }
            else
            {
                plugin.getOut().sendMessage( sender, CommandInformation.Usage.RACE.getUsage() );
            }
        }
        return true;
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
