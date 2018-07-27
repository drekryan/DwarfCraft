/*
 * Copyright (c) 2018.
 *
 * DwarfCraft is an RPG plugin that allows players to improve their characters
 * skills and capabilities through training, not experience.
 *
 * Authors: Jessy1237 and Drekryan
 * Original Authors: smartaleq, LexManos and RCarretta
 */

package com.Jessy1237.DwarfCraft;

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

import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;
import com.Jessy1237.DwarfCraft.models.DwarfTrainer;
import com.Jessy1237.DwarfCraft.models.DwarfVehicle;

import net.citizensnpcs.api.npc.NPC;

public class DataManager
{

    List<DwarfPlayer> dwarves = new ArrayList<>();
    public HashMap<Integer, DwarfVehicle> vehicleMap = new HashMap<>();
    public HashMap<Integer, DwarfTrainer> trainerList = new HashMap<>();
    private final ConfigManager configManager;
    private final DwarfCraft plugin;
    private final DBWrapperSQLite sqlLiteWrapper;
    private final DBWrapperMySQL mySQLWrapper;
    private boolean useMySQL;

    protected DataManager( DwarfCraft plugin, ConfigManager cm )
    {
        this.plugin = plugin;
        this.configManager = cm;

        sqlLiteWrapper = new DBWrapperSQLite( plugin, cm );
        mySQLWrapper = new DBWrapperMySQL( plugin, cm );

        this.useMySQL = configManager.useMySQL;
    }

    void dbInitialize()
    {
        if ( useMySQL )
            mySQLWrapper.dbInitialize();
        else
            sqlLiteWrapper.dbInitialize();
    }

    void dbFinalize()
    {
        if ( useMySQL )
            mySQLWrapper.dbFinalize();
        else
            sqlLiteWrapper.dbFinalize();
    }

    public void createDwarfData( DwarfPlayer dCPlayer )
    {
        if ( useMySQL )
            mySQLWrapper.createDwarfData( dCPlayer );
        else
            sqlLiteWrapper.createDwarfData( dCPlayer );
    }

    public boolean checkDwarfData( DwarfPlayer player )
    {
        if ( useMySQL )
            return mySQLWrapper.checkDwarfData( player );
        else
            return sqlLiteWrapper.checkDwarfData( player );
    }

    public boolean saveDwarfData( DwarfPlayer dwarfPlayer, DwarfSkill[] skills )
    {
        if ( useMySQL )
            return mySQLWrapper.saveDwarfData( dwarfPlayer, skills );
        else
            return sqlLiteWrapper.saveDwarfData( dwarfPlayer, skills );
    }

    public void addVehicle( DwarfVehicle v )
    {
        vehicleMap.put( v.getVehicle().getEntityId(), v );
    }


    public boolean checkTrainersInChunk( Chunk chunk )
    {
        for ( Map.Entry<Integer, DwarfTrainer> pairs : trainerList.entrySet() )
        {
            DwarfTrainer d = ( pairs.getValue() );
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

    public DwarfPlayer createDwarf( Player player )
    {
        DwarfPlayer newDwarf = new DwarfPlayer( plugin, player );
        newDwarf.setRace( plugin.getConfigManager().getDefaultRace() );
        newDwarf.setSkills( plugin.getConfigManager().getAllSkills() );

        for ( DwarfSkill skill : newDwarf.getSkills().values() )
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
    public DwarfPlayer find( Player player )
    {
        for ( DwarfPlayer d : dwarves )
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

    protected DwarfPlayer findOffline( UUID uuid )
    {
        DwarfPlayer dCPlayer = createDwarf( null );
        if ( sqlLiteWrapper.checkDwarfData( dCPlayer, uuid ) )
            return dCPlayer;
        else
        {
            // No DwarfPlayer or data found
            return null;
        }
    }

    public DwarfTrainer getTrainer( NPC npc )
    {
        for ( Iterator<Map.Entry<Integer, DwarfTrainer>> i = trainerList.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry<Integer, DwarfTrainer> pairs = i.next();
            DwarfTrainer trainer = ( pairs.getValue() );
            if ( trainer.getEntity().getId() == npc.getId() )
                return trainer;
        }
        return null;
    }

    public boolean isTrainer( Entity entity )
    {
        for ( Iterator<Map.Entry<Integer, DwarfTrainer>> i = trainerList.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry<Integer, DwarfTrainer> pairs = i.next();
            DwarfTrainer trainer = ( pairs.getValue() );
            if ( trainer.getEntity().getId() == entity.getEntityId() )
                return true;
        }
        return false;
    }

    @SuppressWarnings( "unlikely-arg-type" )
    protected DwarfTrainer getTrainer( String str )
    {
        return ( trainerList.get( str ) ); // can return null
    }

    public DwarfVehicle getVehicle( Vehicle v )
    {
        for ( Integer i : vehicleMap.keySet() )
        {
            if ( i == v.getEntityId() )
            {
                return vehicleMap.get( i );
            }
        }
        return null;
    }

    public DwarfTrainer getTrainerByName( String name )
    {
        for ( DwarfTrainer trainer : trainerList.values() )
        {
            if ( trainer.getName().equalsIgnoreCase( name ) )
            {
                return trainer;
            }
        }
        return null;
    }

    public void removeVehicle( Vehicle v )
    {
        int id = -1;
        for ( Integer i : vehicleMap.keySet() )
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
