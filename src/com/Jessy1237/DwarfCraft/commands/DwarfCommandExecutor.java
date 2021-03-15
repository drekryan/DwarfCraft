package com.Jessy1237.DwarfCraft.commands;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.models.DwarfCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DwarfCommandExecutor implements CommandExecutor, TabCompleter {

    private final DwarfCraft plugin;

    public DwarfCommandExecutor(final DwarfCraft plugin ) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args ) {
        String name = (args.length == 0) ? "help" : args[0];
        DwarfCommand dwarfCommand = plugin.getCommandManager().getCommand( name );

        if (dwarfCommand != null) {
            if ( dwarfCommand.hasPermission(sender) ) {
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                String newCommandLabel = args[0];
                dwarfCommand.execute(sender, newCommandLabel, newArgs);
                return true;
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do that.");
                return true;
            }
        } else {
            return true;
        }
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if ( !command.getName().equalsIgnoreCase( "dwarfcraft" ) || !plugin.isEnabled() )
            return null;

        List<String> matches = new ArrayList<>();
        if ( args.length <= 1 || args[0].equalsIgnoreCase( "" ) )
        {
            matches = new CommandHelp( plugin, "help" ).onTabComplete( commandSender, command, s, args );
        }
        else if ( plugin.getCommandManager().getCommand( args[0] ) != null)
        {
            DwarfCommand cmd = plugin.getCommandManager().getCommand( args[0].toLowerCase() );
            if (cmd instanceof TabCompleter) {
                matches = ((TabCompleter) cmd).onTabComplete(commandSender, command, s, args);
            }
        }

        return matches;
    }
}
