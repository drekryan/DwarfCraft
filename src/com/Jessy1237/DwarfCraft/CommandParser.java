package com.Jessy1237.DwarfCraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Jessy1237.DwarfCraft.CommandException.Type;
import com.Jessy1237.DwarfCraft.models.DwarfEffect;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */
public final class CommandParser
{
    private final DwarfCraft plugin;
    private CommandSender sender;
    private String[] input;
    private DwarfPlayer target = null;

    public CommandParser( final DwarfCraft plugin, CommandSender sender, String[] args )
    {
        this.plugin = plugin;
        this.sender = sender;
        this.input = args;
    }

    public List<Object> parse( List<Object> desiredArguments, boolean ignoreSize ) throws CommandException
    {
        List<Object> output = new ArrayList<>();
        int arrayIterator = 0;
        try
        {
            for ( Object o : desiredArguments )
            {
                if ( o instanceof DwarfPlayer )
                    output.add( parseDwarf( arrayIterator ) );
                else if ( o instanceof Player )
                    output.add( parsePlayer( arrayIterator ) );
                else if ( o instanceof DwarfSkill )
                    output.add( parseSkill( arrayIterator ) );
                else if ( o instanceof DwarfEffect )
                    output.add( parseEffect( arrayIterator ) );
                else if ( o instanceof Boolean )
                    output.add( parseConfirm( arrayIterator ) );
                else if ( o instanceof String && ( ( String ) o ).equalsIgnoreCase( "SkillLevelInt" ) )
                    output.add( parseSkillLevel( arrayIterator ) );
                else if ( o instanceof String && ( ( String ) o ).equalsIgnoreCase( "UniqueIdAdd" ) )
                    output.add( parseUniqueId( arrayIterator, true ) );
                else if ( o instanceof String && ( ( String ) o ).equalsIgnoreCase( "UniqueIdRmv" ) )
                    output.add( parseUniqueId( arrayIterator, false ) );
                else if ( o instanceof String && ( ( String ) o ).equalsIgnoreCase( "Name" ) )
                    output.add( parseName( arrayIterator ) );
                else if ( o instanceof String && ( ( String ) o ).equalsIgnoreCase( "Type" ) )
                    output.add( parseType( arrayIterator ) );
                else if ( o instanceof Integer )
                    output.add( parseInteger( arrayIterator ) );
                else if ( o instanceof String )
                    output.add( o );
                arrayIterator++;
            }
        }
        catch ( ArrayIndexOutOfBoundsException e )
        {
            throw new CommandException( plugin, Type.TOOFEWARGS );
        }
        if ( input.length > output.size() && !ignoreSize )
            throw new CommandException( plugin, Type.TOOMANYARGS );
        if ( input.length < output.size() && !ignoreSize )
            throw new CommandException( plugin, Type.TOOFEWARGS );
        return output;
    }

    private Object parseConfirm( int arrayIterator )
    {
        try
        {
            if ( input[arrayIterator].equalsIgnoreCase( "confirm" ) )
                return true;
        }
        catch ( IndexOutOfBoundsException e )
        {
            return false;
        }
        return false;
    }

    @SuppressWarnings( "deprecation" )
    private DwarfPlayer parseDwarf( int argNumber ) throws CommandException
    {
        Player player;
        DwarfPlayer dCPlayer = null;
        try
        {
            String dwarf = input[argNumber];
            player = sender.getServer().getPlayer( dwarf );
            if ( player != null && player.isOnline() )
                dCPlayer = plugin.getDataManager().find( player );
            else if ( player == null || !player.isOnline() )
            {
                dCPlayer = plugin.getDataManager().findOffline( plugin.getServer().getOfflinePlayer( dwarf ).getUniqueId() );
            }
            if ( dCPlayer == null )
            {
                throw new CommandException( plugin, Type.PARSEDWARFFAIL );
            }
            this.target = dCPlayer;
            return dCPlayer;
        }
        catch ( ArrayIndexOutOfBoundsException e )
        {
            if ( sender instanceof Player )
                throw new CommandException( plugin, Type.TOOFEWARGS );
            else
                throw new CommandException( plugin, Type.CONSOLECANNOTUSE );
        }

    }

