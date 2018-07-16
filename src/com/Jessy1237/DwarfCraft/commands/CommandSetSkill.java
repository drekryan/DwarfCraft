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

import org.bukkit.ChatColor;
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
import com.Jessy1237.DwarfCraft.events.DwarfLevelUpEvent;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;

public class CommandSetSkill extends Command implements TabCompleter
{
    private final DwarfCraft plugin;

    public CommandSetSkill( final DwarfCraft plugin )
    {
        super( "SetSkill" );
        this.plugin = plugin;
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( DwarfCraft.debugMessagesThreshold < 1 )
            System.out.println( "DC1: started command 'setskill'" );

        if ( args.length == 0 )
        {
            plugin.getOut().sendMessage( sender, CommandInformation.Usage.SETSKILL.getUsage() );
        }
        else if ( args[0].equalsIgnoreCase( "?" ) )
        {
            plugin.getOut().sendMessage( sender, CommandInformation.Desc.SETSKILL.getDesc() );
        }
        else
        {
            try
            {
                CommandParser parser = new CommandParser( plugin, sender, args );
                List<Object> desiredArguments = new ArrayList<>();
                List<Object> outputList;

                DwarfPlayer dCPlayer = new DwarfPlayer( plugin, null );
                DwarfSkill skill = new DwarfSkill( 0, null, 0, null, null, null, null, null );
                int level = 0;
                String name;
                desiredArguments.add( dCPlayer );
                desiredArguments.add( skill );
                desiredArguments.add( level );

                try
                {
                    outputList = parser.parse( desiredArguments, false );
                    dCPlayer = ( DwarfPlayer ) outputList.get( 0 );
                    skill = ( DwarfSkill ) outputList.get( 1 );
                    level = ( Integer ) outputList.get( 2 );
                    name = dCPlayer.getPlayer().getName();
                }
                catch ( CommandException e )
                {
                    if ( e.getType() == Type.TOOFEWARGS )
                    {
                        if ( sender instanceof Player )
                        {
                            desiredArguments.remove( 0 );
                            desiredArguments.add( dCPlayer );
                            outputList = parser.parse( desiredArguments, true );
                            dCPlayer = ( DwarfPlayer ) outputList.get( 2 );
                            skill = ( DwarfSkill ) outputList.get( 0 );
                            level = ( Integer ) outputList.get( 1 );
                            name = (sender).getName();
                        }
                        else
                            throw new CommandException( plugin, Type.CONSOLECANNOTUSE );
                    }
                    else
                        throw e;
                }
                if ( skill == null )
                {
                    DwarfSkill[] skills = new DwarfSkill[dCPlayer.getSkills().values().size()];
                    int i = 0;
                    for ( DwarfSkill s : dCPlayer.getSkills().values() )
                    {
                        int oldLevel = s.getLevel();
                        s.setLevel( level );

                        DwarfLevelUpEvent event = new DwarfLevelUpEvent( dCPlayer, null, s );
                        plugin.getServer().getPluginManager().callEvent( event );
                        dCPlayer.runLevelUpCommands( skill );

                        if ( !event.isCancelled() )
                        {
                            s.setDeposit( 0, 1 );
                            s.setDeposit( 0, 2 );
                            s.setDeposit( 0, 3 );
                            skills[i] = s;
                            i++;
                        }
                        else
                        {
                            s.setLevel( oldLevel );
                        }
                    }
                    plugin.getOut().sendMessage(sender, "&eAll skills for player &9" + name + "&e have been set to level &3" + level);
                    plugin.getDataManager().saveDwarfData( dCPlayer, skills );
                }
                else
                {
                    int oldLevel = skill.getLevel();
                    skill.setLevel( level );

                    DwarfLevelUpEvent event = new DwarfLevelUpEvent( dCPlayer, null, skill );
                    plugin.getServer().getPluginManager().callEvent( event );
                    dCPlayer.runLevelUpCommands( skill );

                    if ( !event.isCancelled() )
                    {
                        skill.setDeposit( 0, 1 );
                        skill.setDeposit( 0, 2 );
                        skill.setDeposit( 0, 3 );
                        DwarfSkill[] skills = new DwarfSkill[1];
                        skills[0] = skill;
                        plugin.getOut().sendMessage(sender, "&b" + skill.getDisplayName() + " &eskill for player &9" + name + "&e has been set to level &3" + level);
                        plugin.getDataManager().saveDwarfData( dCPlayer, skills );
                    }
                    else
                    {
                        skill.setLevel( oldLevel );
                    }
                }
            }
            catch ( CommandException e )
            {
                e.describe( sender );
                sender.sendMessage( CommandInformation.Usage.SETSKILL.getUsage() );
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

        ArrayList<String> completions = new ArrayList<>();
        ArrayList<String> matches = new ArrayList<>();
        if ( args.length == 2 )
        {
            completions.clear();
            matches.clear();

            for ( Player player : plugin.getServer().getOnlinePlayers() )
            {
                // Strip Colours from Display Names from silly plugins that add it to the Player Name instead of using prefixes
                completions.add( ChatColor.stripColor( player.getDisplayName() ) );
            }

            return StringUtil.copyPartialMatches( args[1], completions, matches );
        }
        else if ( args.length == 3 )
        {
            completions.clear();
            matches.clear();

            // Gets a list of all possible skill names
            Collection<DwarfSkill> skills = plugin.getConfigManager().getAllSkills().values();

            for ( DwarfSkill skill : skills )
            {
                String skillName = skill.getDisplayName().replaceAll( " ", "_" );
                completions.add( skillName.toLowerCase() );
            }

            return StringUtil.copyPartialMatches( args[2], completions, matches );
        }

        return Collections.emptyList();
    }
}
