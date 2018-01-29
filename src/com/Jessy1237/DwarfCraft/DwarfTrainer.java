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

    @SuppressWarnings( { "unused", "deprecation" } )
    public void trainSkill( DCPlayer dCPlayer, ItemStack clickedItemStack, TrainerGUI trainerGUI )
    {
        Skill skill = dCPlayer.getSkill( getSkillTrained() );
        Player player = dCPlayer.getPlayer();
        String tag = Messages.trainSkillPrefix.replaceAll( "%skillid%", "" + skill.getId() );
        List<List<ItemStack>> costs = dCPlayer.calculateTrainingCost( skill );
        List<ItemStack> trainingCostsToLevel = costs.get( 0 );
        // List<ItemStack> totalCostsToLevel = costs.get(1);

        boolean hasMats = true;
        boolean deposited = false;

        final PlayerInventory oldInv = player.getInventory();

        for ( ItemStack costStack : trainingCostsToLevel )
        {
            final int origCost = costStack.getAmount();
            int amountTaken = 0;
            if ( clickedItemStack.getType().equals( costStack.getType() ) )
            {
                //Checks if the trainer has already accepted the required item
                if ( costStack.getAmount() == 0 )
                {
                    plugin.getOut().sendMessage( player, Messages.noMoreItemNeeded.replaceAll( "%itemname%", plugin.getUtil().getCleanName( costStack ) ), tag );
                    continue;
                }
                
                if ( containsEnough( costStack, player ) )
                {
                    for ( ItemStack invStack : player.getInventory().getContents() )
                    {
                        if ( invStack == null )
                            continue;
                        if ( ( invStack.getType().equals( costStack.getType() ) && ( invStack.getDurability() == costStack.getDurability() || ( plugin.getUtil().isTool( invStack.getTypeId() ) && invStack.getDurability() == invStack.getType().getMaxDurability() ) ) )
                                || plugin.getUtil().checkEquivalentBuildBlocks( invStack.getTypeId(), costStack.getTypeId() ) != null )
                        {
                            int inv = invStack.getAmount();
                            int cost = costStack.getAmount();

                            if ( cost - inv >= 0 )
                            {
                                amountTaken += inv;
                                costStack.setAmount( cost - inv );
                                player.getInventory().removeItem( invStack );
                            }
                            else
                            {
                                amountTaken += cost;
                                invStack.setAmount( inv - cost );
                                costStack.setAmount( 0 );
                            }
                        }
                    }

                    // For now the method will only take the required amount otherwise it won't take any items
                    // TODO: separate out the methods for deposits (i.e. a specific item is clicked) and another for training the actual skill
                    trainerGUI.updateItem( costStack, origCost - amountTaken );
                    player.getWorld().playSound( player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f );
                    player.sendMessage( ChatColor.GREEN + "Removed " + costStack.getAmount() + "x " + costStack.getType() );

                    if ( costStack.getType().equals( skill.Item1.Item.getType() ) )
                    {
                        skill.setDeposit1( origCost );
                    }
                    else if ( costStack.getType().equals( skill.Item2.Item.getType() ) )
                    {
                        skill.setDeposit2( origCost );
                    }
                    else if ( costStack.getType().equals( skill.Item3.Item.getType() ) )
                    {
                        skill.setDeposit3( origCost );
                    }

                    Skill[] dCSkills = new Skill[1];
                    dCSkills[0] = skill;
                    plugin.getDataManager().saveDwarfData( dCPlayer, dCSkills );
                    this.setLastTrain( System.currentTimeMillis() );
                }
                else
                {
                    plugin.getOut().sendMessage( player, Messages.moreItemNeeded.replaceAll( "%costamount%", "" + costStack.getAmount() ).replaceAll( "%itemname%", plugin.getUtil().getCleanName( costStack ) ), tag );
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

    /**
     * Checks if the player's inventory contains enough of the required item or any equivalent item to train
     * 
     * @param costStack The cost stack to check against
     * @param player The player who is trying to skill up
     * @return true if the player contains enough of the item or its equivalents otherwise false
     */

    @SuppressWarnings( "deprecation" )
    private boolean containsEnough( ItemStack costStack, Player player )
    {

        if ( player.getInventory().containsAtLeast( costStack, costStack.getAmount() ) )
        {
            return true;
        }

        int amountHas = 0;
        for ( ItemStack item : player.getInventory().getContents() )
        {
            if ( item == null )
                continue;

            if ( item.getType().equals( costStack.getType() ) )
                amountHas += item.getAmount();

            if ( amountHas >= costStack.getAmount() )
                return true;
        }

        ArrayList<Integer> equivs = plugin.getUtil().checkEquivalentBuildBlocks( costStack.getTypeId(), -1 );

        if ( equivs != null )
        {
            for ( int id : equivs )
            {
                for ( ItemStack item : player.getInventory().getContents() )
                {
                    if ( item == null )
                        continue;

                    if ( item.getTypeId() == id )
                        amountHas += item.getAmount();

                    if ( amountHas >= costStack.getAmount() )
                        return true;
                }
            }
        }
        return false;
    }
}