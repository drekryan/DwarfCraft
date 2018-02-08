package com.Jessy1237.DwarfCraft;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfRace;
import com.Jessy1237.DwarfCraft.models.DwarfTrainerTrait;

import net.citizensnpcs.api.npc.NPC;

public class Util
{

    private DwarfCraft plugin;

    public Util( DwarfCraft plugin )
    {
        this.plugin = plugin;
    }

    // Stolen from nossr50
    private int charLength( char x )
    {
        if ( "i.:,;|!".indexOf( x ) != -1 )
            return 2;
        else if ( "l'".indexOf( x ) != -1 )
            return 3;
        else if ( "tI[]".indexOf( x ) != -1 )
            return 4;
        else if ( "fk{}<>\"*()".indexOf( x ) != -1 )
            return 5;
        else if ( "abcdeghjmnopqrsuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ1234567890\\/#?$%-=_+&^".indexOf( x ) != -1 )
            return 6;
        else if ( "@~".indexOf( x ) != -1 )
            return 7;
        else if ( x == ' ' )
            return 4;
        else
            return -1;
    }

    protected int msgLength( String str )
    {
        int len = 0;

        for ( int i = 0; i < str.length(); i++ )
        {
            if ( str.charAt( i ) == '&' )
            {
                i++;
                continue; // increment by 2 for colors, as in the case of "&3"
            }
            len += charLength( str.charAt( i ) );
        }
        return len;
    }

    public int randomAmount( double input )
    {
        double rand = Math.random();
        if ( rand > input % 1 )
            return ( int ) Math.floor( input );
        else
            return ( int ) Math.ceil( input );
    }

    protected String sanitize( String str )
    {
        String retval = "";
        for ( int i = 0; i < str.length(); i++ )
        {
            if ( "abcdefghijlmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_".indexOf( str.charAt( i ) ) != -1 )
                retval = retval + str.charAt( i );
        }
        return retval;
    }

    public ItemStack parseItem( String info )
    {
        String[] pts = info.split( ":" );
        int data = ( pts.length > 1 ? Integer.parseInt( pts[1] ) : 0 );
        Material mat = Material.matchMaterial( pts[0] );
        if ( mat == null )
        {
            System.out.println( "DC ERROR: Could not parse material: " + info );
            return null;
        }
        ItemStack item1 = new ItemStack( mat );
        item1.setDurability( ( short ) data );
        return item1;
    }

