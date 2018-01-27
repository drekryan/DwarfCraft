package com.Jessy1237.DwarfCraft;

import org.bukkit.inventory.Inventory;

public class TrainerGUI {
    private DwarfTrainer trainer;
    private DCPlayer player;
    private Inventory inventory;

    public TrainerGUI(DwarfTrainer trainer, DCPlayer player, Inventory inventory) {
        this.trainer = trainer;
        this.player = player;
        this.inventory = inventory;
    }

    public void openGui() {
        if (player != null && inventory != null) {
            player.getPlayer().openInventory(inventory);
        }
    }

    public DwarfTrainer getTrainer() {
        return trainer;
    }

    public DCPlayer getDcPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }
}