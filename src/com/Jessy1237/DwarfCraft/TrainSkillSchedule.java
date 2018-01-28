package com.Jessy1237.DwarfCraft;

import org.bukkit.inventory.ItemStack;

public class TrainSkillSchedule implements Runnable
{

    private final DwarfTrainer trainer;
    private final DCPlayer dcplayer;
    private final ItemStack clickedItem;
    private final TrainerGUI trainerGUI;

    public TrainSkillSchedule( DwarfTrainer trainer, DCPlayer dcplayer, ItemStack clickedItem, TrainerGUI trainerGUI )
    {
        this.trainer = trainer;
        this.dcplayer = dcplayer;
        this.clickedItem = clickedItem;
        this.trainerGUI = trainerGUI;
    }

    @Override
    public void run()
    {
        trainer.trainSkill( dcplayer, clickedItem, trainerGUI );
    }

}
