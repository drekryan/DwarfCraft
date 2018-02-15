package com.Jessy1237.DwarfCraft.commands;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.Messages;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */
public class CommandTutorial extends Command
{
    private final DwarfCraft plugin;

    public CommandTutorial( final DwarfCraft plugin )
    {
        super( "Tutorial" );
        this.plugin = plugin;
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( DwarfCraft.debugMessagesThreshold < 1 )
            System.out.println( "DC1: started command 'tutorial'" );

        final int pageCharLimit = 256; // Page limit is a limitation set by Spigot/Minecraft

        // Ensure the command is being run by an in-game player, otherwise the book cannot be given to them
        if ( sender instanceof Player )
        {

            // Create a new Written Book
            ItemStack book = new ItemStack( Material.WRITTEN_BOOK );

            // Set the BookMeta onto the Written Book
            BookMeta bookMeta = ( BookMeta ) book.getItemMeta();
            bookMeta.setTitle( "Welcome to DwarfCraft" );
            bookMeta.setAuthor( "Jessy1237" );

            for ( String readPage : Messages.tutorial )
            {

                Player player = ( Player ) sender;
                DwarfPlayer dwarfPlayer = plugin.getDataManager().find( player );

                String page = plugin.getOut().parseColors( readPage.replaceAll( "%maxskilllevel%", "" + plugin.getConfigManager().getMaxSkillLevel() ).replaceAll( "%playername%", player.getDisplayName() ).replaceAll( "%playerrace%", dwarfPlayer.getRace() ) );

                if ( isOverPageLimit( bookMeta, sender ) )
                    break;

                if ( page.startsWith( "{" ) || page.startsWith( "[" ) )
                {
                    try
                    {
                        BaseComponent[] comps = ComponentSerializer.parse( page );
                        bookMeta.spigot().addPage( comps );
                    }
                    catch ( Exception e )
                    {
                        plugin.getLogger().log( Level.SEVERE, "Failed to add page to Tutorial Book! Skipping..." );
                        plugin.getLogger().log( Level.SEVERE, "Invalid JSON found at page: " + page );
                    }
                }
                else
                {
                    // Handle text overflowing the page by inserting additional pages
                    String leftOver = page;
                    while ( leftOver != null )
                    {
                        String section = "";
                        if ( leftOver.length() > pageCharLimit )
                        {
                            section = leftOver.substring( 0, pageCharLimit );

                            int index = section.lastIndexOf( ' ' );
                            section = section.substring( 0, index++ );
                            leftOver = leftOver.substring( index, leftOver.length() );
                        }
                        else
                        {
                            section = leftOver;
                            leftOver = null;
                        }

                        bookMeta.addPage( section );
                    }

                    if ( isOverPageLimit( bookMeta, sender ) )
                        break;

                }
            }

            book.setItemMeta( bookMeta );

            // Add Written Book to Players Inventory
            ( ( Player ) sender ).getInventory().addItem( book );
        }
        else
        {
            sender.sendMessage( "Error: This command must be run from in-game" );
        }

        return true;
    }

    private boolean isOverPageLimit( BookMeta bookMeta, CommandSender sender )
    {
        if ( bookMeta.getPageCount() >= 50 )
        {
            plugin.getLogger().log( Level.SEVERE, "The tutorial book cannot support more than 50 pages!" );
            sender.sendMessage( ChatColor.DARK_RED + "The tutorial book reached the maximum page limit of 50. Skipping all additional pages... " );

            return true;
        }

        return false;
    }
}
