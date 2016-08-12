package com.Jessy1237.DwarfCraft.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.Jessy1237.DwarfCraft.DCPlayer;
import com.Jessy1237.DwarfCraft.Race;

public class DwarfCraftRaceChangeEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private DCPlayer player;
    private Race race;

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

    public DwarfCraftRaceChangeEvent( DCPlayer player, Race race )
    {
        this.player = player;
        this.race = race;
    }

    /**
     * Gets the DCPlayer that levelled up a skill.
     * 
     * @return DCPlayer
     */
    public DCPlayer getDCPlayer()
    {
        return player;
    }

    /**
     * Gets the race that the player is changing to.
     * 
     * @return Race
     */
    public Race getRace()
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
    public void setRace( Race race )
    {
        this.race = race;
    }
}
