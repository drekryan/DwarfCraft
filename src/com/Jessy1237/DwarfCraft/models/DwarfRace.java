package com.Jessy1237.DwarfCraft.models;

import java.util.ArrayList;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

public class DwarfRace
{

    private final String mName;
    private ArrayList<Integer> skills;
    private String Desc;
    private String prefixColour;

    public DwarfRace( String name )
    {
        this.mName = name;
    }

    public DwarfRace( String name, final ArrayList<Integer> skills, String Desc )
    {
        this.mName = name;
        this.Desc = Desc;
        this.skills = skills;
        prefixColour = "&f";
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
}
