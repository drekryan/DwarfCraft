package com.Jessy1237.DwarfCraft;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.jbls.LexManos.CSV.CSVRecord;

import com.Jessy1237.DwarfCraft.events.DwarfCraftEffectEvent;

@SuppressWarnings( "deprecation" )
public class Effect
{
    private DwarfCraft plugin;
    private int mID;
    private double mBase;
    private double mLevelIncrease;
    private double mLevelIncreaseNovice;
    private double mMin;
    private double mMax;
    private boolean mException;
    private double mExceptionLow;
    private double mExceptionHigh;
    private double mExceptionValue;
    private int mNormalLevel;
    private EffectType mType;
    private ItemStack mInitiator;
    private ItemStack mOutput;
    private boolean mRequireTool;
    private int[] mTools;
    private boolean mFloorResult;

    private EntityType mCreature;

    public Effect( CSVRecord record, DwarfCraft plugin )
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
        mType = EffectType.getEffectType( record.getString( "Type" ) );
        if ( mType != EffectType.MOBDROP && mType != EffectType.SHEAR )
        {
            mInitiator = plugin.getUtil().parseItem( record.getString( "OriginID" ) );
        }
        else
        {
            mCreature = EntityType.fromName( record.getString( "OriginID" ) );
        }
        mOutput = plugin.getUtil().parseItem( record.getString( "OutputID" ) );
        mRequireTool = record.getBool( "RequireTool" );
        mFloorResult = record.getBool( "Floor" );

        if ( record.getString( "Tools" ).isEmpty() )
            mTools = new int[0];
        else
        {
            String[] stools = record.getString( "Tools" ).split( " " );
            mTools = new int[stools.length];
            for ( int x = 0; x < stools.length; x++ )
                mTools[x] = Integer.parseInt( stools[x] );
        }

