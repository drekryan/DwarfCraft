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

import com.jessy1237.dwarfcraft.DwarfCraft;
import com.jessy1237.dwarfcraft.models.DwarfCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class CommandHelp extends DwarfCommand implements TabCompleter
{
    public CommandHelp( final DwarfCraft plugin, String name )
    {
        super( plugin, name );
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( DwarfCraft.debugMessagesThreshold < 1 )
            plugin.getUtil().consoleLog( Level.FINE, "DC1: started command 'dchelp'" );

        Set<String> keys = plugin.getCommandManager().getAllCommands().keySet();
        sender.sendMessage( "Available Commands: " + String.join( ", ", keys ) );
        return true;
    }

    @Override
    public List<String> onTabComplete( CommandSender commandSender, Command command, String s, String[] args )
    {
        if ( !command.getName().equalsIgnoreCase( "dwarfcraft" ) || !plugin.isEnabled() )
            return null;

        Set<String> keys = plugin.getCommandManager().getAllCommands().keySet();
        final List<String> completions = new ArrayList<>(keys);
        List<String> matches = new ArrayList<>();

        StringUtil.copyPartialMatches( args[0], completions, matches );
        Collections.sort( matches );

        return matches;
    }
}
