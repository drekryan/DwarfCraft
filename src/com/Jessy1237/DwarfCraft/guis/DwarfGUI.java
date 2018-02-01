package com.Jessy1237.DwarfCraft.guis;

import com.Jessy1237.DwarfCraft.DwarfPlayer;
import com.Jessy1237.DwarfCraft.DwarfCraft;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public abstract class DwarfGUI {

    protected DwarfCraft plugin;
    protected DwarfPlayer dwarfPlayer;
    protected Inventory inventory;

    public DwarfGUI(DwarfCraft plugin, DwarfPlayer player)
    {
        this.plugin = plugin;
        this.dwarfPlayer = player;
    }

    public abstract void init();

    public void openGUI()
    {
        if (dwarfPlayer != null && inventory != null)
        {
            dwarfPlayer.getPlayer().openInventory(inventory);
        }
    }

    public DwarfPlayer getDwarfPlayer()
    {
        return dwarfPlayer;
    }

    public Inventory getInventory()
    {
        return inventory;
    }

    /**
     * Adds an Item as a clickable option to the GUI
     *
     * @param name The display name of the item in the inventory
     * @param lore The lore of the item in the inventory
     * @param guiIndex This the index of the item slot
     * @param item The item to be added as an option to the GUI
     */
    protected void addItem(String name, ArrayList<String> lore, int guiIndex, ItemStack item)
    {
        if (inventory == null)
            return;

        ItemMeta meta = item.getItemMeta();
        if ( lore != null )
            meta.setLore( lore );
        if ( name != null )
            meta.setDisplayName( name );
        meta.addItemFlags( ItemFlag.HIDE_ATTRIBUTES );
        meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
        meta.addItemFlags( ItemFlag.HIDE_DESTROYS );
        meta.addItemFlags( ItemFlag.HIDE_PLACED_ON );
        meta.addItemFlags( ItemFlag.HIDE_POTION_EFFECTS );
        meta.addItemFlags( ItemFlag.HIDE_UNBREAKABLE );

        item.setItemMeta( meta );

        inventory.setItem( guiIndex, item );
    }
}

