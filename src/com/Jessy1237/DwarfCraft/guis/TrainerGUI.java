package com.Jessy1237.DwarfCraft.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.Messages;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;
import com.Jessy1237.DwarfCraft.models.DwarfTrainer;
import com.Jessy1237.DwarfCraft.schedules.TrainSkillSchedule;

public class TrainerGUI extends DwarfGUI
{
    private DwarfTrainer trainer;

    public TrainerGUI( DwarfCraft plugin, DwarfTrainer trainer, DwarfPlayer dwarfPlayer )
    {
        super( plugin, dwarfPlayer );

        this.trainer = trainer;
    }

    @Override
    public void init()
    {
        DwarfSkill skill = dwarfPlayer.getSkill( trainer.getSkillTrained() );
        this.inventory = plugin.getServer().createInventory( dwarfPlayer.getPlayer(), 18, plugin.getOut().parseColors( plugin.getPlaceHolderParser().parseByDwarfSkill( Messages.trainerGUITitle, skill ) ) );
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
        addItem( "Train Skill", null, 13, guiItem );

        guiItem = new ItemStack( Material.INK_SACK, 1, ( short ) 2 );
        addItem( "Train & Deposit Skill", null, 14, guiItem );
    }

    public void remove()
    {
        trainer.setWait( false );
    }

    public void click( InventoryClickEvent event )
    {
        Player player = ( Player ) event.getWhoClicked();

        if ( event.isLeftClick() && event.getRawSlot() <= 17 )
        {
            if ( event.getCurrentItem() == null )
                return;

            if ( event.getCurrentItem().getType().equals( Material.AIR ) )
                return;

            if ( trainer == null || dwarfPlayer.getPlayer() == null )
            {
                player.closeInventory();
                return;
            }

            long currentTime = System.currentTimeMillis();
            if ( ( currentTime - trainer.getLastTrain() ) < ( long ) ( plugin.getConfigManager().getTrainDelay() * 1000 ) )
            {
                plugin.getOut().sendMessage( event.getWhoClicked(), Messages.trainerCooldown );
            }
            else
            {
                plugin.getServer().getScheduler().scheduleSyncDelayedTask( plugin, new TrainSkillSchedule( plugin, trainer, dwarfPlayer, event.getCurrentItem(), this ), 2 );
            }
        }

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
        this.inventory = plugin.getServer().createInventory( dwarfPlayer.getPlayer(), 18, plugin.getOut().parseColors( plugin.getPlaceHolderParser().parseByDwarfSkill( Messages.trainerGUITitle, skill ) ) );
        init();
        dwarfPlayer.getPlayer().updateInventory();
        openGUI();
        plugin.getDwarfInventoryListener().addDwarfGUI( dwarfPlayer.getPlayer(), this );
    }
}