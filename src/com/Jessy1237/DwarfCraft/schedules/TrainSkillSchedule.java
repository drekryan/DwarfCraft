package com.Jessy1237.DwarfCraft.schedules;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.Messages;
import com.Jessy1237.DwarfCraft.PlaceHolderParser.PlaceHolder;
import com.Jessy1237.DwarfCraft.guis.TrainerGUI;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;
import com.Jessy1237.DwarfCraft.models.DwarfTrainer;

public class TrainSkillSchedule implements Runnable
{

    private DwarfCraft plugin;
    private final DwarfTrainer trainer;
    private final DwarfPlayer dCPlayer;
    private final ItemStack clickedItem;
    private final TrainerGUI trainerGUI;

    public TrainSkillSchedule( DwarfCraft plugin, DwarfTrainer trainer, DwarfPlayer dCPlayer, ItemStack clickedItem, TrainerGUI trainerGUI )
    {
        this.plugin = plugin;
        this.trainer = trainer;
        this.dCPlayer = dCPlayer;
        this.clickedItem = clickedItem;
        this.trainerGUI = trainerGUI;
    }

    @Override
    public void run()
    {

        DwarfSkill skill = dCPlayer.getSkill( trainer.getSkillTrained() );
        String tag = Messages.trainSkillPrefix.replaceAll( "%skillid%", "" + skill.getId() );

        if ( clickedItem.getType().equals( Material.INK_SACK ) )
        {
            // Checks if after a level up if any of the limitting constraints have changed. i.e. player may have levelled up past the trainers ability while the inventory was open
            if ( skill.getLevel() >= plugin.getConfigManager().getRaceLevelLimit() && !plugin.getConfigManager().getAllSkills( dCPlayer.getRace() ).contains( skill.getId() ) )
            {
                plugin.getOut().sendMessage( dCPlayer.getPlayer(), Messages.raceDoesNotSpecialize.replaceAll( PlaceHolder.RACE_LEVEL_LIMIT.getPlaceHolder(), "" + plugin.getConfigManager().getRaceLevelLimit() ), tag );
                dCPlayer.getPlayer().closeInventory();
                return;
            }

            if ( skill.getLevel() >= plugin.getConfigManager().getMaxSkillLevel() )
            {
                plugin.getOut().sendMessage( dCPlayer.getPlayer(), Messages.maxSkillLevel.replaceAll( PlaceHolder.MAX_SKILL_LEVEL.getPlaceHolder(), "" + plugin.getConfigManager().getMaxSkillLevel() ), tag );
                dCPlayer.getPlayer().closeInventory();
                return;
            }

            if ( skill.getLevel() >= trainer.getMaxSkill() )
            {
                plugin.getOut().sendMessage( dCPlayer.getPlayer(), Messages.trainerMaxLevel, tag );
                dCPlayer.getPlayer().closeInventory();
                return;
            }

            if ( clickedItem.getDurability() == 12 && clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase( "Deposit All" ) )
            {
                trainer.depositAll( dCPlayer, clickedItem, trainerGUI );
            }
            else if ( clickedItem.getDurability() == 10 && clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase( "Train Skill" ) )
            {
                trainer.trainSkill( dCPlayer, clickedItem, trainerGUI );
            }
            else if ( clickedItem.getDurability() == 2 && clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase( "Train & Deposit Skill" ) )
            {
                if ( trainer.trainSkill( dCPlayer, clickedItem, trainerGUI ) )
                {
                    trainer.depositAll( dCPlayer, clickedItem, trainerGUI );
                }
            }
        }
        else if ( clickedItem.getType().equals( Material.BARRIER ) && clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase( "Cancel" ) )
        {
            dCPlayer.getPlayer().closeInventory();
        }
        else
        {

            // Checks if after a level up if any of the limitting constraints have changed. i.e. player may have levelled up past the trainers ability while the inventory was open
            if ( skill.getLevel() >= plugin.getConfigManager().getRaceLevelLimit() && !plugin.getConfigManager().getAllSkills( dCPlayer.getRace() ).contains( skill.getId() ) )
            {
                plugin.getOut().sendMessage( dCPlayer.getPlayer(), Messages.raceDoesNotSpecialize.replaceAll( PlaceHolder.RACE_LEVEL_LIMIT.getPlaceHolder(), "" + plugin.getConfigManager().getRaceLevelLimit() ), tag );
                dCPlayer.getPlayer().closeInventory();
                return;
            }

            if ( skill.getLevel() >= plugin.getConfigManager().getMaxSkillLevel() )
            {
                plugin.getOut().sendMessage( dCPlayer.getPlayer(), Messages.maxSkillLevel.replaceAll( PlaceHolder.MAX_SKILL_LEVEL.getPlaceHolder(), "" + plugin.getConfigManager().getMaxSkillLevel() ), tag );
                dCPlayer.getPlayer().closeInventory();
                return;
            }
            if ( skill.getLevel() >= trainer.getMaxSkill() )
            {
                plugin.getOut().sendMessage( dCPlayer.getPlayer(), Messages.trainerMaxLevel, tag );
                dCPlayer.getPlayer().closeInventory();
                return;
            }

            trainer.depositOne( dCPlayer, clickedItem, trainerGUI );
        }

        trainer.setLastTrain( System.currentTimeMillis() );
    }

}
