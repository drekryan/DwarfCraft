/*
 * Copyright (c) 2018.
 *
 * DwarfCraft is an RPG plugin that allows players to improve their characters
 * skills and capabilities through training, not experience.
 *
 * Authors: Jessy1237 and Drekryan
 * Original Authors: smartaleq, LexManos and RCarretta
 */

package com.jessy1237.dwarfcraft.commands;

import java.util.logging.Level;

import com.jessy1237.dwarfcraft.models.DwarfCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jessy1237.dwarfcraft.DwarfCraft;

public class CommandReload extends DwarfCommand
{
    public CommandReload( final DwarfCraft plugin, String name )
    {
        super( plugin, name );
        setDescription("Reloads the DwarfCraft plugin.");
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( args.length > 0 )
        {
            if ( args[0].equalsIgnoreCase( "?" ) )
            {
                plugin.getOut().sendMessage( sender, description );
            }
        }
        else
        {
            if ( DwarfCraft.debugMessagesThreshold < 1 )
                plugin.getUtil().consoleLog( Level.FINE, "DC1: started command 'reload'" );

            if ( sender instanceof Player )
                plugin.getOut().sendMessage( sender, "&aReloading DwarfCraft..." );
            plugin.getUtil().consoleLog( Level.FINE, "Reloading..." );

            plugin.getConfigManager().clearCommands();
            plugin.onDisable();
            plugin.reloadConfig();
            plugin.onEnable( true );

            if ( sender instanceof Player )
                plugin.getOut().sendMessage( sender, "&aReload complete" );
            plugin.getUtil().consoleLog( Level.FINE, "Reload complete" );
        }
        return true;
    }

    @Override
    public String getUsage() {
        return "/dwarfcraft reload";
    }

    @Override
    public boolean isOp() {
        return true;
    }
}