/*
 * Copyright (c) 2018.
 *
 * DwarfCraft is an RPG plugin that allows players to improve their characters
 * skills and capabilities through training, not experience.
 *
 * Authors: Jessy1237 and Drekryan
 * Original Authors: smartaleq, LexManos and RCarretta
 */

package com.Jessy1237.DwarfCraft.models;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DwarfTrainingItem
{
    private final Set<Material> mats;
    private final double base;
    private final int max;

    public DwarfTrainingItem( Set<Material> mats, double base, int max )
    {
        this.mats = mats;
        this.base = base;
        this.max = max;
    }

    public boolean isTag() {
        return mats.size() > 1;
    }

    public ItemStack getItemStack()
    {
        return new ItemStack(mats.iterator().next());
    }
    
    public Set<Material> getMaterials()
    {
        return mats;
    }

    public double getBase()
    {
        return this.base;
    }

    public int getMax()
    {
        return this.max;
    }
}
