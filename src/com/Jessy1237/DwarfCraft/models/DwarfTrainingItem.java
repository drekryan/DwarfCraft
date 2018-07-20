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

import java.util.Collections;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

public class DwarfTrainingItem
{
    private final ItemStack itemStack;
    private final double base;
    private final int max;
    private final String tag;

    public DwarfTrainingItem( ItemStack item, double base, int max, String tag )
    {
        this.itemStack = item;
        this.base = base;
        this.max = max;
        this.tag = tag;
    }

    public boolean isTag() {
        return ( this.tag != null && !this.tag.isEmpty() );
    }

    public Tag getTag()
    {
        //TODO: Support for checking the REGISTRY_ITEMS IF REGISTRY_BLOCKS FAILS
        if ( isTag() )
            return Bukkit.getTag( Tag.REGISTRY_BLOCKS, NamespacedKey.minecraft( this.tag ), Material.class );

        return null;
    }

    public ItemStack getItemStack()
    {
        //TODO: Support for checking the REGISTRY_ITEMS IF REGISTRY_BLOCKS FAILS
        if ( isTag() ) {
            Tag tag = Bukkit.getTag( Tag.REGISTRY_BLOCKS, NamespacedKey.minecraft( this.tag ), Material.class );
            return new ItemStack( (Material) tag.getValues().iterator().next() );
        }

        return this.itemStack;
    }

    public Set<Material> getMatchingMaterials()
    {
        if ( this.tag.equals( "" ) ) return Collections.emptySet();

        // TODO 1.13: Check REGISTRY_ITEMS too!
        Tag<Material> tag = Bukkit.getTag( Tag.REGISTRY_BLOCKS, NamespacedKey.minecraft( this.tag ), Material.class );
        return tag.getValues();
    }

    public boolean matchesTag( Material mat )
    {
        Set<Material> mats = getMatchingMaterials( );
        if ( mats == null || mats.size() <= 0 )
            return false;
        return mats.contains( mat );
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
