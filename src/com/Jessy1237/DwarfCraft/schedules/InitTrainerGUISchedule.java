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

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.guis.TrainerGUI;

public class InitTrainerGUISchedule implements Runnable
{
    private TrainerGUI trainerGUI;
    private DwarfCraft plugin;

    public InitTrainerGUISchedule( DwarfCraft plugin, TrainerGUI trainerGUI )
    {
        this.trainerGUI = trainerGUI;
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        plugin.getDwarfInventoryListener().addDwarfGUI( trainerGUI.getDwarfPlayer().getPlayer(), trainerGUI );
    }
}
