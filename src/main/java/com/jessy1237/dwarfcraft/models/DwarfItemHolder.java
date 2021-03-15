/*
 * Copyright (c) 2018.
 *
 * DwarfCraft is an RPG plugin that allows players to improve their characters
 * skills and capabilities through training, not experience.
 *
 * Authors: Jessy1237 and Drekryan
 * Original Authors: smartaleq, LexManos and RCarretta
 */

package com.jessy1237.dwarfcraft.models;

import java.util.Set;

import com.jessy1237.dwarfcraft.DwarfCraft;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

public class DwarfItemHolder
{
    private final DwarfCraft plugin;
    private Set<Material> mats;
    private final Tag<Material> tag;
    private final String tagName;

    public DwarfItemHolder( DwarfCraft plugin, Set<Material> mats, Tag<Material> tag, String tagName )
    {
        this.plugin = plugin;
        this.tag = tag;
        this.mats = mats;
        this.tagName = tagName;

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

    public String getMaterialString()
    {
        StringBuilder builder = new StringBuilder("");
        int index = 0;
        for ( Material mat : mats ) {
            builder.append( plugin.getUtil().getCleanName( new ItemStack(mat) ) );
            if ( index++ < mats.size() - 1 ) builder.append(" OR ");
        }
        return builder.toString();
    }

    public ItemStack getItemStack()
    {
        return mats.isEmpty() ? new ItemStack( Material.AIR ) : new ItemStack( mats.iterator().next() );
    }

    public Tag<Material> getTag()
    {
        return tag;
    }

    public String getTagName()
    {
        return tagName;
    }
}