    /**
     * Gets the official clean name of the item as spigot names are a bit iffy.
     * 
     * @param item The item to get the clean name of
     * @return A clean name for the item if the item exists othewise returns 'NULL'
     */
    public String getCleanName( ItemStack item )
    {
        if ( item == null )
            return "NULL";
        switch ( item.getType() )
        {
            case SAPLING:
                if ( checkEquivalentBuildBlocks( item.getType(), null ) != null )
                    return "Sapling";
                switch ( item.getDurability() )
                {
                    case 0:
                        return "Oak Sapling";
                    case 1:
                        return "Spruce Sapling";
                    case 2:
                        return "Birch Sapling";
                    case 3:
                        return "Jungle Sapling";
                    case 4:
                        return "Acacia Sapling";
                    case 5:
                        return "Dark Oak Sapling";
                    default:
                        return "Sapling";
                }
            case SAND:
                switch ( item.getDurability() )
                {
                    case 0:
                        return "Sand";
                    case 1:
                        return "Red Sand";
                    default:
                        return "Sand";
                }
            case RAW_FISH:
                if ( checkEquivalentBuildBlocks( item.getType(), null ) != null )
                    return "Raw Fish";
                switch ( item.getDurability() )
                {
                    case 0:
                        return "Raw Fish";
                    case 1:
                        return "Raw Salmon";
                    case 2:
                        return "Clownfish";
                    case 3:
                        return "Pufferfish";
                    default:
                        return "Raw Fish";
                }

            case LOG:
                if ( checkEquivalentBuildBlocks( item.getType(), null ) != null )
                    return "Log";
                switch ( item.getDurability() )
                {
                    case 0:
                        return "Oak Log";
                    case 1:
                        return "Spruce Log";
                    case 2:
                        return "Birch Log";
                    case 3:
                        return "Jungle Tree Log";
                    default:
                        return "Log";
                }
            case LOG_2:
                if ( checkEquivalentBuildBlocks( item.getType(), null ) != null )
                    return "Log";
                switch ( item.getDurability() )
                {
                    case 0:
                        return "Acacia Log";
                    case 1:
                        return "Dark Oak Log";
                    default:
                        return "Log";
                }
            case LEAVES:
                if ( checkEquivalentBuildBlocks( item.getType(), null ) != null )
                    return "Leaves";
                switch ( item.getDurability() )
                {
                    case 0:
                        return "Oak Leaves";
                    case 1:
                        return "Spruce Leaves";
                    case 2:
                        return "Birch Leaves";
                    case 3:
                        return "Jungle Tree Leaves";
                    default:
                        return "Leaves";
                }
            case LEAVES_2:
                if ( checkEquivalentBuildBlocks( item.getType(), null ) != null )
                    return "Leaves";
                switch ( item.getDurability() )
                {
                    case 0:
                        return "Acacia Leaves";
                    case 1:
                        return "Dark Oak Leaves";
                    default:
                        return "Leaves";
                }
            case WOOL:
                if ( checkEquivalentBuildBlocks( item.getType(), null ) != null )
                    return "Wool";
                switch ( item.getDurability() )
                {
                    case 0:
                        return "White Wool";
                    case 1:
                        return "Orange Dye";
                    case 2:
                        return "Magenta Dye";
                    case 3:
                        return "Light Blue Dye";
                    case 4:
                        return "Dandelion Yellow";
                    case 5:
                        return "Lime Dye";
                    case 6:
                        return "Pink Dye";
                    case 7:
                        return "Gray Dye";
                    case 8:
                        return "Light Gray Dye";
                    case 9:
                        return "Cyan Dye";
                    case 10:
                        return "Purple Dye";
                    case 11:
                        return "Lapis Lazuli";
                    case 12:
                        return "Cocoa Beans";
                    case 13:
                        return "Cactus Green";
                    case 14:
                        return "Rose Red";
                    case 15:
                        return "Ink Sac";
                    default:
                        return String.format( "Unknown Dye(%d)", item.getDurability() );
                }
            case DOUBLE_STEP:
                if ( checkEquivalentBuildBlocks( item.getType(), null ) != null )
                    return "Slab";
                switch ( item.getDurability() )
                {
                    case 15:
                        return "Tile Quartz Double Slab";
                    case 9:
                        return "Smooth Sandstone Double Slab";
                    case 8:
                        return "Smooth Stone Double Slab";
                    case 7:
                        return "Quarts Double Slab";
                    case 6:
                        return "Nether Brick Double Slab";
                    case 5:
                        return "Stone Brick Double Slab";
                    case 4:
                        return "Brick Double Slab";
                    case 3:
                        return "Cobblestone Double Slab";
                    case 2:
                        return "Wooden Double Slab";
                    case 1:
                        return "Sandstone Double Slab";
                    case 0:
                        return "Stone Double Slab";
                    default:
                        return String.format( "Slab" );
                }
            case SUGAR_CANE_BLOCK:
                return "Sugar Cane";
            case CROPS:
                switch ( item.getDurability() )
                {
                    case 7:
                        return "Fully Grown Crops";
                    default:
                        return String.format( "Crop" );
                }
            case COAL:
                if ( checkEquivalentBuildBlocks( item.getType(), null ) != null )
                    return "Coal";
                switch ( item.getDurability() )
                {
                    case 0:
                        return "Coal";
                    case 1:
                        return "Charcoal";
                    default:
                        return "Coal";
                }
            case SULPHUR:
                return "Gun Powder";
            case NETHER_STALK:
                return "Nether Wart";
            case NETHER_WARTS:
                return "Nether Wart";
            case POTATO_ITEM:
                return "Potato";
            case POTATO:
                return "Potato Crop";
            case CARROT_ITEM:
                return "Carrot";
            case CARROT:
                return "Carrot Crop";
            case INK_SACK:
                if ( checkEquivalentBuildBlocks( item.getType(), null ) != null )
                    return "Dye";
                switch ( item.getDurability() )
                {
                    case 15:
                        return "Bone Meal";
                    case 14:
                        return "Orange Dye";
                    case 13:
                        return "Magenta Dye";
                    case 12:
                        return "Light Blue Dye";
                    case 11:
                        return "Dandelion Yellow";
                    case 10:
                        return "Lime Dye";
                    case 9:
                        return "Pink Dye";
                    case 8:
                        return "Gray Dye";
                    case 7:
                        return "Light Gray Dye";
                    case 6:
                        return "Cyan Dye";
                    case 5:
                        return "Purple Dye";
                    case 4:
                        return "Lapis Lazuli";
                    case 3:
                        return "Cocoa Beans";
                    case 2:
                        return "Cactus Green";
                    case 1:
                        return "Rose Red";
                    case 0:
                        return "Ink Sac";
                    default:
                        return String.format( "Unknown Dye(%d)", item.getDurability() );
                }
            default:
                return cleanEnumString( item.getType().toString().replaceAll( "_", " " ) );
        }
    }

