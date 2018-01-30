package com.Jessy1237.DwarfCraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrainerGUI
{
    private DwarfCraft plugin;
    private DwarfTrainer trainer;
    private DCPlayer player;
    private Inventory inventory;

    public TrainerGUI( DwarfCraft plugin, DwarfTrainer trainer, DCPlayer player, Inventory inventory )
    {
        this.plugin = plugin;
        this.trainer = trainer;
        this.player = player;
        this.inventory = inventory;
    }

    public void init()
    {
        inventory.clear();

        Skill skill = player.getSkill( trainer.getSkillTrained() );
        List<List<ItemStack>> costs = player.calculateTrainingCost( skill );
        List<ItemStack> trainingCostsToLevel = costs.get( 0 );

        int guiIndex = 0;
        for ( ItemStack costStack : trainingCostsToLevel )
        {
            ArrayList<String> lore = new ArrayList<>();
            lore.add( ChatColor.RED + "" + ( costStack.getAmount() != 0 ? "" + costStack.getAmount() + " needed to level" : "No more is required" ) );
            costStack.setAmount( 1 );
            addItem( null, lore, guiIndex, costStack );
            guiIndex++;
        }

        ItemStack guiItem;
        for ( int i = 0; i < 9; i++ )
        {
            guiItem = new ItemStack( Material.BARRIER );
            addItem( "Cancel", null, 10 + ( i - 1 ), guiItem );
        }

        guiItem = new ItemStack( Material.INK_SACK, 1, ( short ) 12 );
        addItem( "Deposit All", null, 12, guiItem );

        guiItem = new ItemStack( Material.INK_SACK, 1, ( short ) 10 );
        addItem( "Train Skill", null, 13, guiItem );

        guiItem = new ItemStack( Material.INK_SACK, 1, ( short ) 2 );
        addItem( "Train & Deposit Skill", null, 14, guiItem );
    }

    public void openGUI()
    {
        if ( player != null && inventory != null )
        {
            player.getPlayer().openInventory( inventory );
        }
    }

    public DwarfTrainer getTrainer()
    {
        return trainer;
    }

    public DCPlayer getDCPlayer()
    {
        return player;
    }

    public Inventory getInventory()
    {
        return inventory;
    }

    public void updateItem( ItemStack item, int amount )
    {
        for ( ItemStack invStack : inventory.getContents() )
        {
            if ( invStack == null )
                continue;

            if ( invStack.getType().equals( item.getType() ) )
            {
                ArrayList<String> lore = new ArrayList<>();
                lore.add( ChatColor.RED + ( amount != 0 ? "" + amount + " needed to level" : "No more is required" ) );
                ItemMeta meta = invStack.getItemMeta();
                meta.setLore( lore );
                invStack.setItemMeta( meta );
                player.getPlayer().updateInventory();
                return;
            }
        }
    }

    /**
     * Updates the title of the inventory by reopening it with a new title. Inadvertently also updates the item requirements.
     */
    public void updateTitle()
    {
        Skill skill = player.getSkill( trainer.getSkillTrained() );
        player.getPlayer().closeInventory();
        inventory = plugin.getServer().createInventory( player.getPlayer(), 18, plugin.getOut()
                .parseColors( Messages.trainerGUITitle.replaceAll( "%skillid%", "" + skill.getId() ).replaceAll( "%skillname%", "" + skill.getDisplayName() ).replaceAll( "%skilllevel%", "" + skill.getLevel() ).replaceAll( "%maxskilllevel%", "" + plugin.getConfigManager().getMaxSkillLevel() ) ) );
        init();
        player.getPlayer().updateInventory();
        openGUI();
        plugin.getDCInventoryListener().trainerGUIs.put( player.getPlayer(), this );
    }

    /**
     * Adds an Item as a clickable option to the GUI
     * 
     * @param name The display name of the item in the inventory
     * @param lore The lore of the item in the inventory
     * @param guiIndex This the index of the item slot
     * @param item The item to be added as an option to the GUI
     */
    private void addItem( String name, ArrayList<String> lore, int guiIndex, ItemStack item )
    {
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