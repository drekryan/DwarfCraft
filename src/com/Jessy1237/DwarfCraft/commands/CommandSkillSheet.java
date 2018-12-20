/*
 * Copyright (c) 2018.
 *
 * DwarfCraft is an RPG plugin that allows players to improve their characters
 * skills and capabilities through training, not experience.
 *
 * Authors: Jessy1237 and Drekryan
 * Original Authors: smartaleq, LexManos and RCarretta
 */

package com.Jessy1237.DwarfCraft.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Jessy1237.DwarfCraft.CommandException;
import com.Jessy1237.DwarfCraft.CommandException.Type;
import com.Jessy1237.DwarfCraft.CommandInformation;
import com.Jessy1237.DwarfCraft.CommandParser;
import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.Messages;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;

public class CommandSkillSheet extends Command
{
    private final DwarfCraft plugin;

    public CommandSkillSheet( final DwarfCraft plugin )
    {
        super( "SkillSheet" );
        this.plugin = plugin;
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( DwarfCraft.debugMessagesThreshold < 1 )
            plugin.getUtil().consoleLog( Level.FINE, "DC1: started command 'skillsheet'" );

        try
        {
            if ( args.length == 0 && sender instanceof Player )
            {
                DwarfPlayer dCPlayer = plugin.getDataManager().find( ( Player ) sender );
                if ( dCPlayer.getRace().equalsIgnoreCase( "NULL" ) )
                {
                    plugin.getOut().sendMessage( sender, Messages.chooseARace );
                    return true;
                }

                plugin.getOut().printSkillSheet( dCPlayer, sender, false );
                return true;
            }
            else if ( args.length == 0 )
            {
                throw new CommandException( plugin, Type.CONSOLECANNOTUSE );
            }
            else if ( args[0].equalsIgnoreCase( "?" ) )
            {
                plugin.getOut().sendMessage( sender, CommandInformation.Desc.SKILLSHEET.getDesc() );
                return true;
            }
            else
            {
                CommandParser parser = new CommandParser( plugin, sender, args );
                List<Object> desiredArguments = new ArrayList<>();
                List<Object> outputList;
                boolean printFull = false;

                if ( args[0].equalsIgnoreCase( "-f" ) || args[0].equalsIgnoreCase( "full" ) )
                {
                    printFull = true;
                    desiredArguments.add( args[0] );
                }

                DwarfPlayer dCPlayer = new DwarfPlayer( plugin, null );
                desiredArguments.add( dCPlayer );

                try
                {
                    outputList = parser.parse( desiredArguments, false );
                    if ( outputList.get( 0 ) instanceof String )
                        dCPlayer = ( DwarfPlayer ) outputList.get( 1 );
                    else
                        dCPlayer = ( DwarfPlayer ) outputList.get( 0 );
                }
                catch ( CommandException dce )
                {
                    if ( dce.getType() == Type.PARSEDWARFFAIL || dce.getType() == Type.TOOFEWARGS || dce.getType() == Type.EMPTYPLAYER )
                    {
                        if ( sender instanceof Player )
                        {
                            dCPlayer = plugin.getDataManager().find( ( Player ) sender );
                        }
                        else
                            throw new CommandException( plugin, Type.CONSOLECANNOTUSE );
                    }
                    else
                        throw dce;
                }

                if ( dCPlayer.getRace().equalsIgnoreCase( "NULL" ) )
                {
                    plugin.getOut().sendMessage( sender, Messages.chooseARace );
                    return true;
                }
                plugin.getOut().printSkillSheet( dCPlayer, sender, printFull );
                return true;
            }
        }
        catch ( CommandException e )
        {
            e.describe( sender );
            sender.sendMessage( CommandInformation.Usage.SKILLSHEET.getUsage() );
            return false;
        }
    }
}