    // Checks the itemID to see if it is a tool. Excludes fishing rod and,
    // flint
    // and steel.

    /**
     * Checks the Material to see if it is a tool. Excludes fishing rod and, flint and steel.
     * 
     * @param mat The Material to be checked
     * @return True if the mat is a tool otherwise false
     */
    public boolean isTool( Material mat )
    {
        if ( mat == Material.IRON_SPADE || mat == Material.IRON_AXE || mat == Material.IRON_PICKAXE || mat == Material.IRON_SWORD || mat == Material.WOOD_SWORD || mat == Material.WOOD_SPADE || mat == Material.WOOD_PICKAXE || mat == Material.WOOD_AXE || mat == Material.STONE_SWORD
                || mat == Material.STONE_SPADE || mat == Material.STONE_PICKAXE || mat == Material.STONE_AXE || mat == Material.DIAMOND_SWORD || mat == Material.DIAMOND_SPADE || mat == Material.DIAMOND_PICKAXE || mat == Material.DIAMOND_AXE || mat == Material.GOLD_SWORD || mat == Material.GOLD_SPADE
                || mat == Material.GOLD_PICKAXE || mat == Material.GOLD_AXE || mat == Material.WOOD_HOE || mat == Material.STONE_HOE || mat == Material.IRON_HOE || mat == Material.DIAMOND_HOE || mat == Material.GOLD_HOE || mat == Material.SHEARS )
        {
            return true;
        }
        return false;
    }

