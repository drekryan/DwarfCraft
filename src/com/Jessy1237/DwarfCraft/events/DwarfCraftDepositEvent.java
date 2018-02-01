package com.Jessy1237.DwarfCraft.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.Jessy1237.DwarfCraft.model.DwarfPlayer;
import com.Jessy1237.DwarfCraft.model.DwarfSkill;
import com.Jessy1237.DwarfCraft.model.DwarfTrainer;

public class DwarfCraftDepositEvent extends Event implements Cancellable
{

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private DwarfPlayer player;
    private DwarfTrainer trainer;
    private DwarfSkill skill;

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

    /**
     * The event for when a DwarfPlayer deposits into their skill. This event is fired
     * after the skill is levelled but before the data is saved.
     * 
     * @param player
     *            the player that levelled up a skill
     * @param trainer
     *            the trainer that was used to level up the skill
     * @param skill
     *            the skill that was levelled up
     */
    public DwarfCraftDepositEvent( DwarfPlayer player, DwarfTrainer trainer, DwarfSkill skill )
    {
        this.player = player;
        this.trainer = trainer;
        this.skill = skill;
    }

    /**
     * Gets the DwarfPlayer that levelled up a skill.
     * 
     * @return DwarfPlayer
     */
    public DwarfPlayer getDCPlayer()
    {
        return player;
    }

    /**
     * Gets the trainer that was used to level up the skill.
     * 
     * @return Trainer
     */
    public DwarfTrainer getTrainer()
    {
        return trainer;
    }

    /**
     * Gets the skill that was deposited into.
     * 
     * @return DwarfSkill
     */
    public DwarfSkill getSkill()
    {
        return skill;
    }
}
