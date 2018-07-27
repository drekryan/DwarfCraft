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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.inventory.ItemStack;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.Messages;
import com.Jessy1237.DwarfCraft.PlaceholderParser.PlaceHolder;
import com.Jessy1237.DwarfCraft.guis.TrainerGUI;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;
import com.Jessy1237.DwarfCraft.models.DwarfTrainer;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

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

        if ( clickedItem.getType().equals( Material.LIGHT_BLUE_DYE ) || clickedItem.getType().equals( Material.LIME_DYE ) || clickedItem.getType().equals( Material.CACTUS_GREEN ))
        {
            // Checks if after a level up if any of the limitting constraints have changed. i.e. player may have levelled up past the trainers ability while the inventory was open
            if ( skill.getLevel() >= plugin.getConfigManager().getRaceLevelLimit() && !plugin.getConfigManager().getAllSkills( dCPlayer.getRace() ).contains( skill.getId() ) )
            {
                dCPlayer.getPlayer().spigot().sendMessage( ChatMessageType.ACTION_BAR, new TextComponent( ChatColor.translateAlternateColorCodes( '&', Messages.raceDoesNotSpecialize.replaceAll( PlaceHolder.RACE_LEVEL_LIMIT.getPlaceHolder(), "" + plugin.getConfigManager().getRaceLevelLimit() ) ) ) );
                dCPlayer.getPlayer().playSound( dCPlayer.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, SoundCategory.MASTER, 0.5f, 1.0f );
                dCPlayer.getPlayer().closeInventory();
                return;
            }

            if ( skill.getLevel() >= plugin.getConfigManager().getMaxSkillLevel() )
            {
                dCPlayer.getPlayer().spigot().sendMessage( ChatMessageType.ACTION_BAR, new TextComponent( ChatColor.translateAlternateColorCodes( '&', Messages.maxSkillLevel.replaceAll( PlaceHolder.SKILL_MAX_LEVEL.getPlaceHolder(), "" + plugin.getConfigManager().getMaxSkillLevel() ) ) ) );
                dCPlayer.getPlayer().playSound(dCPlayer.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, SoundCategory.MASTER, 0.5f, 1.0f );
                dCPlayer.getPlayer().closeInventory();
                return;
            }

            if ( skill.getLevel() >= trainer.getMaxSkill() )
            {
                dCPlayer.getPlayer().spigot().sendMessage( ChatMessageType.ACTION_BAR, new TextComponent( ChatColor.translateAlternateColorCodes( '&', Messages.trainerMaxLevel ) ) );
                dCPlayer.getPlayer().playSound(dCPlayer.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, SoundCategory.MASTER, 0.5f, 1.0f );
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
                dCPlayer.getPlayer().spigot().sendMessage( ChatMessageType.ACTION_BAR, new TextComponent( ChatColor.translateAlternateColorCodes( '&', Messages.raceDoesNotSpecialize.replaceAll( PlaceHolder.RACE_LEVEL_LIMIT.getPlaceHolder(), "" + plugin.getConfigManager().getRaceLevelLimit() ) ) ) );
                dCPlayer.getPlayer().closeInventory();
                return;
            }

            if ( skill.getLevel() >= plugin.getConfigManager().getMaxSkillLevel() )
            {
                dCPlayer.getPlayer().spigot().sendMessage( ChatMessageType.ACTION_BAR, new TextComponent( ChatColor.translateAlternateColorCodes( '&', Messages.maxSkillLevel.replaceAll( PlaceHolder.SKILL_MAX_LEVEL.getPlaceHolder(), "" + plugin.getConfigManager().getMaxSkillLevel() ) ) ) );
                dCPlayer.getPlayer().closeInventory();
                return;
            }
            if ( skill.getLevel() >= trainer.getMaxSkill() )
            {
                dCPlayer.getPlayer().spigot().sendMessage( ChatMessageType.ACTION_BAR, new TextComponent( ChatColor.translateAlternateColorCodes( '&', Messages.trainerMaxLevel ) ) );
                dCPlayer.getPlayer().closeInventory();
                return;
            }

            trainer.depositOne( dCPlayer, clickedItem, trainerGUI );
        }

        trainer.setLastTrain( System.currentTimeMillis() );
    }

}
