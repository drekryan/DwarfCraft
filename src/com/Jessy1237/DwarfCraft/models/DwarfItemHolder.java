package com.Jessy1237.DwarfCraft.models;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2018. DwarfCraft is an RPG plugin that allows players to improve their characters skills and capabilities through training, not experience. Authors: Jessy1237 and Drekryan
 */
public class DwarfItemHolder
{

    private Set<Material> mats;
    private Tag<Material> tag;

    public DwarfItemHolder( Set<Material> mats, Tag<Material> tag )
    {
        this.tag = tag;
        this.mats = mats;

        if ( tag != null )
        {
            this.mats = tag.getValues();
        }
    }

    public boolean isTagged()
    {
        return tag != null;
    }

    public Set<Material> getMaterials()
    {
        return mats;
    }

    public ItemStack getItemStack()
    {
        return new ItemStack( mats.iterator().next() );
    }

    public Tag<Material> getTag()
    {
        return tag;
    }
}
