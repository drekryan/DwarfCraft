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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.Jessy1237.DwarfCraft.CommandException;
import com.Jessy1237.DwarfCraft.CommandException.Type;
import com.Jessy1237.DwarfCraft.CommandInformation;
import com.Jessy1237.DwarfCraft.CommandParser;
import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.Messages;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;

public class CommandSkillInfo extends Command implements TabCompleter
{
    private final DwarfCraft plugin;

    public CommandSkillInfo( final DwarfCraft plugin )
    {
        super( "SkillInfo" );
        this.plugin = plugin;
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( DwarfCraft.debugMessagesThreshold < 1 )
            System.out.println( "DC1: started command 'skillinfo'" );

        if ( args.length == 0 || args == null )
        {
            plugin.getOut().sendMessage( sender, CommandInformation.Usage.SKILLINFO.getUsage() );
        }
        else if ( args[0].equalsIgnoreCase( "?" ) )
        {
            plugin.getOut().sendMessage( sender, CommandInformation.Desc.SKILLINFO.getDesc() );
        }
        else
        {
            try
            {
                CommandParser parser = new CommandParser( plugin, sender, args );
                List<Object> desiredArguments = new ArrayList<Object>();
                List<Object> outputList = null;

                DwarfPlayer dwarfPlayer = new DwarfPlayer( plugin, null );
                DwarfSkill skill = new DwarfSkill( 0, null, 0, null, null, null, null, null );
                desiredArguments.add( dwarfPlayer );
                desiredArguments.add( skill );

                try
                {
                    outputList = parser.parse( desiredArguments, false );
                    if ( args.length > outputList.size() )
                        throw new CommandException( plugin, Type.TOOMANYARGS );

                    skill = ( DwarfSkill ) outputList.get( 1 );
                    dwarfPlayer = ( DwarfPlayer ) outputList.get( 0 );
                }
                catch ( CommandException dce )
                {
                    if ( dce.getType() == Type.PARSEDWARFFAIL || dce.getType() == Type.TOOFEWARGS || dce.getType() == Type.EMPTYPLAYER )
                    {
                        if ( !( sender instanceof Player ) )
                            throw new CommandException( plugin, Type.CONSOLECANNOTUSE );
                        
                        desiredArguments.remove( 0 );
                        outputList = parser.parse( desiredArguments, true );
                        skill = ( DwarfSkill ) outputList.get( 0 );
                        dwarfPlayer = plugin.getDataManager().find( ( Player ) sender );

                        if ( dwarfPlayer.getRace().equalsIgnoreCase( "" ) )
                        {
                            plugin.getOut().sendMessage( sender, Messages.chooseARace );
                            return true;
                        }
                    }
                    else
                        throw dce;
                }
                plugin.getOut().printSkillInfo( sender, skill, dwarfPlayer, plugin.getConfigManager().getMaxSkillLevel() );
                return true;
            }
            catch ( CommandException e )
            {
                e.describe( sender );
                sender.sendMessage( CommandInformation.Usage.SKILLINFO.getUsage() );
                return false;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete( CommandSender commandSender, Command command, String s, String[] args )
    {
        if ( !command.getName().equalsIgnoreCase( "dwarfcraft" ) )
            return null;

        if ( args.length == 2 )
        {
            // Gets a list of all possible skill names
            Collection<DwarfSkill> skills = plugin.getConfigManager().getAllSkills().values();
            ArrayList<String> completions = new ArrayList<>();
            ArrayList<String> matches = new ArrayList<>();

            for ( DwarfSkill skill : skills )
            {
                String skillName = skill.getDisplayName().replaceAll( " ", "_" );
                completions.add( skillName.toLowerCase() );
            }

            return StringUtil.copyPartialMatches( args[1], completions, matches );
        }

        return Collections.emptyList();
    }
}