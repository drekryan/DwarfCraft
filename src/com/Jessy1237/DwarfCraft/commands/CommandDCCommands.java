package com.Jessy1237.DwarfCraft.commands;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.*;

public class CommandDCCommands extends Command implements TabCompleter
{
    // A list of all supported DwarfCraft commands
    private static final String[] COMMANDS = new String[] { "debug", "help", "info", "rules", "tutorial", "commands", "skillsheet", "skillinfo", "effectinfo", "race", "races", "setskill", "creategreeter", "createtrainer", "listtrainers", "dmem" };

    @SuppressWarnings( "unused" )
    private final DwarfCraft plugin;

    public CommandDCCommands( final DwarfCraft plugin )
    {
        super( "DCCommands" );
        this.plugin = plugin;
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( DwarfCraft.debugMessagesThreshold < 1 )
        {
            System.out.println( "DC1: started command 'dchelp'" );
        }

        sender.sendMessage( "DwarfCraft commands: " + String.join( ", ", COMMANDS ) );
        return true;
    }

    @Override
    public List<String> onTabComplete( CommandSender commandSender, Command command, String s, String[] args )
    {
        if ( !command.getName().equalsIgnoreCase( "dwarfcraft" ) )
            return null;

        final List<String> completions = new ArrayList<>( Arrays.asList( COMMANDS ) );
        List<String> matches = new ArrayList<>();

        StringUtil.copyPartialMatches( args[0], completions, matches );
        Collections.sort( matches );

        return matches;
    }
}
