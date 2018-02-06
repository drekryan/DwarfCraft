package com.Jessy1237.DwarfCraft.commands;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.Jessy1237.DwarfCraft.CommandException;
import com.Jessy1237.DwarfCraft.CommandException.Type;
import com.Jessy1237.DwarfCraft.CommandInformation;
import com.Jessy1237.DwarfCraft.CommandParser;
import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;
import com.Jessy1237.DwarfCraft.models.DwarfTrainerTrait;

import net.citizensnpcs.api.npc.AbstractNPC;

public class CommandCreateTrainer extends Command
{
    private final DwarfCraft plugin;

    public CommandCreateTrainer( final DwarfCraft plugin )
    {
        super( "CreateTrainer" );
        this.plugin = plugin;
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( DwarfCraft.debugMessagesThreshold < 1 )
            System.out.println( "DC1: started command 'createtrainer'" );

        if ( args.length == 0 || args[0].equals( null ) )
        {
            plugin.getOut().sendMessage( sender, CommandInformation.Usage.CREATETRAINER.getUsage() );
        }
        else if ( args[0].equalsIgnoreCase( "?" ) )
        {
            plugin.getOut().sendMessage( sender, CommandInformation.Desc.CREATETRAINER.getDesc() );
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
                npc.spawn( p.getLocation() );
                npc.addTrait( new DwarfTrainerTrait( plugin, uid, skill.getId(), maxSkill, minSkill, false, null ) );
                npc.setProtected( true );
            }
            catch ( CommandException e )
            {
                e.describe( sender );
                sender.sendMessage( CommandInformation.Usage.CREATETRAINER.getUsage() );
            }
        }
        return true;
    }
}
