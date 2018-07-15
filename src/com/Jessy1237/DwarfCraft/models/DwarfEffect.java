package com.Jessy1237.DwarfCraft.models;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Mule;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;
import org.jbls.LexManos.CSV.CSVRecord;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.Messages;
import com.Jessy1237.DwarfCraft.events.DwarfEffectEvent;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */
public class DwarfEffect
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
    private DwarfEffectType mType;
    private ItemStack mInitiator;
    private ItemStack mOutput;
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
            mInitiator = plugin.getUtil().parseItem( record.getString( "OriginMaterial" ) );
        }
        else
        {
            mCreature = EntityType.valueOf( record.getString( "OriginMaterial" ) );
        }
        mOutput = plugin.getUtil().parseItem( record.getString( "OutputMaterial" ) );
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
                Material mat = Material.matchMaterial( stools[x] );
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
     * General description of a benefit including minimum and maximum benefit
     * 
     * @return
     */
    public String describeGeneral( DwarfPlayer dCPlayer )
    {
        String description;
        String initiator = plugin.getUtil().getCleanName( mInitiator );
        if ( initiator.equalsIgnoreCase( "AIR" ) )
            initiator = "None";
        String output = plugin.getUtil().getCleanName( mOutput );
        if ( output.equalsIgnoreCase( "AIR" ) )
            output = "None";
        double effectAmountLow = getEffectAmount( 0, dCPlayer );
        double effectAmountHigh = getEffectAmount( plugin.getConfigManager().getMaxSkillLevel(), dCPlayer );
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
    public String describeLevel( DwarfPlayer dCPlayer )
    {
        if ( dCPlayer == null )
            return "Failed"; // TODO add failure code

        String description = "no skill description";
        description = plugin.getOut().parseEffectLevel( dCPlayer, this );

        return description;
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
     * @param dCPlayer
     * @return
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
            System.out.println( String.format( "DC1: GetEffectAmmount ID: %d Level: %d Base: %.2f Increase: %.2f Novice: %.2f Max: %.2f Min: %.2f "
                    + "Exception: %s Exctpion Low: %.2f Exception High: %.2f Exception Value: %.2f Floor Result: %s", mID, skillLevel, mBase, mLevelIncrease, mLevelIncreaseNovice, mMax, mMin, mException, mExceptionLow, mExceptionHigh, mExceptionValue, mFloorResult ) );
        }
        return ( mFloorResult ? Math.floor( effectAmount ) : effectAmount );
    }

    public DwarfEffectType getEffectType()
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

    public Material getInitiatorMaterial()
    {
        return ( mInitiator == null ? null : mInitiator.getType() );
    }

    public Material getOutputMaterial()
    {
        return ( mOutput == null ? null : mOutput.getType() );
    }

    public ItemStack getInitiator()
    {
        return mInitiator;
    }

    public ItemStack getOutput()
    {
        return mOutput;
    }

    public ItemStack getOutput( DwarfPlayer player )
    {
        return getOutput( player, ( short ) 0, Material.AIR );
    }

    public ItemStack getOutput( DwarfPlayer player, Short oldData )
    {
        return getOutput( player, oldData, Material.AIR );
    }

    public ItemStack getOutput( DwarfPlayer player, Short oldData, Material oldMat )
    {
        short data = mOutput.getDurability();

        if ( data == 0 )
            data = oldData;

        final int count = plugin.getUtil().randomAmount( getEffectAmount( player ) );
        ItemStack item = null;
        if ( plugin.getUtil().checkEquivalentBuildBlocks( mOutput.getType(), oldMat ) == null || oldMat == null || oldMat == Material.AIR )
        {
            item = new ItemStack( mOutput.getType(), count, data );
        }
        else
        {
            item = new ItemStack( oldMat, count, data );
        }
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
            return checkInitiator( Material.AIR, ( short ) 0 );
        else
            return checkInitiator( item.getType(), item.getDurability() );
    }

    public boolean checkInitiator( Material mat, Short data )
    {
        if ( mInitiator.getType() != mat && plugin.getUtil().checkEquivalentBuildBlocks( mat, mInitiator.getType() ) == null )
            return false;

        if ( mInitiator.getDurability() != 0 )
        {
            return mInitiator.getDurability() == data;
        }
        return true;
    }

    /**
     * Tool to string parser for effect descriptions
     * 
     * @return
     */
    public String toolType()
    {
        for ( Material mat : mTools )
        {
            if ( mat == Material.IRON_SWORD )
                return "swords";
            if ( mat == Material.IRON_HOE )
                return "hoes";
            if ( mat == Material.IRON_AXE )
                return "axes";
            if ( mat == Material.WOOD_PICKAXE )
                return "pickaxes";
            if ( mat == Material.IRON_PICKAXE )
                return "most picks";
            if ( mat == Material.DIAMOND_PICKAXE )
                return "high picks";
            if ( mat == Material.IRON_SPADE )
                return "shovels";
            if ( mat == Material.FISHING_ROD )
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
            case DONKEY:
                return ( entity instanceof Donkey );
            case MULE:
                return ( entity instanceof Mule );
            case LLAMA:
                return ( entity instanceof Llama );
            case HUSK:
                return ( entity instanceof Husk );
            case SKELETON_HORSE:
                return ( entity instanceof SkeletonHorse );
            case ELDER_GUARDIAN:
                return ( entity instanceof ElderGuardian );
            case EVOKER:
                return ( entity instanceof Evoker );
            case STRAY:
                return ( entity instanceof Stray );
            case ZOMBIE_VILLAGER:
                return ( entity instanceof ZombieVillager );
            case VEX:
                return ( entity instanceof Vex );
            case VINDICATOR:
                return ( entity instanceof Vindicator );
            case ILLUSIONER:
                return ( entity instanceof Illusioner );
            case PARROT:
                return ( entity instanceof Parrot );
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
