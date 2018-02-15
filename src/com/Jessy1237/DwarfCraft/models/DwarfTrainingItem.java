package com.Jessy1237.DwarfCraft.models;

import org.bukkit.inventory.ItemStack;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */
public class DwarfTrainingItem
{

    public final ItemStack Item;
    public final double Base;
    public final int Max;

    public DwarfTrainingItem( ItemStack item, double base, int max )
    {
        Item = item;
        Base = base;
        Max = max;
    }
}
