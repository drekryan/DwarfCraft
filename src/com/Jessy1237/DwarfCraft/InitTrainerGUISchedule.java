package com.Jessy1237.DwarfCraft;

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
        trainerGUI.getDCPlayer().getPlayer().updateInventory();
        trainerGUI.openGUI();
    }
}
