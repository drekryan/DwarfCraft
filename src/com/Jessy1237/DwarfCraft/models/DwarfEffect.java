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

import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jbls.LexManos.CSV.CSVRecord;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.Messages;
import com.Jessy1237.DwarfCraft.events.DwarfEffectEvent;

public class DwarfEffect
{
    private DwarfCraft plugin;
    private int mID;
    private double mBase;
    private double mLevelIncrease;
    private double mLevelIncreaseNovice;
    private double mMin, mMax;
    private boolean mException;
    private double mExceptionLow;
    private double mExceptionHigh;
    private double mExceptionValue;
    private int mNormalLevel;
    private DwarfEffectType mType;
    private DwarfItemHolder mInitiator;
    private DwarfItemHolder mResult;
    private boolean mRequireTool;
    private Material[] mTools;
    private boolean mFloorResult;

    private EntityType mCreature;

    public DwarfEffect( CSVRecord record, DwarfCraft plugin )
    {
        if ( record == null )
            return;
        mID = record.getInt( "ID" );
        mBase = record.getDouble( "BaseValue" );
        mLevelIncrease = record.getDouble( "LevelIncrease" );
        mLevelIncreaseNovice = record.getDouble( "LevelIncreaseNovice" );
        mMin = record.getDouble( "Min" );
        mMax = record.getDouble( "Max" );
        mException = record.getBool( "Exception" );
        mExceptionLow = record.getInt( "ExceptionLow" );
        mExceptionHigh = record.getInt( "ExceptionHigh" );
        mExceptionValue = record.getDouble( "ExceptionValue" );
        mNormalLevel = record.getInt( "NormalLevel" );
        mType = DwarfEffectType.getEffectType( record.getString( "Type" ) );
        if ( mType != DwarfEffectType.MOBDROP && mType != DwarfEffectType.SHEAR || record.getString( "OriginMaterial" ).equalsIgnoreCase( "AIR" ) )
        {
            mInitiator = plugin.getUtil().getDwarfItemHolder( record, "OriginMaterial" );
        }
        else
        {
            mCreature = EntityType.valueOf( record.getString( "OriginMaterial" ) );
        }
        mResult = plugin.getUtil().getDwarfItemHolder( record, "OutputMaterial" );

        mRequireTool = record.getBool( "RequireTool" );
        mFloorResult = record.getBool( "Floor" );

        if ( record.getString( "Tools" ).isEmpty() )
            mTools = new Material[0];
        else
        {
            String[] stools = record.getString( "Tools" ).split( " " );
            mTools = new Material[stools.length];
            for ( int x = 0; x < stools.length; x++ )
            {
                Material mat = Material.getMaterial( stools[x] );
                if ( mat != null )
                    mTools[x] = mat;
            }
        }

        this.plugin = plugin;
    }

    @Override
    public boolean equals( Object o )
    {
        boolean equals = false;

        if ( o instanceof DwarfEffect )
        {
            DwarfEffect e = ( DwarfEffect ) o;
            equals = e.getId() == getId();
        }

        return equals;
    }

    /**
     * Description of a skills effect at a given level
     * 
     * @param dCPlayer the DwarfPlayer instance
     * @return description of a skills effect at a given level
     */
    public String describeLevel( DwarfPlayer dCPlayer )
    {
        if ( dCPlayer == null )
            return "Failed"; // TODO add failure code

        return plugin.getOut().parseEffectLevel( dCPlayer, this );
    }

    public String effectLevelColor( int skillLevel )
    {
        if ( skillLevel > mNormalLevel )
            return Messages.effectLevelColorGreaterThanNormal;
        else if ( skillLevel == mNormalLevel )
            return Messages.effectLevelColorEqualToNormal;
        else
            return Messages.effectLevelColorLessThanNormal;
    }

    /**
     * Returns an effect Amount for a particular Dwarf
     * 
     * @param dCPlayer the DwarfPlayer instance
     * @return the effect amount
     */
    public double getEffectAmount( DwarfPlayer dCPlayer )
    {
        return getEffectAmount( dCPlayer.getSkillLevel( this.mID / 10 ), dCPlayer );
    }

    @SuppressWarnings( "unlikely-arg-type" )
    public double getEffectAmount( int skillLevel, DwarfPlayer dCPlayer )
    {
        double effectAmount = mBase;
        if ( skillLevel == -1 )
            skillLevel = mNormalLevel;
        effectAmount += skillLevel * mLevelIncrease;
        effectAmount += Math.min( skillLevel, 5 ) * mLevelIncreaseNovice;
        effectAmount = Math.min( effectAmount, mMax );
        effectAmount = Math.max( effectAmount, mMin );

        if ( dCPlayer != null )
            if ( mException && skillLevel <= mExceptionHigh && skillLevel >= mExceptionLow && !( skillLevel == plugin.getConfigManager().getRaceLevelLimit() && plugin.getConfigManager().getAllSkills( dCPlayer.getRace() ).contains( plugin.getConfigManager().getAllSkills().get( mID / 10 ) ) ) )
                effectAmount = mExceptionValue;

        if ( DwarfCraft.debugMessagesThreshold < 1 )
        {
            plugin.getUtil().consoleLog( Level.FINE, String.format( "DC1: GetEffectAmmount ID: %d Level: %d Base: %.2f Increase: %.2f Novice: %.2f Max: %.2f Min: %.2f "
                    + "Exception: %s Exctpion Low: %.2f Exception High: %.2f Exception Value: %.2f Floor Result: %s", mID, skillLevel, mBase, mLevelIncrease, mLevelIncreaseNovice, mMax, mMin, mException, mExceptionLow, mExceptionHigh, mExceptionValue, mFloorResult ) );
        }

        // If effect type is SMELT, force effectAmount to be 1.
        if (mType == DwarfEffectType.SMELT) effectAmount = 1;

        return ( mFloorResult ? Math.floor( effectAmount ) : effectAmount );
    }

