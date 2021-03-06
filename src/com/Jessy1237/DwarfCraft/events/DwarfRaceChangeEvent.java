/*
 * Copyright (c) 2018.
 *
 * DwarfCraft is an RPG plugin that allows players to improve their characters
 * skills and capabilities through training, not experience.
 *
 * Authors: Jessy1237 and Drekryan
 * Original Authors: smartaleq, LexManos and RCarretta
 */

package com.Jessy1237.DwarfCraft.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfRace;

public class DwarfRaceChangeEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private DwarfPlayer player;
    private DwarfRace race;

    public HandlerList getHandlers()
    {
        return handlers;
    }

    public boolean isCancelled()
    {
        return cancelled;
    }

    public void setCancelled( boolean cancel )
    {
        cancelled = cancel;
    }

    public DwarfRaceChangeEvent( DwarfPlayer player, DwarfRace race )
    {
        this.player = player;
        this.race = race;
    }

    /**
     * Gets the DwarfPlayer that levelled up a skill.
     * 
     * @return DwarfPlayer
     */
    public DwarfPlayer getDwarfPlayer()
    {
        return player;
    }

    /**
     * Gets the race that the player is changing to.
     * 
     * @return DwarfRace
     */
    public DwarfRace getRace()
    {
        return race;
    }

    /**
     * Sets the race that the player will change to
     * 
     * @param race A dwarfcraft race that the player will change to.
     */
    public void setRace( DwarfRace race )
    {
        this.race = race;
    }
}
