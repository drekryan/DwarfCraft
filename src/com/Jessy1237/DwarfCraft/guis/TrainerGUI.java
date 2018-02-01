package com.Jessy1237.DwarfCraft.guis;

import java.util.ArrayList;
import java.util.List;

import com.Jessy1237.DwarfCraft.*;
import com.Jessy1237.DwarfCraft.model.DwarfPlayer;
import com.Jessy1237.DwarfCraft.model.DwarfSkill;
import com.Jessy1237.DwarfCraft.model.DwarfTrainer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrainerGUI extends DwarfGUI
{
    private DwarfTrainer trainer;

    public TrainerGUI(DwarfCraft plugin, DwarfTrainer trainer, DwarfPlayer dwarfPlayer)
    {
        super(plugin, dwarfPlayer);

        this.trainer = trainer;
    }

    @Override
    public void init()
    {
        DwarfSkill skill = dwarfPlayer.getSkill( trainer.getSkillTrained());
        this.inventory = plugin.getServer().createInventory( dwarfPlayer.getPlayer(), 18, plugin.getOut().parseColors( Messages.trainerGUITitle.replaceAll( "%skillid%", "" + skill.getId() ).replaceAll( "%skillname%", "" + skill.getDisplayName() )
                .replaceAll( "%skilllevel%", "" + skill.getLevel() ).replaceAll( "%maxskilllevel%", "" + plugin.getConfigManager().getMaxSkillLevel() ) ) );
        inventory.clear();

        List<List<ItemStack>> costs = dwarfPlayer.calculateTrainingCost( skill );
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
        addItem( "Train DwarfSkill", null, 13, guiItem );

        guiItem = new ItemStack( Material.INK_SACK, 1, ( short ) 2 );
        addItem( "Train & Deposit DwarfSkill", null, 14, guiItem );
    }

    public DwarfTrainer getTrainer()
    {
        return trainer;
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
                dwarfPlayer.getPlayer().updateInventory();
                return;
            }
        }
    }

    /**
     * Updates the title of the inventory by reopening it with a new title. Inadvertently also updates the item requirements.
     */
    public void updateTitle()
    {
        DwarfSkill skill = dwarfPlayer.getSkill( trainer.getSkillTrained() );
        dwarfPlayer.getPlayer().closeInventory();
        inventory = plugin.getServer().createInventory( dwarfPlayer.getPlayer(), 18, plugin.getOut()
                .parseColors( Messages.trainerGUITitle.replaceAll( "%skillid%", "" + skill.getId() ).replaceAll( "%skillname%", "" + skill.getDisplayName() ).replaceAll( "%skilllevel%", "" + skill.getLevel() ).replaceAll( "%maxskilllevel%", "" + plugin.getConfigManager().getMaxSkillLevel() ) ) );
        init();
        dwarfPlayer.getPlayer().updateInventory();
        openGUI();
        plugin.getDwarfInventoryListener().trainerGUIs.put( dwarfPlayer.getPlayer(), this );
    }
}