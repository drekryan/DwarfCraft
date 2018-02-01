package com.Jessy1237.DwarfCraft.schedules;

import com.Jessy1237.DwarfCraft.guis.TrainerGUI;

public class InitTrainerGUISchedule implements Runnable
{
    private TrainerGUI trainerGUI;

    public InitTrainerGUISchedule( TrainerGUI trainerGUI )
    {
        this.trainerGUI = trainerGUI;
    }

    @Override
    public void run()
    {
        trainerGUI.init();
        trainerGUI.getDwarfPlayer().getPlayer().updateInventory();
        trainerGUI.openGUI();
    }
}
