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

import java.util.ArrayList;

import org.bukkit.Material;

public class DwarfRace
{

    private final String mName;
    private ArrayList<Integer> skills;
    private String Desc;
    private String prefixColour;
    private Material icon;

    public DwarfRace( String name )
    {
        this.mName = name;
    }

    public DwarfRace( String name, final ArrayList<Integer> skills, String Desc, Material icon )
    {
        this.mName = name;
        this.Desc = Desc;
        this.skills = skills;
        prefixColour = "&f";
        this.icon = icon;
    }

    public String getName()
    {
        return mName;
    }

    public ArrayList<Integer> getSkills()
    {
        return this.skills;
    }

    public String getDesc()
    {
        return this.Desc;
    }

    public String getPrefixColour()
    {
        return prefixColour;
    }

    public void setSkills( ArrayList<Integer> skills )
    {
        this.skills = skills;
    }

    public void setDesc( String Desc )
    {
        this.Desc = Desc;
    }

    public void setPrefixColour( String prefixColour )
    {
        this.prefixColour = prefixColour;
    }

    public Material getIcon()
    {
        return icon;
    }

    public void setIcon( Material icon )
    {
        this.icon = icon;
    }
}