        this.plugin = plugin;
    }

    /**
     * General description of a benefit including minimum and maximum benefit
     * 
     * @return
     */
    protected String describeGeneral( DCPlayer dCPlayer )
    {
        String description;
        String initiator = plugin.getUtil().getCleanName( mInitiator );
        if ( initiator.equalsIgnoreCase( "AIR" ) )
            initiator = "None";
        String output = plugin.getUtil().getCleanName( mOutput );
        if ( output.equalsIgnoreCase( "AIR" ) )
            output = "None";
        double effectAmountLow = getEffectAmount( 0, dCPlayer );
        double effectAmountHigh = getEffectAmount( 30, dCPlayer );
        double minorAmount = getEffectAmount( -1, dCPlayer );
        String toolType = toolType();

        description = Messages.describeGeneral;
        description = description.replaceAll( "%initiator%", initiator );
        description = description.replaceAll( "%output%", output );
        description = description.replaceAll( "%effectamountlow%", String.format( "%.2f", effectAmountLow ) );
        description = description.replaceAll( "%effectamounthigh%", String.format( "%.2f", effectAmountHigh ) );
        description = description.replaceAll( "%minoramount%", String.format( "%.2f", minorAmount ) );
        description = description.replaceAll( "%normallevel%", "" + mNormalLevel );
        description = description.replaceAll( "%tooltype%", toolType );

        return description;
    }

    /**
     * Description of a skills effect at a given level
     * 
     * @param dCPlayer
     * @return
     */
    protected String describeLevel( DCPlayer dCPlayer )
    {
        if ( dCPlayer == null )
            return "Failed"; // TODO add failure code

        String description = "no skill description";
        // Variables used in skill descriptions
        String initiator = plugin.getUtil().getCleanName( mInitiator );
        String output = plugin.getUtil().getCleanName( mOutput );

        double effectAmount = getEffectAmount( dCPlayer );
        double minorAmount = getEffectAmount( mNormalLevel, null );
        boolean moreThanOne = ( effectAmount > 1 );
        String effectLevelColor = effectLevelColor( dCPlayer.getSkill( this ).getLevel() );
        String toolType = toolType();

        if ( mType == EffectType.SMELT )
        {
            if ( mInitiator.getTypeId() == 265 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.IRON_ORE ) );
            }
            else if ( mInitiator.getTypeId() == 266 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.GOLD_ORE ) );
            }
            else if ( mInitiator.getTypeId() == 320 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.PORK ) );
            }
            else if ( mInitiator.getTypeId() == 350 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.RAW_FISH ) );
            }
            else if ( mInitiator.getTypeId() == 366 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.RAW_CHICKEN ) );
            }
            else if ( mInitiator.getTypeId() == 412 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.RABBIT ) );
            }
            else if ( mInitiator.getTypeId() == 424 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.MUTTON ) );
            }
            else if ( mInitiator.getTypeId() == 364 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.RAW_BEEF ) );
            }
            else if ( mInitiator.getTypeId() == 20 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.SAND ) );
            }
            else if ( mInitiator.getTypeId() == 393 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.POTATO_ITEM ) );
            }
            else if ( mInitiator.getTypeId() == 263 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.LOG ) );
            }
            else if ( mInitiator.getTypeId() == 1 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.COBBLESTONE ) );
            }
            else if ( mInitiator.getTypeId() == 336 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.CLAY_BALL ) );
            }
            else if ( mInitiator.getTypeId() == 405 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.NETHERRACK ) );
            }
            else if ( mInitiator.getTypeId() == 172 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.CLAY ) );
            }
            else if ( mInitiator.getTypeId() == 351 )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.CACTUS ) );
            }
        }

        description = plugin.getOut().parseEffectLevel( mType, initiator, output, effectAmount, minorAmount, moreThanOne, effectLevelColor, toolType, mCreature, dCPlayer, mInitiator );

        return description;
    }

    private String effectLevelColor( int skillLevel )
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
     * @param dCPlayer
     * @return
     */
    public double getEffectAmount( DCPlayer dCPlayer )
    {
        return getEffectAmount( dCPlayer.getSkillLevel( this.mID / 10 ), dCPlayer );
    }

    public double getEffectAmount( int skillLevel, DCPlayer dCPlayer )
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
            System.out.println( String.format( "DC1: GetEffectAmmount ID: %d Level: %d Base: %.2f Increase: %.2f Novice: %.2f Max: %.2f Min: %.2f "
                    + "Exception: %s Exctpion Low: %.2f Exception High: %.2f Exception Value: %.2f Floor Result: %s", mID, skillLevel, mBase, mLevelIncrease, mLevelIncreaseNovice, mMax, mMin, mException, mExceptionLow, mExceptionHigh, mExceptionValue, mFloorResult ) );
        }
        return ( mFloorResult ? Math.floor( effectAmount ) : effectAmount );
    }

    public EffectType getEffectType()
    {
        return mType;
    }

    protected int getElfEffectLevel()
    {
        return mNormalLevel;
    }

    public int getId()
    {
        return mID;
    }

    public int getInitiatorId()
    {
        return mInitiator.getTypeId();
    }

    public int getOutputId()
    {
        return mOutput.getTypeId();
    }

    public ItemStack getOutput()
    {
        return mOutput;
    }

    public ItemStack getOutput( DCPlayer player )
    {
        return getOutput( player, ( byte ) 0, -1 );
    }

    public ItemStack getOutput( DCPlayer player, Byte oldData )
    {
        return getOutput( player, oldData, -1 );
    }

    public ItemStack getOutput( DCPlayer player, Byte oldData, int oldID )
    {
        Byte data = ( mOutput.getData() == null ? null : mOutput.getData().getData() );

        if ( data != null && data == 0 )
            data = oldData;

        final int count = plugin.getUtil().randomAmount( getEffectAmount( player ) );
        ItemStack item = null;
        if ( plugin.getUtil().checkEquivalentBuildBlocks( mOutput.getTypeId(), oldID ) == null || oldID == -1 )
        {
            item = new ItemStack( mOutput.getTypeId(), count, data );
        }
        else
        {
            item = new ItemStack( oldID, count, data );
        }
        return item;
    }

    public boolean getToolRequired()
    {
        return mRequireTool;
    }

    public int[] getTools()
    {
        return mTools;
    }

    public boolean checkInitiator( ItemStack item )
    {
        if ( item == null )
            return checkInitiator( 0, ( byte ) 0 );
        else
            return checkInitiator( item.getTypeId(), ( byte ) item.getDurability() );
    }

    public boolean checkInitiator( int id, byte data )
    {
        if ( mInitiator.getTypeId() != id && plugin.getUtil().checkEquivalentBuildBlocks( id, mInitiator.getTypeId() ) == null )
            return false;

        if ( mInitiator.getData() != null )
        {
            if ( mInitiator.getData().getData() == 0 ) // 0 means we dont care.
                return true;
            return mInitiator.getData().getData() == data;
        }
        return true;
    }

    /**
     * Tool to string parser for effect descriptions
     * 
     * @return
     */
    private String toolType()
    {
        for ( int toolId : mTools )
        {
            if ( toolId == 267 )
                return "swords";
            if ( toolId == 292 )
                return "hoes";
            if ( toolId == 258 )
                return "axes";
            if ( toolId == 270 )
                return "pickaxes";
            if ( toolId == 257 )
                return "most picks";
            if ( toolId == 278 )
                return "high picks";
            if ( toolId == 256 )
                return "shovels";
            if ( toolId == 346 )
                return "fishing rod";
        }
        return "any tool";
    }

    public boolean checkMob( Entity entity )
    {
        if ( mCreature == null )
            return false;

        switch ( mCreature )
        {
            case CHICKEN:
                return ( entity instanceof Chicken );
            case COW:
                return ( entity instanceof Cow );
            case CREEPER:
                return ( entity instanceof Creeper );
            case GHAST:
                return ( entity instanceof Ghast );
            case GIANT:
                return ( entity instanceof Giant );
            case PIG:
                return ( entity instanceof Pig );
            case PIG_ZOMBIE:
                return ( entity instanceof PigZombie );
            case SHEEP:
                return ( entity instanceof Sheep );
            case SKELETON:
                return ( entity instanceof Skeleton );
            case SLIME:
                return ( entity instanceof Slime );
            case SPIDER:
                return ( entity instanceof Spider );
            case SQUID:
                return ( entity instanceof Squid );
            case ZOMBIE:
                return ( entity instanceof Zombie ) && !( entity instanceof PigZombie );
            case WOLF:
                return ( entity instanceof Wolf );
            case MUSHROOM_COW:
                return ( entity instanceof MushroomCow );
            case SILVERFISH:
                return ( entity instanceof Silverfish );
            case ENDERMAN:
                return ( entity instanceof Enderman );
            case VILLAGER:
                return ( entity instanceof Villager );
            case BLAZE:
                return ( entity instanceof Blaze );
            case MAGMA_CUBE:
                return ( entity instanceof MagmaCube );
            case CAVE_SPIDER:
                return ( entity instanceof CaveSpider );
            case SNOWMAN:
                return ( entity instanceof Snowman );
            case ENDER_DRAGON:
                return ( entity instanceof EnderDragon );
            case OCELOT:
                return ( entity instanceof Ocelot );
            case WITHER:
                return ( entity instanceof Wither );
            case IRON_GOLEM:
                return ( entity instanceof IronGolem );
            case BAT:
                return ( entity instanceof Bat );
            case RABBIT:
                return ( entity instanceof Rabbit );
            case GUARDIAN:
                return ( entity instanceof Guardian );
            case HORSE:
                return ( entity instanceof Horse );
            case ENDERMITE:
                return ( entity instanceof Endermite );
            case WITCH:
                return ( entity instanceof Witch );
            case POLAR_BEAR:
                return ( entity instanceof PolarBear );
            case SHULKER:
                return ( entity instanceof Shulker );
            default:
                return false;
        }
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

        for ( int id : mTools )
            if ( id == tool.getTypeId() )
                return true;

        return false;
    }

    @Override
    public String toString()
    {
        return Integer.toString( mID );
    }

    public void damageTool( DCPlayer player, int base, ItemStack tool )
    {
        damageTool( player, base, tool, true );
    }

    public void damageTool( DCPlayer player, int base, ItemStack tool, boolean negate )
    {
        short wear = ( short ) ( plugin.getUtil().randomAmount( getEffectAmount( player ) ) * base );

        if ( DwarfCraft.debugMessagesThreshold < 2 )
            System.out.println( String.format( "DC2: Affected durability of a \"%s\" - Effect: %d Old: %d Base: %d Wear: %d", plugin.getUtil().getCleanName( tool ), mID, tool.getDurability(), base, wear ) );

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

        DwarfCraftEffectEvent e = new DwarfCraftEffectEvent( player, this, null, null, null, null, ( double ) base, ( double ) wear, null, null, tool );
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
            if ( tool.getTypeId() == 267 && tool.getDurability() < 250 )
                return;

            if ( tool.getAmount() > 1 )
            {
                tool.setAmount( tool.getAmount() - 1 );
                tool.setDurability( ( short ) -1 );
            }
            else
            {
                player.getPlayer().setItemInHand( null );
            }
        }
    }

}
