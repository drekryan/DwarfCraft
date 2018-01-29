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
        // TODO:Add gui functionality for button to train the skill only one level and another button as previously worked with leveling up and depositing leftovers. Already have functionality to
        // deposit one item type at a time.
        trainer.depositSkill( dcplayer, clickedItem, trainerGUI );
    }

}
