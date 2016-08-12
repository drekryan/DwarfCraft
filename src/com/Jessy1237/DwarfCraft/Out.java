package com.Jessy1237.DwarfCraft;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
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

    private String consoleLinePrinter(String line, String prefix )
    {
        if (plugin != null) {
            plugin.getServer().getConsoleSender().sendMessage(prefix + line);
        }

        return null;
    }

    public boolean effectInfo( CommandSender sender, DCPlayer dCPlayer, Effect effect )
    {
        sendMessage( sender, effect.describeLevel( dCPlayer ), ChatColor.LIGHT_PURPLE + "[" + ChatColor.DARK_PURPLE + effect.getId() + ChatColor.LIGHT_PURPLE + "] " );
        sendMessage( sender, effect.describeGeneral( dCPlayer ), ChatColor.LIGHT_PURPLE + "[" + ChatColor.DARK_PURPLE + effect.getId() + ChatColor.LIGHT_PURPLE + "] " );
        return true;
    }

    protected void generalInfo( CommandSender sender )
    {
        sendMessage( sender, ChatColor.LIGHT_PURPLE + Messages.GeneralInfo, ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "?" + ChatColor.GOLD + "] " );
    }

    public void help( CommandSender sender )
    {
        sendMessage( sender, Messages.GeneralInfo, "&6[&dHelp&6] " );
    }

    public void info( CommandSender sender )
    {
        sendMessage( sender, Messages.GeneralInfo, "&6[&dInfo&6] " );
    }

    /**
     * Removes carriage returns from strings and passes separate
     *
     * @param message
     * @param prefix
     */
    private void messagePrinter(String message, String prefix )
    {
        String[] lines = message.split( "\\n" );
        String lastColor = "";
        for ( String line : lines )
            lastColor = consoleLinePrinter(lastColor.concat( line ), prefix);
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
            lastColor = playerLinePrinter(player, lastColor.concat( line ), prefix);
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
            } else {
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
        sendMessage( sender, String.format( ChatColor.GOLD + "Skill Info for " + ChatColor.AQUA + "%s " + ChatColor.GOLD + "[" +ChatColor.AQUA + "%d" + ChatColor.GOLD + "] " +
                "|| Your level " + ChatColor.DARK_AQUA + "%d/%d", skill.getDisplayName(), skill.getId(), skill.getLevel(), maxTrainLevel ) );

        // effects lines
        sendMessage( sender, "&6[&5EffectID&6]&f------&6[Effect]&f------" );
        for ( Effect effect : skill.getEffects() )
        {
            if ( effect != null )
                sendMessage( sender, effect.describeLevel( dCPlayer ), String.format( "&6[&5%d&6] ", effect.getId() ) );
        }

        // training lines
        if ( skill.getLevel() == plugin.getConfigManager().getMaxSkillLevel() )
        {
            sendMessage( sender, ChatColor.GOLD + "---This skill is maximum level, no training available---" );
            return true;
        }

        if ( skill.getLevel() > maxTrainLevel )
        {
            sendMessage( sender, ChatColor.GOLD + "---You're as skilled as me, you need a more advanced trainer!--" );
            return true;
        }

        sendMessage( sender, String.format( ChatColor.GOLD + "---Train costs for level " + ChatColor.DARK_AQUA + "%d", ( skill.getLevel() + 1 ) ) );
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
                sendMessage( sender, String.format( ChatColor.DARK_GREEN + " %d of %d %s" + ChatColor.GOLD + " --", deposited, totalCost, plugin.getUtil().getCleanName( r ) ), ChatColor.GOLD + " -- " );
            }

        }
        return true;
    }

    public void printSkillSheet( DCPlayer dCPlayer, CommandSender sender, String displayName, boolean printFull )
    {
        String message1;
        String message2 = "";
        String prefix = "&6[&dSS&6] ";

        message1 = ( ChatColor.GOLD + "Skill Sheet for " + ChatColor.BLUE + ( displayName == null ? dCPlayer.getPlayer().getName() : displayName ) + " " + ChatColor.GOLD + "[" + ChatColor.BLUE +
                dCPlayer.getRace() + " " + ChatColor.GOLD + "- Lvl " + ChatColor.DARK_AQUA + dCPlayer.getDwarfLevel() + ChatColor.GOLD + "]" );
        sendMessage( sender, message1, prefix );

        boolean odd = true;
        String untrainedSkills = "&6Untrained Skills: ";
        for ( Skill s : dCPlayer.getSkills().values() )
        {
            if ( s.getLevel() == 0 )
            {
                untrainedSkills = untrainedSkills.concat( "|" + ChatColor.GRAY + s.getDisplayName() + ChatColor.GOLD + "| " );
                continue;
            }
            odd = !odd;
            // the goal here is for every skill sheet line to be 60 characters
            // long.
            // each skill should take 30 characters - no more, no less
            String interim = String.format( "&6[&3%02d&6] &b%.18s", s.getLevel(), s.getDisplayName() );

            if ( !odd )
            {
                int interimLen = plugin.getUtil().msgLength( interim );
                int numSpaces = ( ( 124 - interimLen ) / 4 ) - 1;
                for ( int i = 0; i < numSpaces; i++ )
                    interim = interim.concat( " " );
                interimLen = 180 - interimLen - numSpaces * 4;
                // 4 possible cases - need 4, 5, 6, or 7
                if ( interimLen == 4 )
                    interim = interim.concat( ChatColor.BLACK + " | " + ChatColor.AQUA );
                else if ( interimLen == 5 )
                    interim = interim.concat( ChatColor.BLACK + " | " + ChatColor.AQUA );
                else if ( interimLen == 6 )
                    interim = interim.concat( ChatColor.BLACK + " | " + ChatColor.AQUA );
                else if ( interimLen == 7 )
                    interim = interim.concat( ChatColor.BLACK + " | " + ChatColor.AQUA );
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
        sendMessage( sender, Messages.ServerRules, "&6[&dRules&6] " );
    }

    /**
     * Used to send messages to all players on a server
     */
    public void sendBroadcast(String message)
    {
        sendBroadcast(message, "");
    }

    /**
     * Used to send messages to all players on a server with a prefix
     */
    protected void sendBroadcast(String message, String prefix )
    {
        if (plugin != null) {
            plugin.getServer().broadcastMessage(prefix + message);
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
            messagePrinter((Player)sender, message, prefix );
        }
        else
        {
            message = stripColors( message );
            prefix = stripColors( prefix );
            messagePrinter(message, prefix );
        }
    }

    /**
     * Dwarf version
     */
    protected void sendMessage( DCPlayer dCPlayer, String message )
    {
        sendMessage( dCPlayer.getPlayer(), message );
    }

    /**
     * Used to send messages to many players
     */
    protected void sendMessage( Player[] playerArray, String message )
    {
        sendMessage( playerArray, message, "" );
    }

    /**
     * Used to send messages to many players with a prefix
     */
    protected void sendMessage( Player[] playerArray, String message, String prefix )
    {
        for ( Player p : playerArray )
            sendMessage( p, message, prefix );
    }

    private String stripColors( String message )
    {
        message = ChatColor.stripColor(message);
        return message;
    }

    /**
     * Finds &0-F in a string and replaces it with the color symbol
     */
    String parseColors( String message )
    {
        return ChatColor.translateAlternateColorCodes('&', message);
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
        switch(page)
        {
            case 1:
                sendMessage( sender, Messages.Fixed.TUTORIAL1.getMessage(), ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "DC" + ChatColor.GOLD + "] " );
                break;
            case 2:
                sendMessage( sender, Messages.Fixed.TUTORIAL2.getMessage(), ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "DC" + ChatColor.GOLD + "] " );
                break;
            case 3:
                sendMessage( sender, Messages.Fixed.TUTORIAL3.getMessage(), ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "DC" + ChatColor.GOLD + "] " );
                break;
            case 4:
                sendMessage( sender, Messages.Fixed.TUTORIAL4.getMessage(), ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "DC" + ChatColor.GOLD + "] " );
                break;
            case 5:
                sendMessage( sender, Messages.Fixed.TUTORIAL5.getMessage(), ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "DC" + ChatColor.GOLD + "] " );
                break;
            case 6:
                sendMessage( sender, Messages.Fixed.TUTORIAL6.getMessage(), ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "DC" + ChatColor.GOLD + "] " );
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
    public void welcome( Server server, DCPlayer dCPlayer )
    {
        try
        {
            if ( plugin.getConfigManager().sendGreeting )
                sendBroadcast(ChatColor.WHITE + "Welcome, " + ChatColor.BLUE + dCPlayer.getRace() + " " + ChatColor.GOLD + dCPlayer.getPlayer().getName(), ChatColor.GOLD + "[DC]         " );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public void race( CommandSender sender, Player player )
    {
        sendMessage( sender, "You are a " + plugin.getDataManager().find( player ).getRace() );
    }

    public void adminRace( CommandSender sender, Player player )
    {
        sendMessage( sender, player.getDisplayName() + " is a " + plugin.getDataManager().find( player ).getRace() );
    }

    public void alreadyRace( CommandSender sender, DCPlayer dCPlayer, String newRace )
    {
        sendMessage( sender, "You are already a " + newRace );
    }

    public void resetRace( CommandSender sender, DCPlayer dCPlayer, String newRace )
    {
        sendMessage( sender, "You are once again a fresh new " + newRace );
    }

    public void changedRace( CommandSender sender, DCPlayer dCPlayer, String newRace )
    {
        sendMessage( sender, "You are now a " + newRace );
    }

    public void confirmRace( CommandSender sender, DCPlayer dCPlayer, String newRace )
    {
        sendMessage( sender, "You need to confirm this command with confirm at the end. (Note: This will reset all your skills)" );
    }

    public void dExistRace( CommandSender sender, DCPlayer dCPlayer, String newRace )
    {
        sendMessage( sender, "The race " + newRace + " doesn't exist." );
    }

}
