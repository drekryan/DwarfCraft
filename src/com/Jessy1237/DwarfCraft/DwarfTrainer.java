package com.Jessy1237.DwarfCraft;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.citizensnpcs.api.npc.AbstractNPC;

public final class DwarfTrainer
{
    private AbstractNPC mEntity;
    private final DwarfCraft plugin;
    private boolean wait;
    private long lastTrain;

    public DwarfTrainer( final DwarfCraft plugin, AbstractNPC mEntity )
    {
        this.plugin = plugin;
        this.mEntity = mEntity;
        this.wait = false;
        this.lastTrain = 0;
    }

    @Override
    public boolean equals( Object that )
    {
        if ( this == that )
            return true;
        else if ( that instanceof HumanEntity )
            return ( mEntity.getId() == ( ( HumanEntity ) that ).getEntityId() );
        return false;
    }

    public AbstractNPC getEntity()
    {
        return mEntity;
    }

    public Location getLocation()
    {
        return mEntity.getStoredLocation();
    }

    protected int getMaterial()
    {
        return mEntity.getTrait( DwarfTrainerTrait.class ).getMaterial();
    }

    public World getWorld()
    {
        return mEntity.getStoredLocation().getWorld();
    }

    public int getMaxSkill()
    {
        return mEntity.getTrait( DwarfTrainerTrait.class ).getMaxSkill();
    }

    public Integer getMinSkill()
    {
        return mEntity.getTrait( DwarfTrainerTrait.class ).getMinSkill();
    }

    protected String getMessage()
    {
        return mEntity.getTrait( DwarfTrainerTrait.class ).getMessage();
    }

    public String getName()
    {
        return mEntity.getName();
    }

    public int getSkillTrained()
    {
        return mEntity.getTrait( DwarfTrainerTrait.class ).getSkillTrained();
    }

    public int getUniqueId()
    {
        return mEntity.getId();
    }

    public boolean isGreeter()
    {
        return mEntity.getTrait( DwarfTrainerTrait.class ).isGreeter();
    }

    public void printLeftClick( Player player )
    {
        GreeterMessage msg = plugin.getDataManager().getGreeterMessage( getMessage() );
        if ( msg != null )
        {
            plugin.getOut().sendMessage( player, msg.getLeftClickMessage() );
        }
        else
        {
            System.out.println( String.format( "[DC] Error: Greeter %s has no left click message. Check your configuration file for message ID %d", getUniqueId(), getMessage() ) );
        }
        return;
    }

    public void printRightClick( Player player )
    {
        GreeterMessage msg = plugin.getDataManager().getGreeterMessage( getMessage() );
        if ( msg != null )
        {
            plugin.getOut().sendMessage( player, msg.getRightClickMessage() );
        }

        return;
    }

    @SuppressWarnings( { "unused" } )
    public void trainSkill( DCPlayer dCPlayer, ItemStack clickedItemStack )
    {
        Skill skill = dCPlayer.getSkill( getSkillTrained() );
        Player player = dCPlayer.getPlayer();

        List<List<ItemStack>> costs = dCPlayer.calculateTrainingCost( skill );
        List<ItemStack> trainingCostsToLevel = costs.get( 0 );
        // List<ItemStack> totalCostsToLevel = costs.get(1);

        boolean hasMats = true;
        boolean deposited = false;

        final PlayerInventory oldInv = player.getInventory();

        for ( ItemStack costStack : trainingCostsToLevel )
        {
            if ( clickedItemStack.getType().equals( costStack.getType() ) )
            {
                if ( player.getInventory().containsAtLeast( costStack, costStack.getAmount() ) )
                {
                    int leftToRemove = costStack.getAmount();
                    List<ItemStack> stacksToRemove = new ArrayList<>();

                    while ( leftToRemove > 0 )
                    {
                        if ( leftToRemove >= costStack.getMaxStackSize() )
                        {
                            stacksToRemove.add( new ItemStack( costStack.getType(), costStack.getMaxStackSize() ) );
                            leftToRemove -= costStack.getMaxStackSize();
                        }
                        else
                        {
                            stacksToRemove.add( new ItemStack( costStack.getType(), leftToRemove ) );
                            leftToRemove = 0;
                        }
                    }

                    player.getInventory().removeItem( stacksToRemove.toArray( new ItemStack[stacksToRemove.size()] ) );
                    player.updateInventory();
                    player.getWorld().playSound( player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f );
                    player.sendMessage( ChatColor.GREEN + "Removed " + costStack.getAmount() + "x " + costStack.getType() );

                    this.setLastTrain( System.currentTimeMillis() );
                }
                else
                {
                    player.sendMessage( ChatColor.GOLD + "" + costStack.getAmount() + "x " + costStack.getType() );
                    player.sendMessage( ChatColor.RED + "ERROR! You dont have enough to train!" );
                }
            }
        }
    }

    public boolean isWaiting()
    {
        return this.wait;
    }

    public long getLastTrain()
    {
        return this.lastTrain;
    }

    public void setWait( boolean wait )
    {
        this.wait = wait;
    }

    public void setLastTrain( long lastTrain )
    {
        this.lastTrain = lastTrain;
    }

    public String getType()
    {
        return mEntity.getEntity().getType().toString();
    }
}