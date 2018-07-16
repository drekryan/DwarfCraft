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

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

public class DwarfTrainingItem
{

    private final ItemStack itemStack;
    private final Tag tag;
    private final double base;
    private final int max;

    public DwarfTrainingItem( ItemStack item, double base, int max )
    {
        this.itemStack = item;
        this.tag = null;
        this.base = base;
        this.max = max;
    }

    public DwarfTrainingItem( Tag tag, double base, int max )
    {
        this.tag = tag;
        this.itemStack = null;
        this.base = base;
        this.max = max;
    }

    public boolean isTag() {
        return (this.itemStack == null && this.tag != null);
    }

    public ItemStack getItemStack()
    {
        if (isTag()) {
            if ( tag != null && tag.getValues().iterator().hasNext()) {
                return new ItemStack( (Material) tag.getValues().iterator().next() );
            } else {
                return new ItemStack( Material.AIR );
            }
        }

        return this.itemStack;
    }

    public Tag getTag() {
        return this.tag;
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