    /**
     * Checks the supplied itemID and sees if it is equivalent to the comparable itemID. The equivalent blocks are set in a config file as a list.
     * 
     * @param mat The main Material to obtain the list of comparable Materials
     * @param compareMat The Material to check if it is in the list of comparableMaterials
     * @return A list of all the equivalent Materials if the comparableMaterial is equivalent to the Material. If the compareMat is null then the list of equivalent Materials to the main Material is
     *         returned. Otherwise null is returned
     */
    public ArrayList<Material> checkEquivalentBuildBlocks( Material mat, Material compareMat )
    {
        if ( !plugin.getConfigManager().buildingblocks )
            return null;

        for ( ArrayList<Material> blocks : plugin.getConfigManager().getBlockGroups().values() )
        {
            if ( blocks != null && blocks.size() > 0 )
            {
                for ( Material m : blocks )
                {
                    if ( mat == m )
                    {
                        for ( Material m1 : blocks )
                        {
                            if ( compareMat == m1 || compareMat == null )
                            {
                                return blocks;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public String getPlayerPrefix( DwarfPlayer player )
    {
        String race = player.getRace().substring( 0, 1 ).toUpperCase() + player.getRace().substring( 1 );
        return plugin.getOut().parseColors( plugin.getConfigManager().getRace( race ).getPrefixColour() + plugin.getConfigManager().getPrefix().replace( "%racename%", race ) + "&f" );
    }

    public String getPlayerPrefix( String race )
    {
        String raceStr = race.substring( 0, 1 ).toUpperCase() + race.substring( 1 );
        return plugin.getOut().parseColors( plugin.getConfigManager().getRace( raceStr ).getPrefixColour() + plugin.getConfigManager().getPrefix().replace( "%racename%", raceStr ) + "&f" );
    }

    public String getPlayerPrefixOldColours( String race )
    {
        String raceStr = race.substring( 0, 1 ).toUpperCase() + race.substring( 1 );
        return plugin.getConfigManager().getRace( raceStr ).getPrefixColour() + plugin.getConfigManager().getPrefix().replace( "%racename%", raceStr ) + "&f";
    }

    public void removePlayerPrefixes()
    {
        // Removes the DwarfCraft prefixes when the server shuts down for all
        // players with the old colours method and new colours method.
        for ( OfflinePlayer op : plugin.getServer().getOfflinePlayers() )
        {
            if ( op == null )
                continue;

            if ( plugin.isChatEnabled() )
            {
                for ( World w : plugin.getServer().getWorlds() )
                {
                    if ( w == null )
                        continue;

                    for ( DwarfRace race : plugin.getConfigManager().getRaceList() )
                    {
                        if ( race == null )
                            continue;

                        String raceStr = race.getName();
                        String prefix = plugin.getChat().getPlayerPrefix( w.getName(), op );

                        if ( prefix == null )
                            continue;
                        if ( prefix.equals( "" ) )
                            continue;
                        while ( plugin.getChat().getPlayerPrefix( w.getName(), op ).contains( getPlayerPrefix( raceStr ) ) )
                        {
                            prefix = plugin.getChat().getPlayerPrefix( w.getName(), op );
                            prefix = prefix.replace( getPlayerPrefix( raceStr ) + " ", "" );
                            plugin.getChat().setPlayerPrefix( w.getName(), op, prefix );
                        }

                        while ( plugin.getChat().getPlayerPrefix( w.getName(), op ).contains( getPlayerPrefixOldColours( raceStr ) ) )
                        {
                            prefix = plugin.getChat().getPlayerPrefix( w.getName(), op );
                            prefix = prefix.replace( getPlayerPrefixOldColours( raceStr ) + " ", "" );
                            plugin.getChat().setPlayerPrefix( w.getName(), op, prefix );
                        }
                    }
                }
            }
        }
    }

    public void setPlayerPrefix( Player player )
    {
        DataManager dm = plugin.getDataManager();
        DwarfPlayer data = dm.find( player );

        if ( data == null )
            data = dm.createDwarf( player );
        if ( !dm.checkDwarfData( data ) )
            dm.createDwarfData( data );

        if ( plugin.isChatEnabled() )
        {
            if ( plugin.getConfigManager().prefix )
            {

                String prefix = plugin.getChat().getPlayerPrefix( player );

                if ( prefix != null )
                {
                    if ( !prefix.equals( "" ) )
                    {
                        while ( plugin.getChat().getPlayerPrefix( player ).contains( plugin.getUtil().getPlayerPrefix( data ) ) )
                        {
                            prefix = plugin.getChat().getPlayerPrefix( player );
                            prefix = prefix.replace( plugin.getUtil().getPlayerPrefix( data ) + " ", "" );
                            plugin.getChat().setPlayerPrefix( player, prefix );
                        }

                        if ( !plugin.getChat().getPlayerPrefix( player ).contains( plugin.getUtil().getPlayerPrefix( data ) ) )
                        {
                            plugin.getChat().setPlayerPrefix( player, plugin.getUtil().getPlayerPrefix( data ) + " " + plugin.getChat().getPlayerPrefix( player ) );
                        }
                    }
                }
            }
            else
            {
                String prefix = plugin.getChat().getPlayerPrefix( player );

                if ( prefix != null )
                    if ( !prefix.equals( "" ) )
                        while ( plugin.getChat().getPlayerPrefix( player ).contains( plugin.getUtil().getPlayerPrefix( data ) ) )
                        {
                            prefix = plugin.getChat().getPlayerPrefix( player );
                            prefix = prefix.replace( plugin.getUtil().getPlayerPrefix( data ) + " ", "" );
                            plugin.getChat().setPlayerPrefix( player, prefix );
                        }
            }
        }
    }

    /**
     * Gets the clean name of the Entity.
     * 
     * @param mCreature The creature to get the clean name of
     * @return The clean name of the entity if the entity exists otherwise returns 'NULL'
     */
    public String getCleanName( EntityType mCreature )
    {
        if ( mCreature == null )
            return "NULL";

        switch ( mCreature )
        {
            case MUSHROOM_COW:
                return "Mooshroom";
            case IRON_GOLEM:
                return "Iron Golem";
            case MAGMA_CUBE:
                return "Magma Cube";
            case ENDER_DRAGON:
                return "Ender Dragon";
            case WITHER_SKULL:
                return "Wither Skull";
            case PIG_ZOMBIE:
                return "Pig Zombie";
            case CAVE_SPIDER:
                return "Cave Spider";
            case WITHER:
                return "Wither";
            case OCELOT:
                return "Ocelot";
            default:
                return cleanEnumString( mCreature.toString() );
        }
    }

    public String cleanEnumString( String enumStr )
    {
        String enumString = enumStr.toLowerCase();
        String[] enumWords = enumString.split( "_" );

        StringBuffer sb = new StringBuffer();
        for ( String word : enumWords )
        {
            sb.append( word.substring( 0, 1 ).toUpperCase() + word.substring( 1 ) );
            sb.append( " " );
        }

        return sb.toString().trim();
    }

    public enum FoodLevel
    {
        APPLE( Material.APPLE, 4, 2.4f ),
        BAKED_POTATO( Material.BAKED_POTATO, 5, 6.0f ),
        BEETROOT( Material.BEETROOT, 1, 1.2f ),
        BEETROOT_SOUP( Material.BEETROOT_SOUP, 6, 7.2f ),
        BREAD( Material.BREAD, 5, 6f ),
        CAKE( Material.CAKE, 2, 0.4f ),
        CARROT( Material.CARROT_ITEM, 3, 3.6f ),
        CHORUS_FRUIT( Material.CHORUS_FRUIT, 4, 2.4f ),
        COOKED_CHICKEN( Material.COOKED_CHICKEN, 6, 7.2f ),
        COOKED_FISH( Material.COOKED_FISH, 5, 6f ),
        COOKED_MUTTON( Material.COOKED_MUTTON, 6, 9.6f ),
        COOKED_PORKCHOP( Material.GRILLED_PORK, 8, 12.8f ),
        COOKED_RABBIT( Material.COOKED_RABBIT, 5, 6f ),
        COOKIE( Material.COOKIE, 2, 0.4f ),
        GOLDEN_APPLE( Material.GOLDEN_APPLE, 4, 9.6f ),
        GOLDEN_CARROT( Material.GOLDEN_CARROT, 6, 14.4f ),
        MELON( Material.MELON, 2, 1.2f ),
        MUSHROOM_STEW( Material.MUSHROOM_SOUP, 6, 7.2f ),
        POISONOUS_POTATO( Material.POISONOUS_POTATO, 2, 1.2f ),
        POTATO( Material.POTATO_ITEM, 1, 0.6f ),
        PUMPKIN_PIE( Material.PUMPKIN_PIE, 8, 4.8f ),
        RABBIT_STEW( Material.RABBIT_STEW, 10, 12f ),
        RAW_BEEF( Material.RAW_BEEF, 3, 1.8f ),
        RAW_CHICKEN( Material.RAW_CHICKEN, 2, 1.2f ),
        RAW_FISH( Material.RAW_FISH, 2, 0.4f ),
        RAW_MUTTON( Material.MUTTON, 2, 1.2f ),
        RAW_PORKCHOP( Material.PORK, 3, 1.8f ),
        RAW_RABBIT( Material.RABBIT, 2, 1.8f ),
        ROTTEN_FLESH( Material.ROTTEN_FLESH, 4, 0.8f ),
        SPIDER_EYE( Material.SPIDER_EYE, 2, 3.2f ),
        STEAK( Material.COOKED_BEEF, 8, 12.8f );

        private int lvl;
        private Material mat;
        private float sat;

        private FoodLevel( Material mat, int lvl, float sat )
        {
            this.mat = mat;
            this.lvl = lvl;
            this.sat = sat;
        }

        public Material getMaterial()
        {
            return mat;
        }

        public int getLevel()
        {
            return this.lvl;
        }

        public float getSat()
        {
            return this.sat;
        }

        public static int getLvl( Material mat )
        {
            for ( FoodLevel f : FoodLevel.values() )
            {
                if ( f != null )
                {
                    if ( f.getMaterial() == mat )
                    {
                        return f.getLevel();
                    }
                }
            }
            return 0;
        }

        public float getSat( Material mat )
        {
            for ( FoodLevel f : FoodLevel.values() )
            {
                if ( f != null )
                {
                    if ( f.getMaterial() == mat )
                    {
                        return f.getSat();
                    }
                }
            }
            return 0;
        }
    }

    public boolean isWorldAllowed( World world )
    {
        if ( plugin.getConfigManager().worldBlacklist )
        {
            for ( World w : plugin.getConfigManager().worlds )
            {
                if ( w != null )
                {
                    if ( world.equals( w ) )
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void reloadTrainers()
    {
        // Reloads the DwarfTrainer information without reloading citizens
        for ( World w : plugin.getServer().getWorlds() )
        {
            if ( isWorldAllowed( w ) )
            {
                for ( Entity e : w.getEntities() )
                {
                    if ( plugin.getNPCRegistry().isNPC( e ) )
                    {
                        NPC npc = plugin.getNPCRegistry().getNPC( e );

                        if ( npc.hasTrait( DwarfTrainerTrait.class ) )
                        {
                            DwarfTrainerTrait dtt = npc.getTrait( DwarfTrainerTrait.class );

                            // Reruns the code that registers the CitizensNPC
                            // into DwarfCraft
                            dtt.onSpawn();
                        }
                    }
                }
            }

        }
    }
}
