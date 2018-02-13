package com.Jessy1237.DwarfCraft.commands;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.Jessy1237.DwarfCraft.CommandInformation.Usage;
import com.Jessy1237.DwarfCraft.CommandParser;
import com.Jessy1237.DwarfCraft.CommandException;
import com.Jessy1237.DwarfCraft.CommandException.Type;
import com.Jessy1237.DwarfCraft.DwarfCraft;
import org.bukkit.entity.Player;

public class CommandTutorial extends Command
{
    private final DwarfCraft plugin;

    public CommandTutorial( final DwarfCraft plugin )
    {
        super( "Tutorial" );
        this.plugin = plugin;
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        try
        {

            CommandParser parser = new CommandParser( plugin, sender, args );
            List<Object> desiredArguments = new ArrayList<Object>();
            List<Object> outputList = null;

            if ( DwarfCraft.debugMessagesThreshold < 1 )
                System.out.println( "DC1: started command 'tutorial'" );
            int page = 0;
            desiredArguments.add( page );

            try
            {
                outputList = parser.parse( desiredArguments, false );
                page = ( Integer ) outputList.get( 0 );
            }
            catch ( CommandException e )
            {
                if ( e.getType() == Type.TOOFEWARGS )
                    page = 1;
                else
                    throw e;
            }

            if ( page < 0 || page > 6 )
                throw new CommandException( plugin, Type.PAGENUMBERNOTFOUND );
            plugin.getOut().tutorial( sender, page );

            if ( sender instanceof Player ) {
                // TODO: This is temporary.. Allow customization again from Messages.config by parsing the messages into the JSON format shown here. See #parsePagesToBookJSON
                String bookCommand = "give " + sender.getName() + " written_book 1 0 {pages:[\"[\\\"\\\",{\\\"text\\\":\\\"Welcome to DwarfCraft!\\\\n\\\\n\\\",\\\"color\\\":\\\"dark_purple\\\",\\\"bold\\\":true}," +
                        "{\\\"text\\\":\\\"You have a set of skills that let you do certain tasks better. \\\",\\\"color\\\":\\\"black\\\",\\\"bold\\\":false},{\\\"text\\\":\\\"When you first start, " +
                        "things may be more difficult than you are used to, but as you level up your skills, you will be much more productive.\\\",\\\"color\\\":\\\"none\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"Your " +
                        "'Skillsheet' lists all the skills that are affecting you.\\\\n\\\\n\\\"},{\\\"text\\\":\\\"Type /dc skillsheet to see your skillsheet.\\\",\\\"color\\\":\\\"red\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"" +
                        "run_command\\\",\\\"value\\\":\\\"/dc skillsheet\\\"}}]\"],title:\"Welcome to DwarfCraft\",author:\"Jessy1237\"}";

                plugin.getServer().dispatchCommand( sender, bookCommand );
            }

            return true;
        }
        catch ( CommandException e )
        {
            e.describe( sender );
            sender.sendMessage( Usage.TUTORIAL.getUsage() );
            return false;
        }
    }

    @SuppressWarnings("unused")
    private void parsePagesToBookJSON(String[] pageStrings) {

    }
}