    private DwarfEffect parseEffect( int argNumber ) throws CommandException
    {
        String inputString = input[argNumber];
        DwarfEffect effect;
        int effectId;

        try
        {
            effectId = Integer.parseInt( inputString );
            effect = target.getEffect( effectId );
        }
        catch ( NullPointerException npe )
        {
            throw new CommandException( plugin, Type.EMPTYPLAYER );
        }
        catch ( NumberFormatException nfe )
        {
            throw new CommandException( plugin, Type.PARSEEFFECTFAIL );
        }
        if ( effect == null )
            throw new CommandException( plugin, Type.PARSEEFFECTFAIL );
        return effect;
    }

    private Object parseInteger( int argNumber ) throws CommandException
    {
        int i;
        try
        {
            i = Integer.parseInt( input[argNumber] );
        }
        catch ( NumberFormatException nfe )
        {
            throw new CommandException( plugin, Type.PARSEINTFAIL );
        }
        return i;
    }

    private Object parseName( int arrayIterator )
    {
        String name = input[arrayIterator];
        return name;
    }

    private Object parseType( int arrayIterator )
    {
        String type = input[arrayIterator];
        return type;
    }

    private Object parsePlayer( int arrayIterator ) throws CommandException
    {
        Player player = sender.getServer().getPlayer( input[arrayIterator] );
        if ( player == null )
            throw new CommandException( plugin, Type.PARSEPLAYERFAIL );
        return null;
    }

    private DwarfSkill parseSkill( int argNumber ) throws CommandException
    {
        DwarfSkill skill = null;
        String inputString = input[argNumber];
        if ( inputString.equalsIgnoreCase( "all" ) )
            return null;
        int skillID;

        if ( target == null )
            target = plugin.getDataManager().find( ( Player ) sender );
        if ( !( sender instanceof Player ) )
        {
            try
            {
                skillID = Integer.parseInt( inputString );
                return plugin.getConfigManager().getAllSkills().get( skillID );
            }
            catch ( NumberFormatException nfe )
            {
                if ( inputString.length() < 8 )
                    throw new CommandException( plugin, Type.PARSESKILLFAIL );
                for ( int i : plugin.getConfigManager().getAllSkills( target.getRace() ) )
                {
                    DwarfSkill s = plugin.getConfigManager().getGenericSkill( i );
                    if ( s.getDisplayName().regionMatches( 0, inputString, 0, 8 ) )
                        return s;
                }
            }
            throw new CommandException( plugin, Type.PARSESKILLFAIL );
        }
        try
        {
            try
            {
                skillID = Integer.parseInt( inputString );
                skill = target.getSkill( skillID );
            }
            catch ( NumberFormatException nfe )
            {
                skill = target.getSkill( inputString );
            }
        }
        catch ( NullPointerException npe )
        {
            throw new CommandException( plugin, Type.EMPTYPLAYER );
        }
        if ( skill == null )
            throw new CommandException( plugin, Type.PARSESKILLFAIL );
        return skill;
    }

    private int parseSkillLevel( int argNumber ) throws CommandException
    {
        String inputString = input[argNumber];
        int level;
        try
        {
            level = Integer.parseInt( inputString );
        }
        catch ( NumberFormatException nfe )
        {
            throw new CommandException( plugin, Type.PARSELEVELFAIL );
        }
        if ( level > plugin.getConfigManager().getMaxSkillLevel() || level < -1 )
        {
            throw new CommandException( plugin, Type.LEVELOUTOFBOUNDS );
        }
        return level;
    }

    private Object parseUniqueId( int arrayIterator, boolean add ) throws CommandException
    {
        String uniqueId = input[arrayIterator];
        if ( plugin.getDataManager().getTrainer( uniqueId ) != null && add )
            throw new CommandException( plugin, Type.NPCIDINUSE );
        if ( plugin.getDataManager().getTrainer( uniqueId ) == null && !add )
            throw new CommandException( plugin, Type.NPCIDNOTFOUND );
        return uniqueId;
    }

    public void setTarget( DwarfPlayer player )
    {
        this.target = player;
    }
}
