/*
 * Copyright (c) 2018.
 *
 * DwarfCraft is an RPG plugin that allows players to improve their characters
 * skills and capabilities through training, not experience.
 *
 * Authors: Jessy1237 and Drekryan
 * Original Authors: smartaleq, LexManos and RCarretta
 */

package com.jessy1237.dwarfcraft.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

import com.jessy1237.dwarfcraft.DwarfCraft;
import com.jessy1237.dwarfcraft.models.DwarfPlayer;
import com.jessy1237.dwarfcraft.models.DwarfSkill;
import com.jessy1237.dwarfcraft.models.DwarfTrainer;
import com.jessy1237.dwarfcraft.models.DwarfTrainerTrait;
import com.jessy1237.dwarfcraft.models.DwarfVehicle;

import net.citizensnpcs.api.npc.NPC;

public class DataManager
{
    List<DwarfPlayer> dwarves = new ArrayList<>();
    public HashMap<Integer, DwarfVehicle> vehicleMap = new HashMap<>();
    public HashMap<Integer, DwarfTrainer> trainerList = new HashMap<>();
    private final DwarfCraft plugin;
    private final DBWrapper dbWrapper;
    private final String type;

    public DataManager( final DwarfCraft plugin, final String type )
    {
        this.plugin = plugin;
        this.dbWrapper = DBWrapperFactory.createWrapper( plugin, type );
        this.type = type;
    }

    public void dbInitialize()
    {
        final File database = new File( plugin.getDataFolder(), "dwarfcraft.db" );
        if ( !database.exists() )
        {
            try
            {
                if ( !database.createNewFile() && type.equalsIgnoreCase( "sqlite" ) )
                {
                    plugin.getUtil().consoleLog( Level.SEVERE, "Failed to create database! Disabling..." );
                    plugin.onDisable();
                    return;
                }
            }
            catch ( final IOException e )
            {
                e.printStackTrace();
            }
        }
        dbWrapper.dbInitialize();
    }

    public void dbFinalize()
    {
        dbWrapper.dbFinalize();
    }

    public void createDwarfData( final DwarfPlayer dCPlayer )
    {
        dbWrapper.createDwarfData( dCPlayer );
    }

    public boolean checkDwarfData( final DwarfPlayer player )
    {
        return dbWrapper.checkDwarfData( player );
    }

    public boolean saveDwarfData( final DwarfPlayer dwarfPlayer, final DwarfSkill[] skills )
    {
        return dbWrapper.saveDwarfData( dwarfPlayer, skills );
    }

    public void addVehicle( final DwarfVehicle v )
    {
        vehicleMap.put( v.getVehicle().getEntityId(), v );
    }

    public boolean checkTrainersInChunk( final Chunk chunk )
    {
        for ( final Map.Entry<Integer, DwarfTrainer> pairs : trainerList.entrySet() )
        {
            final DwarfTrainer d = ( pairs.getValue() );
            if ( Math.abs( chunk.getX() - d.getLocation().getBlock().getChunk().getX() ) > 1 )
            {
                continue;
            }
            if ( Math.abs( chunk.getZ() - d.getLocation().getBlock().getChunk().getZ() ) > 1 )
            {
                continue;
            }
            return true;
        }
        return false;
    }

    public DwarfPlayer createDwarf( final Player player )
    {
        final DwarfPlayer newDwarf = new DwarfPlayer( plugin, player );
        newDwarf.setRace( plugin.getRaceManager().getDefaultRace().getId() );
        newDwarf.setSkills( plugin.getSkillManager().getAllSkills() );

        for ( final DwarfSkill skill : newDwarf.getSkills().values() )
        {
            skill.setLevel( 0 );
            skill.setDeposit( 0, 1 );
            skill.setDeposit( 0, 2 );
            skill.setDeposit( 0, 3 );
        }

        if ( player != null )
            dwarves.add( newDwarf );
        return newDwarf;
    }

    /**
     * Finds a DwarfPlayer from the server's static list based on player's name
     * 
     * @param player
     * @return DwarfPlayer or null
     */
    public DwarfPlayer find( final Player player )
    {
        for ( final DwarfPlayer d : dwarves )
        {
            if ( d != null )
            {
                if ( d.getPlayer() != null )
                {
                    if ( d.getPlayer().getUniqueId().equals( player.getUniqueId() ) )
                    {
                        d.setPlayer( player );
                        return d;
                    }
                }
            }
        }
        return null;
    }

    public DwarfPlayer findOffline( final UUID uuid )
    {
        final DwarfPlayer dCPlayer = createDwarf( null );
        if ( dbWrapper.checkDwarfData( dCPlayer, uuid ) )
            return dCPlayer;
        else
        {
            // No DwarfPlayer or data found
            return null;
        }
    }

    public DwarfTrainer getTrainer( final NPC npc )
    {
        if ( !npc.hasTrait( DwarfTrainerTrait.class ) ) return null;
        for ( final Iterator<Map.Entry<Integer, DwarfTrainer>> i = trainerList.entrySet().iterator(); i.hasNext(); )
        {
            final Map.Entry<Integer, DwarfTrainer> pairs = i.next();
            final DwarfTrainer trainer = ( pairs.getValue() );
            if ( trainer.getEntity().getId() == npc.getId() )
                return trainer;
        }
        return null;
    }

    public boolean isTrainer( final Entity entity )
    {
        for ( final Iterator<Map.Entry<Integer, DwarfTrainer>> i = trainerList.entrySet().iterator(); i.hasNext(); )
        {
            final Map.Entry<Integer, DwarfTrainer> pairs = i.next();
            final DwarfTrainer trainer = ( pairs.getValue() );
            if ( trainer.getEntity().getId() == entity.getEntityId() )
                return true;
        }
        return false;
    }

    @SuppressWarnings( "unlikely-arg-type" )
    public DwarfTrainer getTrainer( final String str )
    {
        return ( trainerList.get( str ) ); // can return null
    }

    public DwarfVehicle getVehicle( final Vehicle v )
    {
        for ( final Integer i : vehicleMap.keySet() )
        {
            if ( i == v.getEntityId() )
            {
                return vehicleMap.get( i );
            }
        }
        return null;
    }

    public DwarfTrainer getTrainerByName( final String name )
    {
        for ( final DwarfTrainer trainer : trainerList.values() )
        {
            if ( trainer.getName().equalsIgnoreCase( name ) )
            {
                return trainer;
            }
        }
        return null;
    }

    public void removeVehicle( final Vehicle v )
    {
        int id = -1;
        for ( final Integer i : vehicleMap.keySet() )
        {
            if ( i == v.getEntityId() )
            {
                id = i;
                if ( DwarfCraft.debugMessagesThreshold < 5 )
                    plugin.getUtil().consoleLog( Level.FINE, "DC5:Removed DwarfVehicle from vehicleList" );
            }
        }
        if ( id != -1 )
            vehicleMap.remove( id );
    }
}
