package com.Jessy1237.DwarfCraft.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.Jessy1237.DwarfCraft.model.DwarfPlayer;
import com.Jessy1237.DwarfCraft.model.DwarfRace;

public class DwarfRaceChangeEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private DwarfPlayer player;
    private DwarfRace race;

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
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
     * @param race
     *            A dwarfcraft race that the player will change to.
     * 
     */
    public void setRace( DwarfRace race )
    {
        this.race = race;
    }
}
