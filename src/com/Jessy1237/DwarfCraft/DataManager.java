package com.Jessy1237.DwarfCraft;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;
import com.Jessy1237.DwarfCraft.models.DwarfTrainer;
import com.Jessy1237.DwarfCraft.models.DwarfTrainerTrait;
import com.Jessy1237.DwarfCraft.models.DwarfVehicle;

import net.citizensnpcs.api.npc.AbstractNPC;
import net.citizensnpcs.api.npc.NPC;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */
public class DataManager
{

    private List<DwarfPlayer> dwarves = new ArrayList<DwarfPlayer>();
    public HashMap<Integer, DwarfVehicle> vehicleMap = new HashMap<Integer, DwarfVehicle>();
    public HashMap<Integer, DwarfTrainer> trainerList = new HashMap<Integer, DwarfTrainer>();
    private final ConfigManager configManager;
    private final DwarfCraft plugin;
    private Connection mDBCon;

    protected DataManager( DwarfCraft plugin, ConfigManager cm )
    {
        this.plugin = plugin;
        this.configManager = cm;
    }

    public void addVehicle( DwarfVehicle v )
    {
        vehicleMap.put( v.getVehicle().getEntityId(), v );
    }

    /**
     * this is untested and quite a lot of new code, it will probably fail several times. no way to bugfix currently. Just praying it works
     * 
     * @param oldVersion
     */
    private void buildDB()
    {
        try
        {
            Statement statement = mDBCon.createStatement();
            ResultSet rs = statement.executeQuery( "select * from sqlite_master WHERE name = 'players';" );
            if ( !rs.next() )
            {
                statement.executeUpdate( "create table players ( id INTEGER PRIMARY KEY, uuid, race, raceMaster );" );
            }
            rs.close();

            rs = statement.executeQuery( "select * from sqlite_master WHERE name = 'skills';" );
            if ( !rs.next() )
            {
                statement.executeUpdate( "CREATE TABLE 'skills' " + "  ( " + "    'player' INT, " + "    'id' int, " + "    'level' INT DEFAULT 0, " + "    'deposit1' INT DEFAULT 0, " + "    'deposit2' INT DEFAULT 0, " + "    'deposit3' INT DEFAULT 0, " + "    PRIMARY KEY ('player','id') "
                        + "  );" );
            }
            rs.close();

        }
        catch ( SQLException e )
        {
            plugin.getLogger().log( Level.SEVERE, "DB not built successfully" );
            e.printStackTrace();
            plugin.getServer().getPluginManager().disablePlugin( plugin );
        }
    }

    public boolean checkTrainersInChunk( Chunk chunk )
    {
        for ( Iterator<Map.Entry<Integer, DwarfTrainer>> i = trainerList.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry<Integer, DwarfTrainer> pairs = i.next();
            DwarfTrainer d = ( pairs.getValue() );
            if ( Math.abs( chunk.getX() - d.getLocation().getBlock().getChunk().getX() ) > 1 )
                continue;
            if ( Math.abs( chunk.getZ() - d.getLocation().getBlock().getChunk().getZ() ) > 1 )
                continue;
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
            skill.setDeposit1( 0 );
            skill.setDeposit2( 0 );
            skill.setDeposit3( 0 );
        }

        if ( player != null )
            dwarves.add( newDwarf );
        return newDwarf;
    }

