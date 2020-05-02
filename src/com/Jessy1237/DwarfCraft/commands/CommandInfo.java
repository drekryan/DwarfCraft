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

import com.Jessy1237.DwarfCraft.models.DwarfCommand;
import org.bukkit.command.CommandSender;

import com.Jessy1237.DwarfCraft.DwarfCraft;

public class CommandInfo extends DwarfCommand
{

    public CommandInfo( final DwarfCraft plugin, String name )
    {
        super( plugin, name );
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( DwarfCraft.debugMessagesThreshold < 1 )
            plugin.getUtil().consoleLog( Level.FINE, "DC1: started command 'info'" );

        plugin.getOut().info( sender );
        return true;
    }
}
