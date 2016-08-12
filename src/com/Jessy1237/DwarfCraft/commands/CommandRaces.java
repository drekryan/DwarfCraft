package com.Jessy1237.DwarfCraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.Race;

/**
 * Original Authors: Jessy1237
 */

public class CommandRaces extends Command
{

    private DwarfCraft plugin;

    public CommandRaces( final DwarfCraft plugin )
    {
        super( "Races" );
        this.plugin = plugin;
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        plugin.getOut().sendMessage( sender, "&7Races:&f" );
        for ( Race r : plugin.getConfigManager().getRaceList() )
        {
            if ( r != null )
            {
                plugin.getOut().sendMessage( sender, "\n&b" + r.getName() + ":&f " + r.getDesc() );
            }
        }
        return true;
    }

}
