package com.Jessy1237.DwarfCraft.commands;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.Jessy1237.DwarfCraft.CommandException;
import com.Jessy1237.DwarfCraft.CommandException.Type;
import com.Jessy1237.DwarfCraft.CommandInformation;
import com.Jessy1237.DwarfCraft.CommandParser;
import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;
import com.Jessy1237.DwarfCraft.models.DwarfTrainer;
import com.Jessy1237.DwarfCraft.models.DwarfTrainerTrait;

import net.citizensnpcs.api.npc.AbstractNPC;

public class CommandCreate extends Command implements TabCompleter
{
    private final DwarfCraft plugin;

    public CommandCreate( final DwarfCraft plugin )
    {
        super( "Create" );
        this.plugin = plugin;
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( DwarfCraft.debugMessagesThreshold < 1 )
            System.out.println( "DC1: started command 'create'" );

        if ( args.length == 0 || args[0].equals( null ) )
        {
            plugin.getOut().sendMessage( sender, CommandInformation.Usage.CREATE.getUsage() );
        }
        else if ( args[0].equalsIgnoreCase( "?" ) )
        {
            plugin.getOut().sendMessage( sender, CommandInformation.Desc.CREATE.getDesc() );
        }
        else
        {
            try
            {
                CommandParser parser = new CommandParser( plugin, sender, args );
                List<Object> desiredArguments = new ArrayList<Object>();
                List<Object> outputList = null;

                String uniqueId = "UniqueIdAdd";
                String name = "Name";
                DwarfSkill skill = new DwarfSkill( 0, null, 0, null, null, null, null, null );
                Integer maxSkill = 1;
                Integer minSkill = 1;
                String type = "Type";
                desiredArguments.add( uniqueId );
                desiredArguments.add( name );
                desiredArguments.add( skill );
                desiredArguments.add( maxSkill );
                desiredArguments.add( minSkill );
                desiredArguments.add( type );
                try
                {
                    if ( !( sender instanceof Player ) )
                        throw new CommandException( plugin, Type.CONSOLECANNOTUSE );
                    outputList = parser.parse( desiredArguments, false );
                    uniqueId = ( String ) outputList.get( 0 );
                    name = ( String ) outputList.get( 1 );
                    skill = ( DwarfSkill ) outputList.get( 2 );
                    maxSkill = ( Integer ) outputList.get( 3 );
                    minSkill = ( Integer ) outputList.get( 4 );
                    type = ( String ) outputList.get( 5 );
                }
                catch ( CommandException e )
                {
                    if ( e.getType() == Type.TOOFEWARGS )
                    {
                        outputList = parser.parse( desiredArguments, true );
                        uniqueId = ( String ) outputList.get( 0 );
                        name = ( String ) outputList.get( 1 );
                        skill = ( DwarfSkill ) outputList.get( 2 );
                        maxSkill = ( Integer ) outputList.get( 3 );
                        minSkill = ( Integer ) outputList.get( 4 );
                        type = ( String ) outputList.get( 5 );
                    }
                    else
                        throw e;
                }

                if ( minSkill == 0 )
                {
                    minSkill = -1;
                }

                Player p = ( Player ) sender;
                int uid = -1;
                try
                {
                    uid = Integer.parseInt( uniqueId );
                }
                catch ( NumberFormatException e )
                {
                    plugin.getOut().sendMessage( sender, "Invalid ID. It must be a numerical value." );
                    return true;
                }

                if ( plugin.getNPCRegistry().getById( uid ) != null )
                {
                    plugin.getOut().sendMessage( sender, "An NPC with that ID already exsists! Try another ID." );
                    return true;
                }
                AbstractNPC npc;
                if ( type.equalsIgnoreCase( "PLAYER" ) )
                {
                    npc = ( AbstractNPC ) plugin.getNPCRegistry().createNPC( EntityType.PLAYER, UUID.randomUUID(), uid, name );
                }
                else
                {
                    if ( EntityType.valueOf( type ) == null )
                        throw new CommandException( plugin, Type.INVALIDENTITYTYPE );

                    npc = ( AbstractNPC ) plugin.getNPCRegistry().createNPC( EntityType.valueOf( type ), UUID.randomUUID(), uid, name );
                }
                npc.addTrait( new DwarfTrainerTrait( plugin, uid, skill.getId(), maxSkill, minSkill ) );
                npc.setProtected( true );
                npc.spawn( p.getLocation() );

                // Don't know why onSpawn doesn't work the first time but works if manually call it
                npc.getTrait( DwarfTrainerTrait.class ).onSpawn();

                // Adding the trainer to DwarfCraft DB
                DwarfTrainer trainer = new DwarfTrainer( plugin, ( AbstractNPC ) npc );
                plugin.getDataManager().trainerList.put( npc.getId(), trainer );
            }
            catch ( CommandException e )
            {
                e.describe( sender );
                sender.sendMessage( CommandInformation.Usage.CREATE.getUsage() );
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete( CommandSender commandSender, Command command, String s, String[] args )
    {
        if ( !command.getName().equalsIgnoreCase( "dwarfcraft" ) )
            return null;

        ArrayList<String> arg = new ArrayList<String>();
        switch ( args.length )
        {
            case 4:
                // Gets a list of all possible skill names
                Collection<DwarfSkill> skills = plugin.getConfigManager().getAllSkills().values();
                ArrayList<String> completions = new ArrayList<>();
                ArrayList<String> matches = new ArrayList<>();

                for ( DwarfSkill skill : skills )
                {
                    String skillName = skill.getDisplayName().replaceAll( " ", "_" );
                    completions.add( skillName );
                }

                return StringUtil.copyPartialMatches( args[3], completions, matches );
            case 5:
                arg.add( "30" );
                return arg;
            case 6:
                arg.add( "0" );
                return arg;
            case 7:
                arg.add( "PLAYER" );
                return arg;
            default:
                arg.add( "" );
                return arg;
        }
    }
}
