package com.Jessy1237.DwarfCraft.commands;

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
                System.out.println( "DC1: started command 'reload'" );

            if ( sender instanceof Player )
                plugin.getOut().sendMessage( sender, "&aReloading DwarfCraft..." );
            System.out.println( "[DwarfCraft] Reloading..." );

            plugin.onDisable();
            plugin.onEnable( true );

            if ( sender instanceof Player )
                plugin.getOut().sendMessage( sender, "&aReload complete" );
            System.out.println( "[DwarfCraft] Reload complete" );
        }
        return true;
    }
}