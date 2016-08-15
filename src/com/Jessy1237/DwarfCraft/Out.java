package com.Jessy1237.DwarfCraft;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Out
{
    /*
     * Messaging Statics
     */
    private final int lineLength = 320; // pixels
    private final int maxLines = 20;
    private final DwarfCraft plugin;

    protected Out( final DwarfCraft plugin )
    {
        this.plugin = plugin;
    }

    private String consoleLinePrinter( String line, String prefix )
    {
        if ( plugin != null )
        {
            plugin.getServer().getConsoleSender().sendMessage( prefix + line );
        }

        return null;
    }

    public boolean effectInfo( CommandSender sender, DCPlayer dCPlayer, Effect effect )
    {
        String prefix = Messages.effectInfoPrefix;
        prefix = prefix.replaceAll( "%effectid", "" + effect.getId() );
        sendMessage( sender, effect.describeLevel( dCPlayer ), prefix );
        sendMessage( sender, effect.describeGeneral( dCPlayer ), prefix );
        return true;
    }

    public void info( CommandSender sender )
    {
        sendMessage( sender, Messages.Fixed.GENERALINFO.getMessage(), "&6[&d?&6]" );
    }

    /**
     * Removes carriage returns from strings and passes separate
     *
     * @param message
     * @param prefix
     */
    private void messagePrinter( String message, String prefix )
    {
        String[] lines = message.split( "\\n" );
        String lastColor = "";
        for ( String line : lines )
            lastColor = consoleLinePrinter( lastColor.concat( line ), prefix );
    }

    /**
     * Removes carriage returns from strings and passes separate
     * 
     * @param player
     * @param message
     * @param prefix
     */
    private void messagePrinter( Player player, String message, String prefix )
    {
        String[] lines = message.split( "\\n" );
        String lastColor = "";
        for ( String line : lines )
            lastColor = playerLinePrinter( player, lastColor.concat( line ), prefix );
    }

    /**
     * Used to parse and send multiple line messages Sends actual output
     * commands
     */
    private String playerLinePrinter( Player player, String message, String prefix )
    {
        int messageSectionLength = lineLength - plugin.getUtil().msgLength( prefix );
        String currentLine = "";
        String words[] = message.split( " " );
        String lastColor = "";
        int lineTotal = 0;

        for ( String word : words )
        {
            if ( plugin.getUtil().msgLength( currentLine ) + plugin.getUtil().msgLength( word ) <= messageSectionLength )
            {
                currentLine = currentLine.concat( word + " " );
            }
            else
            {
                player.sendMessage( prefix.concat( lastColor + currentLine ).trim() );
                lineTotal++;

                if ( lineTotal >= maxLines )
                    return lastColor;

                lastColor = lastColor( lastColor + currentLine );
                currentLine = word + " ";
            }
        }

        player.sendMessage( prefix.concat( lastColor + currentLine ).trim() );
        return lastColor = lastColor( lastColor + currentLine );
    }

    public boolean printSkillInfo( CommandSender sender, Skill skill, DCPlayer dCPlayer, int maxTrainLevel )
    {
        // general line
        sendMessage( sender, Messages.skillInfoHeader.replaceAll( "%playername%", dCPlayer.getPlayer().getDisplayName() ).replaceAll( "%skillid%", "" + skill.getId() ).replaceAll( "%skilllevel5", "" + skill.getLevel() )
                .replaceAll( "%maxskilllevel%", "" + plugin.getConfigManager().getMaxSkillLevel() ) );

        // effects lines
        sendMessage( sender, Messages.skillInfoMinorHeader );
        for ( Effect effect : skill.getEffects() )
        {
            if ( effect != null )
                sendMessage( sender, effect.describeLevel( dCPlayer ), Messages.skillInfoEffectIDPrefix.replaceAll( "%effectid%", "" + effect.getId() ) );
        }

        // training lines
        if ( skill.getLevel() == plugin.getConfigManager().getMaxSkillLevel() )
        {
            sendMessage( sender, Messages.skillInfoMaxSkillLevel );
            return true;
        }

        if ( skill.getLevel() > maxTrainLevel )
        {
            sendMessage( sender, Messages.skillInfoAtTrainerLevel );
            return true;
        }

        sendMessage( sender, Messages.skillInfoTrainCostHeader.replaceAll( "%nextskilllevel%", "" + ( skill.getLevel() + 1 ) ) );
        List<List<ItemStack>> costsTurnins = dCPlayer.calculateTrainingCost( skill );
        List<ItemStack> remaining = costsTurnins.get( 0 );
        List<ItemStack> total = costsTurnins.get( 1 );
        for ( int i = 0; i < remaining.size(); i++ )
        {
            ItemStack r = remaining.get( i );
            ItemStack t = total.get( i );
            if ( r != null && t != null )
            {
                int totalCost = t.getAmount();
                int deposited = t.getAmount() - r.getAmount();
                sendMessage( sender,
                        Messages.skillInfoTrainCost.replaceAll( "%depositedamount%", "" + deposited ).replaceAll( "%totalcost%", "" + totalCost ).replaceAll( "%itemtype%", plugin.getUtil().getCleanName( r ) ) );
            }

        }
        return true;
    }

    public void printSkillSheet( DCPlayer dCPlayer, CommandSender sender, String displayName, boolean printFull )
    {
        String message1;
        String message2 = "";
        String prefix = Messages.skillSheetPrefix;

        message1 = parseSkillSheet( Messages.skillSheetHeader, dCPlayer, sender, displayName, null );
        sendMessage( sender, message1, prefix );

        boolean odd = true;
        String untrainedSkills = Messages.skillSheetUntrainedSkillHeader;
        for ( Skill s : dCPlayer.getSkills().values() )
        {
            if ( s.getLevel() == 0 )
            {
                untrainedSkills = untrainedSkills.concat( parseSkillSheet( Messages.skillSheetUntrainedSkillLine, dCPlayer, sender, displayName, s ) );
                continue;
            }
            odd = !odd;
            // the goal here is for every skill sheet line to be 60 characters
            // long.
            // each skill should take 30 characters - no more, no less
            String interim = parseSkillSheet( Messages.skillSheetSkillLine, dCPlayer, sender, displayName, s );

            if ( !odd )
            {
                int interimLen = plugin.getUtil().msgLength( interim );
                int numSpaces = ( ( 116 - interimLen ) / 4 ) - 1;
                for ( int i = 0; i < numSpaces; i++ )
                    interim = interim.concat( " " );
                interimLen = 116 - interimLen - numSpaces * 4;
                // 4 possible cases - need 4, 5, 6, or 7
                if ( interimLen == 4 )
                    interim = interim.concat( "&0| &b" );
                else if ( interimLen == 5 )
                    interim = interim.concat( "&0| &b" );
                else if ( interimLen == 6 )
                    interim = interim.concat( "&0 | &b" );
                else if ( interimLen == 7 )
                    interim = interim.concat( "&0  | &b" );
            }

            message2 = message2.concat( interim );
            if ( odd )
            {
                sendMessage( sender, message2, prefix );
                message2 = "";
            }

        }
        if ( !message2.equals( "" ) )
            sendMessage( sender, message2, prefix );
        if ( printFull )
            sendMessage( sender, untrainedSkills, prefix );
    }

    public void rules( CommandSender sender )
    {
        sendMessage( sender, Messages.serverRules, Messages.serverRulesPrefix );
    }

    /**
     * Used to send messages to all players on a server
     */
    public void sendBroadcast( String message )
    {
        sendBroadcast( message, "" );
    }

    /**
     * Used to send messages to all players on a server with a prefix
     */
    protected void sendBroadcast( String message, String prefix )
    {
        if ( plugin != null )
        {
            plugin.getServer().broadcastMessage( prefix + message );
        }
    }

    /**
     * Used to send messages to one player or console
     */
    public void sendMessage( CommandSender sender, String message )
    {
        sendMessage( sender, message, "" );
    }

    /**
     * Used to send messages to one player with a prefix
     * 
     * @return
     */
    protected void sendMessage( CommandSender sender, String message, String prefix )
    {
        if ( sender instanceof Player )
        {
            message = parseColors( message );
            prefix = parseColors( prefix );
            messagePrinter( ( Player ) sender, message, prefix );
        }
        else
        {
            message = stripColors( message );
            prefix = stripColors( prefix );
            messagePrinter( message, prefix );
        }
    }

    protected void sendMessage( DCPlayer dCPlayer, String message )
    {
        sendMessage( dCPlayer.getPlayer(), message );
    }

    private String stripColors( String message )
    {
        message = ChatColor.stripColor( message );
        return message;
    }

    /**
     * Finds &0-F in a string and replaces it with the color symbol
     */
    String parseColors( String message )
    {
        return ChatColor.translateAlternateColorCodes( '&', message );
    }

    private String lastColor( String currentLine )
    {
        String lastColor = "";
        int lastIndex = currentLine.lastIndexOf( "§" );
        if ( lastIndex == currentLine.length() )
            return "§";
        if ( lastIndex != -1 )
        {
            lastColor = currentLine.substring( lastIndex, lastIndex + 2 );
        }

        return lastColor;
    }

    public void tutorial( CommandSender sender, int page )
    {
        switch( page )
        {
            case 1:
                sendMessage( sender, Messages.Fixed.TUTORIAL1.getMessage(), Messages.tutorialPrefix );
                break;
            case 2:
                sendMessage( sender, Messages.Fixed.TUTORIAL2.getMessage(), Messages.tutorialPrefix );
                break;
            case 3:
                sendMessage( sender, Messages.Fixed.TUTORIAL3.getMessage(), Messages.tutorialPrefix );
                break;
            case 4:
                sendMessage( sender, Messages.Fixed.TUTORIAL4.getMessage(), Messages.tutorialPrefix );
                break;
            case 5:
                sendMessage( sender, Messages.Fixed.TUTORIAL5.getMessage(), Messages.tutorialPrefix );
                break;
            case 6:
                sendMessage( sender, Messages.Fixed.TUTORIAL6.getMessage(), Messages.tutorialPrefix );
                break;
        }
    }

    /**
     * Sends a welcome message based on race of player joining. Broadcasts to
     * the whole server
     * 
     * @param server
     * @param dCPlayer
     */
    public void welcome( DCPlayer dCPlayer )
    {
        try
        {
            if ( plugin.getConfigManager().sendGreeting )
                sendBroadcast( Messages.welcome.replaceAll( "%playerrace%", dCPlayer.getRace() ).replaceAll( "%playername%", dCPlayer.getPlayer().getDisplayName() ), Messages.welcomePrefix );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public void race( CommandSender sender, Player player )
    {
        sendMessage( sender, parseRace( Messages.raceCheck, plugin.getDataManager().find( player ), null ) );
    }

    public void adminRace( CommandSender sender, Player player )
    {
        sendMessage( sender, parseRace( Messages.adminRaceCheck, plugin.getDataManager().find( player ), null ) );
    }

    public void alreadyRace( CommandSender sender, DCPlayer dCPlayer, String newRace )
    {
        sendMessage( sender, parseRace( Messages.alreadyRace, dCPlayer, newRace ) );
    }

    public void resetRace( CommandSender sender, DCPlayer dCPlayer, String newRace )
    {
        sendMessage( sender, parseRace( Messages.resetRace, dCPlayer, newRace ) );
    }

    public void changedRace( CommandSender sender, DCPlayer dCPlayer, String newRace )
    {
        sendMessage( sender, parseRace( Messages.changedRace, dCPlayer, newRace ) );
    }

    public void confirmRace( CommandSender sender, DCPlayer dCPlayer, String newRace )
    {
        sendMessage( sender, parseRace( Messages.confirmRace, dCPlayer, newRace ) );
    }

    public void dExistRace( CommandSender sender, DCPlayer dCPlayer, String newRace )
    {
        sendMessage( sender, parseRace( Messages.raceDoesNotExist, dCPlayer, newRace ) );
    }

    public String parseRace( String message, DCPlayer dCPlayer, String newRace )
    {
        String out = message;

        out = out.replaceAll( "%playername%", dCPlayer.getPlayer().getDisplayName() );
        out = out.replaceAll( "%playerrace%", dCPlayer.getRace() );
        if ( newRace != null )
            out = out.replaceAll( "%racename%", newRace );

        return out;
    }

    public String parseSkillSheet( String message, DCPlayer dCPlayer, CommandSender sender, String displayName, Skill skill )
    {
        String out = message.replace( "%playername%", ( displayName == null ? dCPlayer.getPlayer().getName() : displayName ) );
        out = out.replaceAll( "%playerrace%", dCPlayer.getRace() );
        out = out.replaceAll( "%playerlevel%", "" + dCPlayer.getDwarfLevel() );
        if ( skill != null )
        {
            out = out.replaceAll( "%skilllevel%", String.format( "%02d", skill.getLevel() ) );
            out = out.replaceAll( "%skillname%", String.format( "%.18s", skill.getDisplayName() ) );
        }
        return out;
    }

    @SuppressWarnings( "deprecation" )
    public String parseEffectLevel( EffectType type, String initiator, String output, double effectAmount, double minorAmount, boolean moreThanOne, String effectLevelColor, String toolType, EntityType creature,
            DCPlayer dCPlayer, ItemStack mInitiator )
    {
        String out = "";

        switch( type )
        {
            case BLOCKDROP:
                out = Messages.describeLevelBlockdrop;
                break;
            case MOBDROP:
                if ( creature != null )
                {
                    out = Messages.describeLevelMobdrop;
                    break;
                }
                out = Messages.describeLevelMobdropNoCreature;
                break;
            case SWORDDURABILITY:
                out = Messages.describeLevelSwordDurability;
                break;
            case PVPDAMAGE:
                out = Messages.describeLevelPVPDamage;
                break;
            case PVEDAMAGE:
                out = Messages.describeLevelPVEDamage;
                break;
            case EXPLOSIONDAMAGE:
                if ( moreThanOne )
                {
                    out = Messages.describeLevelExplosionDamageMore;
                    break;
                }
                else
                {
                    out = Messages.describeLevelExplosionDamageLess;
                    break;
                }
            case FIREDAMAGE:
                if ( moreThanOne )
                {
                    out = Messages.describeLevelFireDamageMore;
                    break;
                }
                else
                {
                    out = Messages.describeLevelFireDamageLess;
                    break;
                }
            case FALLDAMAGE:
                if ( moreThanOne )
                {
                    out = Messages.describeLevelFallingDamageMore;
                    break;
                }
                else
                {
                    out = Messages.describeLevelFallingDamageLess;
                    break;
                }
            case FALLTHRESHOLD:
                out = Messages.describeLevelFallThreshold;
                break;
            case PLOWDURABILITY:
                out = Messages.describeLevelPlowDurability;
                break;
            case TOOLDURABILITY:
                out = Messages.describeLevelToolDurability;
                break;
            case RODDURABILITY:
                out = Messages.describeLevelRodDurability;
                break;
            case EAT:
                out = Messages.describeLevelEat;
                break;
            case CRAFT:
                out = Messages.describeLevelCraft;
                break;
            case PLOW:
                out = Messages.describeLevelPlow;
                break;
            case FISH:
                out = Messages.describeLevelFish;
                break;
            case BREW:
                out = Messages.describeLevelBrew;
                break;
            case DIGTIME:
                out = Messages.describeLevelDigTime;
                break;
            case BOWATTACK:
                out = Messages.describeLevelBowAttack;
                break;
            case VEHICLEDROP:
                out = Messages.describeLevelVehicleDrop;
                break;
            case VEHICLEMOVE:
                out = Messages.describeLevelVehicleMove;
                break;
            case SMELT:
                out = Messages.describeLevelSmelt;
                break;
            case SHEAR:
                out = Messages.describeLevelShear;
                break;
            case SPECIAL:
            default:
                out = "&6This Effect description is not yet implemented: " + type.toString();
        }

        out = out.replaceAll( "%playername%", dCPlayer.getPlayer().getDisplayName() );
        out = out.replaceAll( "%playerrace%", dCPlayer.getRace() );
        out = out.replaceAll( "%playerlevel", "" + dCPlayer.getDwarfLevel() );
        out = out.replaceAll( "%initiator%", initiator );
        out = out.replaceAll( "%effectlevelcolor%", effectLevelColor );
        out = out.replaceAll( "%effectamount%", String.format( "%.2f", effectAmount ) );
        out = out.replaceAll( "%output%", output );
        out = out.replaceAll( "%creaturename%", plugin.getUtil().getCleanName( creature ) );
        out = out.replaceAll( "%tooltype%", toolType );
        out = out.replaceAll( "%effectdamage%", "" + ( int ) ( effectAmount * 100 ) );
        out = out.replaceAll( "%effecttakedamage%", "" + ( int ) ( effectAmount * 100 ) );
        out = out.replaceAll( "%effectamountint%", "" + ( int ) effectAmount );
        out = out.replaceAll( "%effectamountfood%", String.format( "%.2f", ( effectAmount / 2.0 ) ) );
        if ( type == EffectType.CRAFT )
        {
            out = out.replaceAll( "%minoramount%", String.format( "%.0f", minorAmount ) );
        }
        else
        {
            out = out.replaceAll( "%minoramount%", String.format( "%.2f", minorAmount ) );
        }
        out = out.replaceAll( "%effectamountdig%", String.format( "%.0f", +( effectAmount * 100 ) ) );
        out = out.replaceAll( "%effectbowdamage%", String.format( "%.0f", ( effectAmount + 2 ) ) );
        out = out.replaceAll( "%originalfoodlevel%", String.format( "%.2f", ( ( double ) Util.FoodLevel.getLvl( mInitiator.getTypeId() ) ) / 2.0 ) );
        out = out.replaceAll( "%colon%", ":" );

        return out;
    }

}