    @SuppressWarnings( "deprecation" )
    protected void dbInitialize()
    {
        try
        {
            Class.forName( "org.sqlite.JDBC" );
            mDBCon = DriverManager.getConnection( "jdbc:sqlite:" + configManager.getDbPath() );
            Statement statement = mDBCon.createStatement();
            ResultSet rs = statement.executeQuery( "select * from sqlite_master WHERE name = 'players';" );
            if ( !rs.next() )
            {
                buildDB();
            }

            // check for update to skill deposits
            try
            {
                rs = statement.executeQuery( "SELECT deposit1 FROM skills;" );
            }
            catch ( SQLException ex )
            {
                statement.executeUpdate( "ALTER TABLE skills ADD COLUMN deposit1 NUMERIC DEFAULT 0;" );
                statement.executeUpdate( "ALTER TABLE skills ADD COLUMN deposit2 NUMERIC DEFAULT 0;" );
                statement.executeUpdate( "ALTER TABLE skills ADD COLUMN deposit3 NUMERIC DEFAULT 0;" );
            }

            // Adds the uuid arg to the player table
            try
            {
                rs = statement.executeQuery( "select uuid from players" );
            }
            catch ( Exception e )
            {
                plugin.getLogger().log( Level.INFO, "Converting Player DB (may lag a little wait for completion message)." );
                mDBCon.setAutoCommit( false );
                HashMap<UUID, String> dcplayers = new HashMap<UUID, String>();
                HashMap<UUID, Integer> ids = new HashMap<UUID, Integer>();

                try
                {
                    PreparedStatement prep = mDBCon.prepareStatement( "SELECT * FROM players" );
                    rs = prep.executeQuery();

                    while ( rs.next() )
                    {
                        dcplayers.put( plugin.getServer().getOfflinePlayer( rs.getString( "name" ) ).getUniqueId(), rs.getString( "race" ) );
                        ids.put( plugin.getServer().getOfflinePlayer( rs.getString( "name" ) ).getUniqueId(), rs.getInt( "id" ) );
                    }

                }
                catch ( Exception e1 )
                {
                    e1.printStackTrace();
                }
                statement.executeUpdate( "DROP TABLE players" );
                statement.executeUpdate( "create table players ( id INTEGER PRIMARY KEY, uuid, race, raceMaster );" );
                for ( UUID uuid : dcplayers.keySet() )
                {
                    if ( uuid != null )
                    {
                        PreparedStatement prep = mDBCon.prepareStatement( "insert into players(id, uuid, race, raceMaster) values(?,?,?);" );
                        prep.setInt( 1, ids.get( uuid ) );
                        prep.setString( 2, uuid.toString() );
                        prep.setString( 3, dcplayers.get( uuid ) );
                        prep.setBoolean( 4, false );
                        prep.execute();
                        prep.close();
                    }
                }
                plugin.getLogger().log( Level.INFO, "Finished Converting the Players DB." );
            }

            // Adds raceMaster arg to the player table
            try
            {
                rs = statement.executeQuery( "select raceMaster from players" );
            }
            catch ( Exception e )
            {
                plugin.getLogger().log( Level.INFO, "Converting Player DB (may lag a little wait for completion message)." );
                mDBCon.setAutoCommit( false );
                HashMap<UUID, String> dcplayers = new HashMap<UUID, String>();
                HashMap<UUID, Integer> ids = new HashMap<UUID, Integer>();

                try
                {
                    PreparedStatement prep = mDBCon.prepareStatement( "SELECT * FROM players" );
                    rs = prep.executeQuery();

                    while ( rs.next() )
                    {
                        dcplayers.put( UUID.fromString( rs.getString( "uuid" ) ), rs.getString( "race" ) );
                        ids.put( UUID.fromString( rs.getString( "uuid" ) ), rs.getInt( "id" ) );
                    }

                }
                catch ( Exception e1 )
                {
                    e1.printStackTrace();
                }
                statement.executeUpdate( "DROP TABLE players" );
                statement.executeUpdate( "create table players ( id INTEGER PRIMARY KEY, uuid, race, raceMaster );" );
                for ( UUID uuid : dcplayers.keySet() )
                {
                    if ( uuid != null )
                    {
                        PreparedStatement prep = mDBCon.prepareStatement( "insert into players(id, uuid, race, raceMaster) values(?,?,?,?);" );
                        prep.setInt( 1, ids.get( uuid ) );
                        prep.setString( 2, uuid.toString() );
                        prep.setString( 3, dcplayers.get( uuid ) );
                        prep.setBoolean( 4, false );
                        prep.execute();
                        prep.close();
                    }
                }
                plugin.getLogger().log( Level.INFO, "Finished Converting the Players DB." );
            }

            try
            {
                rs = statement.executeQuery( "select * from sqlite_master WHERE name = 'trainers';" );
                if ( rs.next() )
                {
                    plugin.getLogger().log( Level.INFO, "Transfering Trainer DB to citizens  (may lag a little wait for completion message)." );

                    rs = statement.executeQuery( "select * from trainers;" );

                    while ( rs.next() )
                    {
                        AbstractNPC npc1;
                        if ( rs.getString( "type" ).equalsIgnoreCase( "PLAYER" ) )
                        {
                            npc1 = ( AbstractNPC ) plugin.getNPCRegistry().createNPC( EntityType.PLAYER, UUID.randomUUID(), Integer.parseInt( rs.getString( "uniqueId" ) ), rs.getString( "name" ) );
                        }
                        else
                        {
                            npc1 = ( AbstractNPC ) plugin.getNPCRegistry().createNPC( EntityType.valueOf( rs.getString( "type" ) ), UUID.randomUUID(), Integer.parseInt( rs.getString( "uniqueId" ) ), rs.getString( "name" ) );
                        }
                        npc1.spawn( new Location( plugin.getServer().getWorld( rs.getString( "world" ) ), rs.getDouble( "x" ), rs.getDouble( "y" ), rs.getDouble( "z" ), rs.getFloat( "yaw" ), rs.getFloat( "pitch" ) ) );
                        npc1.addTrait( new DwarfTrainerTrait( plugin, Integer.parseInt( rs.getString( "uniqueId" ) ), rs.getInt( "skill" ), rs.getInt( "maxSkill" ), rs.getInt( "minSkill" ) ) );
                        npc1.setProtected( true );
                    }
                }
                statement.execute( "DROP TABLE trainers" );
                plugin.getLogger().log( Level.INFO, "Finished Transfering the Trainers DB." );
            }
            catch ( Exception e )
            {

            }
            rs.close();
            mDBCon.setAutoCommit( true );
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    @SuppressWarnings( "unused" )
    private void dbFinalize()
    {
        try
        {
            mDBCon.commit();
            mDBCon.close();
            mDBCon = null;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
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
        if ( checkDwarfData( dCPlayer, uuid ) )
            return dCPlayer;
        else
        {
            // No DwarfPlayer or data found
            return null;
        }
    }

    public void createDwarfData( DwarfPlayer dCPlayer )
    {
        try
        {
            PreparedStatement prep = mDBCon.prepareStatement( "insert into players(uuid, race) values(?,?);" );
            prep.setString( 1, dCPlayer.getPlayer().getUniqueId().toString() );
            prep.setString( 2, plugin.getConfigManager().getDefaultRace().trim() );
            prep.execute();
            prep.close();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public boolean checkDwarfData( DwarfPlayer player )
    {
        return checkDwarfData( player, player.getPlayer().getUniqueId() );
    }

    /**
     * Used for creating and populating a dwarf with a null(off line) player
     * 
     * @param player
     * @param name
     */
    private boolean checkDwarfData( DwarfPlayer player, UUID uuid )
    {
        try
        {
            PreparedStatement prep = mDBCon.prepareStatement( "select * from players WHERE uuid = ?;" );
            prep.setString( 1, uuid.toString() );
            ResultSet rs = prep.executeQuery();

            if ( !rs.next() )
                return false;

            player.setRace( rs.getString( "race" ) );
            player.setRaceMaster( rs.getBoolean( "raceMaster" ) );

            int id = rs.getInt( "id" );
            rs.close();

            prep.close();
            prep = mDBCon.prepareStatement( "select id, level, deposit1, deposit2, deposit3 " + "from skills WHERE player = ?;" );
            prep.setInt( 1, id );
            rs = prep.executeQuery();

            while ( rs.next() )
            {
                int skillID = rs.getInt( "id" );
                int level = rs.getInt( "level" );
                DwarfSkill skill = player.getSkill( skillID );
                if ( skill != null )
                {
                    skill.setLevel( level );
                    skill.setDeposit1( rs.getInt( "deposit1" ) );
                    skill.setDeposit2( rs.getInt( "deposit2" ) );
                    skill.setDeposit3( rs.getInt( "deposit3" ) );
                }
            }
            rs.close();
            prep.close();

            if ( !dwarves.contains( player ) )
                dwarves.add( player );

            return true;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return false;
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
                    System.out.println( "DC5:Removed DwarfVehicle from vehicleList" );
            }
        }
        if ( id != -1 )
            vehicleMap.remove( id );
    }

    private int getPlayerID( UUID uuid )
    {
        try
        {
            PreparedStatement prep = mDBCon.prepareStatement( "select id from players WHERE uuid = ?;" );
            prep.setString( 1, uuid.toString() );
            ResultSet rs = prep.executeQuery();

            if ( !rs.next() )
                return -1;

            int id = rs.getInt( "id" );
            rs.close();
            prep.close();
            return id;
        }
        catch ( Exception e )
        {
            plugin.getLogger().log( Level.WARNING, "Failed to get player ID: " + uuid.toString() );
        }
        return -1;
    }

    public boolean saveDwarfData( DwarfPlayer dwarfPlayer, DwarfSkill[] skills )
    {
        try
        {
            PreparedStatement prep = mDBCon.prepareStatement( "UPDATE players SET race=? WHERE uuid=?;" );
            prep.setString( 1, dwarfPlayer.getRace() );
            prep.setString( 2, dwarfPlayer.getPlayer().getUniqueId().toString() );
            prep.execute();
            prep.close();

            prep = mDBCon.prepareStatement( "UPDATE players SET raceMaster=? WHERE uuid=?;" );
            prep.setBoolean( 1, dwarfPlayer.isRaceMaster() );
            prep.setString( 2, dwarfPlayer.getPlayer().getUniqueId().toString() );
            prep.execute();
            prep.close();

            prep = mDBCon.prepareStatement( "REPLACE INTO skills(player, id, level, " + "deposit1, deposit2, deposit3) " + "values(?,?,?,?,?,?);" );

            int id = getPlayerID( dwarfPlayer.getPlayer().getUniqueId() );
            for ( DwarfSkill skill : skills )
            {
                prep.setInt( 1, id );
                prep.setInt( 2, skill.getId() );
                prep.setInt( 3, skill.getLevel() );
                prep.setInt( 4, skill.getDeposit1() );
                prep.setInt( 5, skill.getDeposit2() );
                prep.setInt( 6, skill.getDeposit3() );
                prep.addBatch();
            }
            prep.executeBatch();
            prep.close();
            return true;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return false;
        }
    }
}