    public DwarfEffectType getEffectType()
    {
        return mType;
    }

    public int getId()
    {
        return mID;
    }

    public Material getInitiatorMaterial()
    {
        return ( mInitiator == null ? null : mInitiator.getItemStack() == null ? null : mInitiator.getItemStack().getType() );
    }

    public Material getOutputMaterial()
    {
        return ( mResult == null ? null : mResult.getItemStack() == null ? null : mResult.getItemStack().getType() );
    }

    public DwarfItemHolder getInitiator()
    {
        return mInitiator;
    }

    public DwarfItemHolder getResult()
    {
        return mResult;
    }

    public ItemStack getResult( DwarfPlayer player )
    {
        final int count = plugin.getUtil().randomAmount( getEffectAmount( player ) );
        ItemStack item = mResult.getItemStack();
        item.setAmount( count );

        return item;
    }

    public boolean getToolRequired()
    {
        return mRequireTool;
    }

    public Material[] getTools()
    {
        return mTools;
    }

    public boolean checkInitiator( ItemStack item )
    {
        if ( item == null )
            return checkInitiator( Material.AIR );
        else
            return checkInitiator( item.getType() );
    }

    public boolean checkInitiator( Material mat )
    {
        return mInitiator.isTagged() ? ( mInitiator.getMaterials().contains( mat ) ) : ( mInitiator.getItemStack().getType() == mat );
    }

    public String toolType()
    {
        for ( Material mat : mTools )
        {
            if ( mat == Material.IRON_SWORD )
                return "sword";
            if ( mat == Material.IRON_HOE )
                return "hoe";
            if ( mat == Material.IRON_AXE )
                return "axe";
            if ( mat == Material.WOODEN_PICKAXE )
                return "pickaxe";
            if ( mat == Material.IRON_PICKAXE )
                return "most pickaxes";
            if ( mat == Material.DIAMOND_PICKAXE )
                return "diamond pickaxe";
            if ( mat == Material.IRON_SHOVEL )
                return "shovel";
            if ( mat == Material.FISHING_ROD )
                return "fishing rod";
            if ( mat == Material.FLINT_AND_STEEL )
                return "flint and steel";
        }
        return "any tool";
    }

    public EntityType getCreature()
    {
        return mCreature;
    }

    public int getNormalLevel()
    {
        return mNormalLevel;
    }

    public boolean checkTool( ItemStack tool )
    {
        if ( !mRequireTool )
            return true;

        if ( tool == null )
            return false;

        for ( Material mat : mTools )
            if ( mat == tool.getType() )
                return true;

        return false;
    }

    @Override
    public String toString()
    {
        return Integer.toString( mID );
    }

    public void damageTool( DwarfPlayer player, int base, ItemStack tool )
    {
        damageTool( player, base, tool, true );
    }

    public void damageTool( DwarfPlayer player, int base, ItemStack tool, boolean negate )
    {
        short wear = ( short ) ( plugin.getUtil().randomAmount( getEffectAmount( player ) ) * base );

        if ( DwarfCraft.debugMessagesThreshold < 2 )
            plugin.getUtil().consoleLog( Level.FINE, String.format( "DC2: Affected durability of a \"%s\" - Effect: %d Old: %d Base: %d Wear: %d", plugin.getUtil().getCleanName( tool ), mID, tool.getDurability(), base, wear ) );

        // Some code taken from net.minecraft.server.ItemStack line 165.
        // Checks to see if damage should be skipped.
        if ( tool.containsEnchantment( Enchantment.DURABILITY ) )
        {
            int level = tool.getEnchantmentLevel( Enchantment.DURABILITY );
            Random r = new Random();
            if ( level > 0 && r.nextInt( level + 1 ) > 0 )
            {
                return;
            }
        }

        base = ( negate ? base : 0 );

        if ( wear == base )
            return; // This is normal wear, skip everything and let MC handle
                    // it
                    // internally.

        DwarfEffectEvent e = new DwarfEffectEvent( player, this, null, null, null, null, ( double ) base, ( double ) wear, null, null, tool );
        plugin.getServer().getPluginManager().callEvent( e );

        if ( e.isCancelled() )
            return;

        tool.setDurability( ( short ) ( tool.getDurability() + e.getAlteredDamage() - base ) );
        // This may have the side effect of causing items to flicker when they
        // are about to break
        // If this becomes a issue, we need to cast to a CraftItemStack, then
        // make CraftItemStack.item public,
        // And call CraftItemStack.item.damage(-base, player.getPlayer());

        if ( tool.getDurability() >= tool.getType().getMaxDurability() )
        {
            if ( tool.getType() == Material.IRON_SWORD && tool.getDurability() < 250 )
                return;

            if ( tool.getAmount() > 1 )
            {
                tool.setAmount( tool.getAmount() - 1 );
                tool.setDurability( ( short ) -1 );
            }
            else
            {
                if ( player.getPlayer().getEquipment().getItemInMainHand().getType() == tool.getType() )
                {
                    player.getPlayer().getEquipment().setItemInMainHand( null );
                }
                else if ( player.getPlayer().getEquipment().getItemInOffHand().getType() == tool.getType() )
                {
                    player.getPlayer().getEquipment().setItemInOffHand( null );
                }
            }
        }
    }

}
