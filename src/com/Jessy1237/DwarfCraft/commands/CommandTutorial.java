package com.Jessy1237.DwarfCraft.commands;

import java.util.HashMap;
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

    final int pageCharLimit = 256;

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

        // Ensure the command is being run by an in-game player, otherwise the book cannot be given to them
        if ( sender instanceof Player )
        {
            Player player = (Player) sender;
            if (args.length > 0) {
                if (!plugin.getPermission().has(player, "dwarfcraft.op.tutorial")) {
                    player.sendMessage(ChatColor.DARK_RED + "You don't have permission to spawn the tutorial book for others. Use '/dc tutorial' instead.");
                    return true;
                }

                Player target = plugin.getServer().getPlayer(args[0]);
                if (target == null || !plugin.getServer().getOnlinePlayers().contains(target)) {
                    player.sendMessage(ChatColor.DARK_RED + args[0] + " was not found online. Unable to spawn tutorial book.");
                    return true;
                }

                HashMap<Integer, ItemStack> overflow = target.getInventory().addItem(createTutorialBook(player));
                dropBookIfInventoryFull(player, overflow);
                sender.sendMessage(ChatColor.DARK_GREEN + "Spawned tutorial book for " + target.getName());
                return true;
            }

            // Add Written Book to Players Inventory
            HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(createTutorialBook(player));
            dropBookIfInventoryFull(player, overflow);
        }
        else
        {
            if (args.length > 0) {
                Player target = plugin.getServer().getPlayer(args[0]);
                if (target == null || !plugin.getServer().getOnlinePlayers().contains(target)) {
                    sender.sendMessage(ChatColor.DARK_RED + args[0] + " was not found online. Unable to spawn tutorial book.");
                    return true;
                }

                HashMap<Integer, ItemStack> overflow = target.getInventory().addItem(createTutorialBook(target));
                dropBookIfInventoryFull(target, overflow);
                sender.sendMessage(ChatColor.DARK_GREEN + "Spawned tutorial book for " + target.getName());
                return true;
            }

            sender.sendMessage( "Error: This command must be run from in-game" );
        }

        return true;
    }

    private ItemStack createTutorialBook(Player player) {
        // Create a new Written Book
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

        // Set the BookMeta onto the Written Book
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.setTitle("DwarfCraft Pocket Guide");
        bookMeta.setAuthor("Jessy1237");

        for (String readPage : Messages.tutorial) {
            DwarfPlayer dwarfPlayer = plugin.getDataManager().find(player);

            String page = plugin.getOut().parseColors(plugin.getPlaceHolderParser().parseByDwarfPlayer(readPage, dwarfPlayer));

            if (isOverPageLimit(bookMeta, player))
                break;

            if (page.startsWith("{") || page.startsWith("[")) {
                try {
                    BaseComponent[] comps = ComponentSerializer.parse(page);
                    bookMeta.spigot().addPage(comps);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to add page to Tutorial Book! Skipping...");
                    plugin.getLogger().log(Level.SEVERE, "Invalid JSON found at page: " + page);
                }
            } else {
                // Handle text overflowing the page by inserting additional pages
                String leftOver = page;
                while (leftOver != null) {
                    String section;
                    if (leftOver.length() > pageCharLimit) {
                        section = leftOver.substring(0, pageCharLimit);

                        int index = section.lastIndexOf(' ');
                        section = section.substring(0, index++);
                        leftOver = leftOver.substring(index, leftOver.length());
                    } else {
                        section = leftOver;
                        leftOver = null;
                    }

                    bookMeta.addPage(section);
                }

                if (isOverPageLimit(bookMeta, player))
                    break;

            }
        }

        book.setItemMeta(bookMeta);

        return book;
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

    private void dropBookIfInventoryFull(Player player, HashMap<Integer, ItemStack> overflowStacks) {
        if (!overflowStacks.isEmpty()) {
            if (overflowStacks.get(0).getType() != Material.WRITTEN_BOOK) return;
            player.getWorld().dropItem(player.getLocation(), overflowStacks.get(0));
        }
    }
}
