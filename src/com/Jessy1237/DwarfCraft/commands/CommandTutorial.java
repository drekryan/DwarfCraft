package com.Jessy1237.DwarfCraft.commands;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

import com.Jessy1237.DwarfCraft.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class CommandTutorial extends Command
{
    @SuppressWarnings("unused")
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

        if ( sender instanceof Player ) {
            // Create a new Written Book
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

            // Set the Book Pages to the text from the Messages config
            //TODO: Support more than the previously capped 6 Tutorial Pages. Make Tutorial Messages non-fixed and customizable
            String[] bookPages = new String[]{
                    ChatColor.translateAlternateColorCodes( '&', Messages.Fixed.TUTORIAL1.getMessage()),
                    ChatColor.translateAlternateColorCodes( '&', Messages.Fixed.TUTORIAL2.getMessage()),
                    ChatColor.translateAlternateColorCodes( '&', Messages.Fixed.TUTORIAL3.getMessage()),
                    ChatColor.translateAlternateColorCodes( '&', Messages.Fixed.TUTORIAL4.getMessage()),
                    ChatColor.translateAlternateColorCodes( '&', Messages.Fixed.TUTORIAL5.getMessage()),
                    ChatColor.translateAlternateColorCodes( '&', Messages.Fixed.TUTORIAL6.getMessage())
            };

            // Set the Book metadata onto the Written Book
            BookMeta bookMeta = (BookMeta) book.getItemMeta();
            bookMeta.setTitle("Welcome to DwarfCraft");
            bookMeta.setAuthor("Jessy1237");
            bookMeta.addPage( bookPages );
            book.setItemMeta( bookMeta );

            // Add Written Book to Player Inventory
            ((Player) sender).getInventory().addItem(book);
        } else {
            sender.sendMessage( "Error: This command must be run from in-game" );
        }

        return true;
    }
}
