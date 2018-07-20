/*
 * Copyright (c) 2018.
 *
 * DwarfCraft is an RPG plugin that allows players to improve their characters
 * skills and capabilities through training, not experience.
 *
 * Authors: Jessy1237 and Drekryan
 * Original Authors: smartaleq, LexManos and RCarretta
 */

package com.Jessy1237.DwarfCraft.guis;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
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
    public int timer;

    public TrainerGUI( DwarfCraft plugin, DwarfTrainer trainer, DwarfPlayer dwarfPlayer )
    {
        super( plugin, dwarfPlayer );

        this.trainer = trainer;
    }

    @Override
    public void init()
    {
        DwarfSkill skill = dwarfPlayer.getSkill( trainer.getSkillTrained() );
        this.inventory = plugin.getServer().createInventory( dwarfPlayer.getPlayer(), 18, plugin.getOut().parseColors( plugin.getPlaceHolderParser().parseByDwarfPlayerAndDwarfSkill( Messages.trainerGUITitle, dwarfPlayer, skill ) ) );
        inventory.clear();

        List<List<ItemStack>> costs = dwarfPlayer.calculateTrainingCost( skill );
        List<ItemStack> trainingCostsToLevel = costs.get( 0 );

        int guiIndex = 0;
        for ( ItemStack costStack : trainingCostsToLevel )
        {
            ArrayList<String> lore = new ArrayList<>();
            lore.add( ChatColor.RED + "" + ( costStack.getAmount() != 0 ? "" + costStack.getAmount() + " needed to level" : "No more is required" ) );

            Tag tag;
            for (int i = 0; i < 3; i++)
            {
                if ( skill.getItem( 1 ).isTag() )
                {
                    tag = skill.getItem( 1 ).getTag();
                    if ( tag.getValues().contains( costStack.getType() ) )
                    {
                        timer = Bukkit.getScheduler().scheduleSyncRepeatingTask( plugin, new CycleSlotTask(dwarfPlayer.getPlayer(), costStack, lore, guiIndex, tag), 1, 15 );
                        guiIndex++;
                        break;
                    }
                }
                else if ( skill.getItem( i ).getItemStack().getType() == costStack.getType() )
                {
                    costStack.setAmount( 1 );
                    addItem( null, lore, guiIndex, costStack );
                    guiIndex++;
                }
            }
        }

        ItemStack guiItem;
        for ( int i = 0; i < 9; i++ )
        {
            guiItem = new ItemStack( Material.BARRIER );
            addItem( "Cancel", null, 10 + ( i - 1 ), guiItem );
        }

        guiItem = new ItemStack( Material.LIGHT_BLUE_DYE, 1 );
        addItem( "Deposit All", null, 12, guiItem );

        guiItem = new ItemStack( Material.LIME_DYE, 1 );
        addItem( "Train Skill", null, 13, guiItem );

        guiItem = new ItemStack( Material.CACTUS_GREEN, 1 );
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

            //Allows for the cancel option to fire without a delay
            if ( event.getCurrentItem().getType().equals( Material.BARRIER ) && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase( "Cancel" ) )
            {
                dwarfPlayer.getPlayer().closeInventory();
            }
            else
            {
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
        this.inventory = plugin.getServer().createInventory( dwarfPlayer.getPlayer(), 18, plugin.getOut().parseColors( plugin.getPlaceHolderParser().parseByDwarfPlayerAndDwarfSkill( Messages.trainerGUITitle, dwarfPlayer, skill ) ) );
        plugin.getDwarfInventoryListener().addDwarfGUI( dwarfPlayer.getPlayer(), this );
    }

    class CycleSlotTask implements Runnable
    {

        private Player player;
        private ItemStack itemStack;
        private ArrayList<String> lore;
        private int index;
        private Tag tag;

        CycleSlotTask( Player player, ItemStack stack, ArrayList<String> lore, int index, Tag tag )
        {
            this.player = player;
            this.itemStack = stack;
            this.lore = lore;
            this.index = index;
            this.tag = tag;
        }

        @Override
        public void run()
        {
            if (player.getOpenInventory().getTopInventory().getType() == InventoryType.CRAFTING)
            {
                Bukkit.getScheduler().cancelTask( timer );
                return;
            }

            int tagIndex = 0;
            for ( Object value : tag.getValues() )
            {
                Material mat = (Material)value;
                if ( mat.equals( itemStack.getType() ) )
                {
                    break;
                }
                tagIndex++;
            }

            int newIndex = tagIndex + 1;
            if ( newIndex == tag.getValues().size())
            {
                newIndex = 0;
            }

            Material newMat = (Material)tag.getValues().toArray()[newIndex];

            this.itemStack = new ItemStack( newMat );
            ItemStack item = new ItemStack( newMat );
            ItemMeta meta = item.getItemMeta();

            if ( lore != null ) meta.setLore( lore );
            meta.addItemFlags( ItemFlag.HIDE_ATTRIBUTES );
            meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
            meta.addItemFlags( ItemFlag.HIDE_DESTROYS );
            meta.addItemFlags( ItemFlag.HIDE_PLACED_ON );
            meta.addItemFlags( ItemFlag.HIDE_POTION_EFFECTS );
            meta.addItemFlags( ItemFlag.HIDE_UNBREAKABLE );
            item.setItemMeta( meta );

            player.getOpenInventory().getTopInventory().setItem( index, item );
            player.updateInventory();
        }

    }
}