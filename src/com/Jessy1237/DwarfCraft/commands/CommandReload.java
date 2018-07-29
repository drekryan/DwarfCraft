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

import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Jessy1237.DwarfCraft.CommandInformation;
import com.Jessy1237.DwarfCraft.DwarfCraft;

public class CommandReload extends Command
{
    private final DwarfCraft plugin;

    public CommandReload( final DwarfCraft plugin )
    {
        super( "DCReload" );
        this.plugin = plugin;
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( args.length > 0 )
        {
            if ( args[0].equalsIgnoreCase( "?" ) )
            {
                plugin.getOut().sendMessage( sender, CommandInformation.Desc.RELOAD.getDesc() );
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
}