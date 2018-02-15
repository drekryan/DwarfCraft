package com.Jessy1237.DwarfCraft.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.Jessy1237.DwarfCraft.CommandException;
import com.Jessy1237.DwarfCraft.CommandException.Type;
import com.Jessy1237.DwarfCraft.CommandInformation;
import com.Jessy1237.DwarfCraft.CommandParser;
import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.models.DwarfEffect;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */
public class CommandEffectInfo extends Command
{
    private final DwarfCraft plugin;

    public CommandEffectInfo( final DwarfCraft plugin )
    {
        super( "EffectInfo" );
        this.plugin = plugin;
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( DwarfCraft.debugMessagesThreshold < 1 )
            System.out.println( "DC1: started command 'effectinfo'" );

        if ( args.length == 0 )
        {
            plugin.getOut().sendMessage( sender, CommandInformation.Usage.EFFECTINFO.getUsage() );
        }
        else if ( args[0].equalsIgnoreCase( "?" ) )
        {
            plugin.getOut().sendMessage( sender, CommandInformation.Desc.EFFECTINFO.getDesc() );
        }
        else
        {
            try
            {
                CommandParser parser = new CommandParser( plugin, sender, args );
                List<Object> desiredArguments = new ArrayList<Object>();
                List<Object> outputList = null;

                DwarfPlayer dCPlayer = new DwarfPlayer( plugin, null );
                DwarfEffect effect = new DwarfEffect( null, plugin );
                desiredArguments.add( dCPlayer );
                desiredArguments.add( effect );
                try
                {
                    outputList = parser.parse( desiredArguments, false );
                    effect = ( DwarfEffect ) outputList.get( 1 );
                    dCPlayer = ( DwarfPlayer ) outputList.get( 0 );
                }
                catch ( CommandException dce )
                {
                    if ( dce.getType() == Type.PARSEDWARFFAIL || dce.getType() == Type.TOOFEWARGS )
                    {
                        desiredArguments.remove( 0 );
                        desiredArguments.add( dCPlayer );
                        outputList = parser.parse( desiredArguments, true );
                        effect = ( DwarfEffect ) outputList.get( 0 );
                        dCPlayer = ( DwarfPlayer ) outputList.get( 1 );
                    }
                    else
                        throw dce;
                }
                plugin.getOut().effectInfo( sender, dCPlayer, effect );
            }
            catch ( CommandException e )
            {
                e.describe( sender );
                sender.sendMessage( CommandInformation.Usage.EFFECTINFO.getUsage() );
                return false;
            }
        }
        return true;
    }
}
