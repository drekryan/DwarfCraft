/*
 * Copyright (c) 2018.
 *
 * DwarfCraft is an RPG plugin that allows players to improve their characters
 * skills and capabilities through training, not experience.
 *
 * Authors: Jessy1237 and Drekryan
 * Original Authors: smartaleq, LexManos and RCarretta
 */

package com.Jessy1237.DwarfCraft.schedules;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.Messages;
import com.Jessy1237.DwarfCraft.PlaceholderParser.PlaceHolder;
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

        if ( clickedItem.getType().equals( Material.LIGHT_BLUE_DYE ) || clickedItem.getType().equals( Material.LIME_DYE ) || clickedItem.getType().equals( Material.CACTUS_GREEN ))
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
                plugin.getOut().sendMessage( dCPlayer.getPlayer(), Messages.maxSkillLevel.replaceAll( PlaceHolder.SKILL_MAX_LEVEL.getPlaceHolder(), "" + plugin.getConfigManager().getMaxSkillLevel() ), tag );
                dCPlayer.getPlayer().closeInventory();
                return;
            }

            if ( skill.getLevel() >= trainer.getMaxSkill() )
            {
                plugin.getOut().sendMessage( dCPlayer.getPlayer(), Messages.trainerMaxLevel, tag );
                dCPlayer.getPlayer().closeInventory();
                return;
            }

            if ( clickedItem.getType() == Material.LIGHT_BLUE_DYE && clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase( "Deposit All" ) )
            {
                trainer.depositAll( dCPlayer, trainerGUI );
            }
            else if ( clickedItem.getType() == Material.LIME_DYE && clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase( "Train Skill" ) )
            {
                trainer.trainSkill( dCPlayer, trainerGUI );
            }
            else if ( clickedItem.getType() == Material.CACTUS_GREEN && clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase( "Train & Deposit Skill" ) )
            {
                if ( trainer.trainSkill( dCPlayer, trainerGUI ) )
                {
                    trainer.depositAll( dCPlayer, trainerGUI );
                }
            }
        }
        else
        {

            // Checks if after a level up if any of the limiting constraints have changed. i.e. player may have leveled up past the trainers ability while the inventory was open
            if ( skill.getLevel() >= plugin.getConfigManager().getRaceLevelLimit() && !plugin.getConfigManager().getAllSkills( dCPlayer.getRace() ).contains( skill.getId() ) )
            {
                plugin.getOut().sendMessage( dCPlayer.getPlayer(), Messages.raceDoesNotSpecialize.replaceAll( PlaceHolder.RACE_LEVEL_LIMIT.getPlaceHolder(), "" + plugin.getConfigManager().getRaceLevelLimit() ), tag );
                dCPlayer.getPlayer().closeInventory();
                return;
            }

            if ( skill.getLevel() >= plugin.getConfigManager().getMaxSkillLevel() )
            {
                plugin.getOut().sendMessage( dCPlayer.getPlayer(), Messages.maxSkillLevel.replaceAll( PlaceHolder.SKILL_MAX_LEVEL.getPlaceHolder(), "" + plugin.getConfigManager().getMaxSkillLevel() ), tag );
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
