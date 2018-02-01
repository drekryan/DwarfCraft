package com.Jessy1237.DwarfCraft.model;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.Messages;
import com.Jessy1237.DwarfCraft.events.DwarfCraftDepositEvent;
import com.Jessy1237.DwarfCraft.events.DwarfCraftLevelUpEvent;
import com.Jessy1237.DwarfCraft.guis.TrainerGUI;

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
        DwarfGreeterMessage msg = plugin.getDataManager().getGreeterMessage( getMessage() );
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
        DwarfGreeterMessage msg = plugin.getDataManager().getGreeterMessage( getMessage() );
        if ( msg != null )
        {
            plugin.getOut().sendMessage( player, msg.getRightClickMessage() );
        }

        return;
    }

    public void depositOne( DwarfPlayer dCPlayer, ItemStack clickedItemStack, TrainerGUI trainerGUI )
    {
        DwarfSkill skill = dCPlayer.getSkill( getSkillTrained() );
        final int dep1 = skill.getDeposit1(), dep2 = skill.getDeposit2(), dep3 = skill.getDeposit3();
        Player player = dCPlayer.getPlayer();
        List<List<ItemStack>> costs = dCPlayer.calculateTrainingCost( skill );
        List<ItemStack> trainingCostsToLevel = costs.get( 0 );
        String tag = Messages.trainSkillPrefix.replaceAll( "%skillid%", "" + skill.getId() );

        boolean deposited = false;
        final PlayerInventory oldInv = player.getInventory();

        for ( ItemStack costStack : trainingCostsToLevel )
        {
            if ( clickedItemStack.getType().equals( costStack.getType() ) )
            {
                deposited = depositItem( costStack, dCPlayer, trainerGUI, skill, tag )[1];
            }
        }

        DwarfCraftDepositEvent e = new DwarfCraftDepositEvent( dCPlayer, this, skill );
        plugin.getServer().getPluginManager().callEvent( e );

        if ( e.isCancelled() )
        {
            skill.setDeposit1( dep1 );
            skill.setDeposit2( dep2 );
            skill.setDeposit3( dep3 );

            player.getInventory().setContents( oldInv.getContents() );
            player.getInventory().setExtraContents( oldInv.getExtraContents() );

            return;
        }

        if ( deposited )
        {
            plugin.getOut().sendMessage( player, Messages.depositSuccessful, tag );
            DwarfSkill[] dCSkills = new DwarfSkill[1];
            dCSkills[0] = skill;
            plugin.getDataManager().saveDwarfData( dCPlayer, dCSkills );
            player.getWorld().playSound( player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f );
        }
    }

    public void depositAll( DwarfPlayer dCPlayer, ItemStack clickedItemStack, TrainerGUI trainerGUI )
    {
        DwarfSkill skill = dCPlayer.getSkill( getSkillTrained() );
        final int dep1 = skill.getDeposit1(), dep2 = skill.getDeposit2(), dep3 = skill.getDeposit3();
        Player player = dCPlayer.getPlayer();
        List<List<ItemStack>> costs = dCPlayer.calculateTrainingCost( skill );
        List<ItemStack> trainingCostsToLevel = costs.get( 0 );
        String tag = Messages.trainSkillPrefix.replaceAll( "%skillid%", "" + skill.getId() );

        final PlayerInventory oldInv = player.getInventory();
        boolean deposited = false;

        for ( ItemStack costStack : trainingCostsToLevel )
        {
            deposited = depositItem( costStack, dCPlayer, trainerGUI, skill, tag )[1];
        }

        DwarfCraftDepositEvent e = new DwarfCraftDepositEvent( dCPlayer, this, skill );
        plugin.getServer().getPluginManager().callEvent( e );

        if ( e.isCancelled() )
        {
            skill.setDeposit1( dep1 );
            skill.setDeposit2( dep2 );
            skill.setDeposit3( dep3 );

            player.getInventory().setContents( oldInv.getContents() );
            player.getInventory().setExtraContents( oldInv.getExtraContents() );

            return;
        }

        if ( deposited )
        {
            plugin.getOut().sendMessage( player, Messages.depositSuccessful, tag );
            DwarfSkill[] dCSkills = new DwarfSkill[1];
            dCSkills[0] = skill;
            plugin.getDataManager().saveDwarfData( dCPlayer, dCSkills );
            player.getWorld().playSound( player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f );
        }
    }

    public void trainSkill( DwarfPlayer dCPlayer, ItemStack clickedItemStack, TrainerGUI trainerGUI )
    {
        DwarfSkill skill = dCPlayer.getSkill( getSkillTrained() );
        Player player = dCPlayer.getPlayer();
        List<List<ItemStack>> costs = dCPlayer.calculateTrainingCost( skill );
        List<ItemStack> trainingCostsToLevel = costs.get( 0 );
        String tag = Messages.trainSkillPrefix.replaceAll( "%skillid%", "" + skill.getId() );

        final PlayerInventory oldInv = player.getInventory();
        boolean hasMatsOrDeposits[] = { true, false };

        for ( ItemStack costStack : trainingCostsToLevel )
        {
            hasMatsOrDeposits = depositItem( costStack, dCPlayer, trainerGUI, skill, tag );
        }

        DwarfCraftLevelUpEvent e = null;
        final int dep1 = skill.getDeposit1(), dep2 = skill.getDeposit2(), dep3 = skill.getDeposit3();
        if ( hasMatsOrDeposits[0] )
        {
            skill.setLevel( skill.getLevel() + 1 );
            skill.setDeposit1( 0 );
            skill.setDeposit2( 0 );
            skill.setDeposit3( 0 );

            e = new DwarfCraftLevelUpEvent( dCPlayer, this, skill );

            plugin.getServer().getPluginManager().callEvent( e );
        }
        if ( hasMatsOrDeposits[1] || hasMatsOrDeposits[0] )
        {

            if ( e != null )
            {
                if ( e.isCancelled() )
                {
                    skill.setLevel( skill.getLevel() - 1 );
                    skill.setDeposit1( dep1 );
                    skill.setDeposit2( dep2 );
                    skill.setDeposit3( dep3 );

                    player.getInventory().setContents( oldInv.getContents() );
                    player.getInventory().setExtraContents( oldInv.getExtraContents() );

                    return;
                }
                else
                {
                    plugin.getOut().sendMessage( player, Messages.trainingSuccessful, tag );
                }
            }

            DwarfSkill[] dCSkills = new DwarfSkill[1];
            dCSkills[0] = skill;
            plugin.getDataManager().saveDwarfData( dCPlayer, dCSkills );
            trainerGUI.updateTitle();
            player.getWorld().playSound( player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f );
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
     * Checks if the player's inventory contains the required item or any equivalent item to train
     * 
     * @param costStack The cost stack to check against
     * @param player The player who is trying to skill up
     * @return true if the player contains enough of the item or its equivalents otherwise false
     */
    @SuppressWarnings( "deprecation" )
    private boolean containsItem( ItemStack costStack, Player player )
    {

        if ( player.getInventory().containsAtLeast( costStack, costStack.getAmount() ) )
        {
            return true;
        }

        for ( ItemStack item : player.getInventory().getContents() )
        {
            if ( item == null )
                continue;

            if ( item.getType().equals( costStack.getType() ) )
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
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Cycles through he players inventory and removes the same or equivalent item to the costStack and removes that amount.
     * 
     * @param player The player to check the inventory
     * @param costStack The item to check against and remove
     * @return The amount of removed from the players inventory
     */
    @SuppressWarnings( "deprecation" )
    private int removeItem( Player player, ItemStack costStack, String tag )
    {
        int amountTaken = 0;
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

        return amountTaken;
    }

    /**
     * Removes the required amount of the item and deposits into the trainer
     * 
     * @param costStack The item type and amount to be removed
     * @param dCPlayer The player that is depositing
     * @param trainerGUI The guis that flagged this deposit event
     * @param skill The skill that is being deposited into
     * @return True for [0] if the player had enough of the required item otherwise [0] false and True for [1] if any items were deposited into the skill otherwise false for [1]
     */
    private boolean[] depositItem( ItemStack costStack, DwarfPlayer dCPlayer, TrainerGUI trainerGUI, DwarfSkill skill, String tag )
    {
        boolean[] hasMatsOrDeposits = { true, false };
        final int origCost = costStack.getAmount();
        Player player = dCPlayer.getPlayer();

        // Checks if the trainer has already accepted the required item
        if ( costStack.getAmount() == 0 )
        {
            plugin.getOut().sendMessage( player, Messages.noMoreItemNeeded.replaceAll( "%itemname%", plugin.getUtil().getCleanName( costStack ) ), tag );
        }
        else
        {
            if ( containsItem( costStack, player ) )
            {

                int amountTaken = removeItem( player, costStack, tag );

                // Settings true for items being deposited
                if ( amountTaken > 0 )
                    hasMatsOrDeposits[1] = true;

                // For now the method will only take the required amount otherwise it won't take any items
                // TODO: separate out the methods for deposits (i.e. a specific item is clicked) and another for training the actual skill
                trainerGUI.updateItem( costStack, origCost - amountTaken );

                if ( costStack.getType().equals( skill.Item1.Item.getType() ) )
                {
                    skill.setDeposit1( skill.getDeposit1() + amountTaken );
                }
                else if ( costStack.getType().equals( skill.Item2.Item.getType() ) )
                {
                    skill.setDeposit2( skill.getDeposit1() + amountTaken );
                }
                else if ( costStack.getType().equals( skill.Item3.Item.getType() ) )
                {
                    skill.setDeposit3( skill.getDeposit1() + amountTaken );
                }
            }
            else
            {
                hasMatsOrDeposits[0] = false;
                plugin.getOut().sendMessage( player, Messages.moreItemNeeded.replaceAll( "%costamount%", "" + costStack.getAmount() ).replaceAll( "%itemname%", plugin.getUtil().getCleanName( costStack ) ), tag );
                return hasMatsOrDeposits;
            }

            if ( costStack.getAmount() == 0 )
            {
                plugin.getOut().sendMessage( player, Messages.noMoreItemNeeded.replaceAll( "%itemname%", plugin.getUtil().getCleanName( costStack ) ), tag );
            }
            else
            {
                plugin.getOut().sendMessage( player, Messages.moreItemNeeded.replaceAll( "%costamount%", "" + costStack.getAmount() ).replaceAll( "%itemname%", plugin.getUtil().getCleanName( costStack ) ), tag );
                hasMatsOrDeposits[0] = false;
                hasMatsOrDeposits[1] = true;
            }
        }

        return hasMatsOrDeposits;
    }
}