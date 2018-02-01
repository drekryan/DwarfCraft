package com.Jessy1237.DwarfCraft.guis;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.model.DwarfPlayer;
import com.Jessy1237.DwarfCraft.model.DwarfSkill;
import com.Jessy1237.DwarfCraft.model.DwarfTrainer;

public class ListTrainersGUI extends DwarfGUI {

    private DwarfTrainer[] trainers = null;
    private int page = 1; //TODO Support multiple pages
    private int inventorySize = 54;

    public ListTrainersGUI(DwarfCraft plugin, DwarfPlayer dwarfPlayer)
    {
        super(plugin, dwarfPlayer);
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public void init() {
        Collection<DwarfTrainer> col = plugin.getDataManager().trainerList.values();
        DwarfTrainer[] trainers = new DwarfTrainer[col.size()];
        col.toArray(trainers);

        this.trainers = trainers;
        this.inventory = plugin.getServer().createInventory(dwarfPlayer.getPlayer(), inventorySize, "Trainers List");

        System.out.println("Found " + trainers.length + " trainers to display");

        //TODO: Better handle 0 trainers
        int slot = 0;
        for (int index = 0; index <= trainers.length - 1; index++) {
            DwarfTrainer trainer = getTrainerAtSlot(index);
            DwarfSkill skill = dwarfPlayer.getSkill(trainer.getSkillTrained());

            ArrayList<String> lore = new ArrayList<>();
            lore.add( ChatColor.GOLD + "Skill: " + ChatColor.RED + skill.getDisplayName());
            lore.add( ChatColor.GOLD + "Min Level: " + ChatColor.WHITE + trainer.getMinSkill());
            lore.add( ChatColor.GOLD + "Max Level: " + ChatColor.WHITE + trainer.getMaxSkill());
            lore.add( ChatColor.GOLD + "Loc: " + ChatColor.WHITE + trainer.getLocation().getX() + ", " + trainer.getLocation().getY() + ", " + trainer.getLocation().getZ());
            lore.add("");
            lore.add( ChatColor.LIGHT_PURPLE + "Click to teleport to Trainer...");

            addItem(trainer.getName(), lore, slot, new ItemStack(Material.SKULL_ITEM, 1, (short)3, (byte)3));
            slot++;
        }
    }

    public DwarfTrainer getTrainerAtSlot(int slot) {
        if (trainers == null) return null;

        int startTrainer = (page * inventorySize) - inventorySize;
        return this.trainers[startTrainer + slot];
    }

}
