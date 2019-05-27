/*
 * Copyright (c) 2018.
 *
 * DwarfCraft is an RPG plugin that allows players to improve their characters
 * skills and capabilities through training, not experience.
 *
 * Authors: Jessy1237 and Drekryan
 * Original Authors: smartaleq, LexManos and RCarretta
 */

package com.Jessy1237.DwarfCraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jbls.LexManos.CSV.CSVRecord;

import com.Jessy1237.DwarfCraft.data.DataManager;
import com.Jessy1237.DwarfCraft.models.DwarfItemHolder;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfRace;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;
import com.Jessy1237.DwarfCraft.models.DwarfTrainer;
import com.Jessy1237.DwarfCraft.models.DwarfTrainerTrait;

import net.citizensnpcs.api.npc.AbstractNPC;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Util
{

    private DwarfCraft plugin;

    public Util( DwarfCraft plugin )
    {
        this.plugin = plugin;
    }

    public void consoleLog( Level logLevel, String message )
    {
        ChatColor color = ChatColor.WHITE;

        if ( logLevel == Level.SEVERE )
            color = ChatColor.RED;
        else if ( logLevel == Level.WARNING )
            color = ChatColor.GOLD;
        else if ( logLevel == Level.FINE )
            color = ChatColor.LIGHT_PURPLE;

        plugin.getServer().getConsoleSender().sendMessage( ChatColor.YELLOW + "[" + plugin.getName() + "] " + color + message );
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

    public void sendPlayerMessage( DwarfPlayer dcPlayer, ChatMessageType type, String message )
    {
        sendPlayerMessage( dcPlayer.getPlayer(), type, message );
    }

    public void sendPlayerMessage( Player player, ChatMessageType type, String message )
    {
        player.spigot().sendMessage( type, new TextComponent( ChatColor.translateAlternateColorCodes( '&', message ) ) );
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

    /**
     * Gets the DwarfItemHolder from the csv record item data.
     * 
     * @param item The CSVRecord being read from
     * @param name The name of the Item being found
     * @return A dwarf item holder but can return an empty dwarf item holder
     */
    @SuppressWarnings( { "unchecked" } )
    public DwarfItemHolder getDwarfItemHolder( CSVRecord item, String name )
    {
        Set<Material> mats = new HashSet<>();
        Tag<Material> tag = null;
        String tagName = "";

        if ( item.getString( name ).startsWith( "#" ) )
        {
            tagName = item.getString( name ).substring( 1 ).toLowerCase();

            // Add missing vanilla wooden_fences, raw_fishes, and grass tags
            if ( tagName.equalsIgnoreCase( "wooden_fences" ) )
            {
                Material[] newMats = { Material.OAK_FENCE, Material.SPRUCE_FENCE, Material.BIRCH_FENCE, Material.JUNGLE_FENCE, Material.ACACIA_FENCE, Material.DARK_OAK_FENCE };
                tag = createDCTag( "wooden_fences", newMats );
            }
            else if ( tagName.equalsIgnoreCase( "raw_fishes" ) )
            {
                Material[] newMats = { Material.COD, Material.PUFFERFISH, Material.SALMON, Material.TROPICAL_FISH };
                tag = createDCTag( "raw_fishes", newMats );
            }
            else if ( tagName.equalsIgnoreCase( "grass" ) )
            {
                Material[] newMats = { Material.DEAD_BUSH, Material.GRASS, Material.TALL_GRASS, Material.FERN, Material.LARGE_FERN };
                tag = createDCTag( "grass", newMats );
            }

            if ( tag == null )
            {
                tag = Bukkit.getTag( Tag.REGISTRY_ITEMS, NamespacedKey.minecraft( tagName ), Material.class );
            }

            if ( tag == null )
            {
                tag = Bukkit.getTag( Tag.REGISTRY_BLOCKS, NamespacedKey.minecraft( tagName ), Material.class );
            }

            if ( tag != null )
            {
                mats = tag.getValues();
            }
        }
        else
        {
            mats.add( parseItem( item.getString( name ) ).getType() );
        }

        return new DwarfItemHolder( mats, tag, tagName );
    }

    @SuppressWarnings( "rawtypes" )
    private Tag createDCTag( String tagName, Material[] newMats )
    {
        Tag tag = new Tag() {
            @Override
            public NamespacedKey getKey()
            {
                return NamespacedKey.minecraft( tagName );
            }

            @Override
            public boolean isTagged( Keyed item )
            {
                Material mat = Material.matchMaterial( item.getKey().toString() );
                return getValues().contains( mat );
            }

            @Override
            public Set getValues()
            {
                return new HashSet<>( Arrays.asList( newMats ) );
            }
        };

        return tag;
    }

    public ItemStack parseItem( String info )
    {
        Material mat = Material.getMaterial( info );
        if ( mat == null )
        {
            return new ItemStack( Material.AIR );
        }
        return new ItemStack( mat );
    }

    /**
     * Gets the official clean name of the Item Holder
     * 
     * @param dih The DwarfItemHolder to get the clean name of
     * @return A clean name for the Dwarf Item Holder
     */
    public String getCleanName( DwarfItemHolder dih )
    {
        return dih == null ? getCleanName( new ItemStack( Material.AIR ) ) : ( dih.isTagged() ? ( cleanEnumString( dih.getTagName() ) ) : ( getCleanName( dih.getItemStack() ) ) );
    }

    /**
     * Gets the official clean name of the item
     * 
     * @param item The item to get the clean name of
     * @return A clean name for the item if the item exists, otherwise returns AIR
     */
    public String getCleanName( ItemStack item )
    {
        if ( item == null )
            item = new ItemStack( Material.AIR );
        return cleanEnumString( item.getType().toString() );
    }

    /**
     * Checks the Material to see if it is a tool. Excludes fishing rod and, flint and steel.
     * 
     * @param mat The Material to be checked
     * @return True if the mat is a tool otherwise false
     */
    public boolean isTool( Material mat )
    {
        if ( mat == Material.IRON_SHOVEL || mat == Material.IRON_AXE || mat == Material.IRON_PICKAXE || mat == Material.IRON_SWORD || mat == Material.WOODEN_SWORD || mat == Material.WOODEN_SHOVEL || mat == Material.WOODEN_PICKAXE || mat == Material.WOODEN_AXE || mat == Material.STONE_SWORD
                || mat == Material.STONE_SHOVEL || mat == Material.STONE_PICKAXE || mat == Material.STONE_AXE || mat == Material.DIAMOND_SWORD || mat == Material.DIAMOND_SHOVEL || mat == Material.DIAMOND_PICKAXE || mat == Material.DIAMOND_AXE || mat == Material.GOLDEN_SWORD
                || mat == Material.GOLDEN_SHOVEL || mat == Material.GOLDEN_PICKAXE || mat == Material.GOLDEN_AXE || mat == Material.WOODEN_HOE || mat == Material.STONE_HOE || mat == Material.IRON_HOE || mat == Material.DIAMOND_HOE || mat == Material.GOLDEN_HOE || mat == Material.SHEARS )
        {
            return true;
        }
        return false;
    }

    public String getPlayerPrefix( DwarfPlayer player )
    {
        if ( player.getRace() == null || player.getRace().isEmpty() )
            return "";
        String race = player.getRace().substring( 0, 1 ).toUpperCase() + player.getRace().substring( 1 );
        String prefix = plugin.getConfigManager().getRace( race ).getPrefixColour();
        return plugin.getOut().parseColors( prefix + plugin.getConfigManager().getPrefix().replace( PlaceholderParser.PlaceHolder.RACE_NAME.getPlaceHolder(), race ) + "&f" );
    }

    public String getPlayerPrefix( String race )
    {
        if ( race == null || race.isEmpty() )
            return "";
        String raceStr = race.substring( 0, 1 ).toUpperCase() + race.substring( 1 );
        String prefix = plugin.getConfigManager().getRace( race ).getPrefixColour();
        return plugin.getOut().parseColors( prefix + plugin.getConfigManager().getPrefix().replace( PlaceholderParser.PlaceHolder.RACE_NAME.getPlaceHolder(), raceStr ) + "&f" );
    }

    public String getPlayerPrefixOldColours( String race )
    {
        if ( race == null || race.isEmpty() )
            return "";
        String raceStr = race.substring( 0, 1 ).toUpperCase() + race.substring( 1 );
        String prefix = plugin.getConfigManager().getRace( race ).getPrefixColour();
        return prefix + plugin.getConfigManager().getPrefix().replace( PlaceholderParser.PlaceHolder.RACE_NAME.getPlaceHolder(), raceStr ) + "&f";
    }

    public int getMaxLevelForSkill( DwarfPlayer dcPlayer, DwarfSkill skill )
    {
        int maxLevel = plugin.getConfigManager().getMaxSkillLevel();
        ArrayList<Integer> skills = plugin.getConfigManager().getAllSkills( dcPlayer.getRace() );
        if ( !skills.contains( skill.getId() ) )
        {
            maxLevel = plugin.getConfigManager().getRaceLevelLimit();
        }

        return maxLevel;
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
        {
            dm.createDwarfData( data );
        }

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
                    }

                    if ( plugin.getChat() != null && !plugin.getChat().getPlayerPrefix( player ).contains( plugin.getUtil().getPlayerPrefix( data ) ) )
                    {
                        plugin.getChat().setPlayerPrefix( player, plugin.getUtil().getPlayerPrefix( data ) + " " + plugin.getChat().getPlayerPrefix( player ) );
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
     * @return The clean name of the entity if the entity exists, otherwise returns an empty string
     */
    public String getCleanName( EntityType mCreature )
    {
        if ( mCreature == null )
            return "";

        return cleanEnumString( mCreature.toString() );
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
        CARROT( Material.CARROT, 3, 3.6f ),
        COOKED_BEEF( Material.COOKED_BEEF, 8, 12.8f ),
        CHORUS_FRUIT( Material.CHORUS_FRUIT, 4, 2.4f ),
        COOKED_CHICKEN( Material.COOKED_CHICKEN, 6, 7.2f ),
        COOKED_COD( Material.COOKED_COD, 5, 6f ),
        COOKED_SALMON( Material.COOKED_SALMON, 6, 9.6f ),
        COOKED_MUTTON( Material.COOKED_MUTTON, 6, 9.6f ),
        COOKED_PORKCHOP( Material.COOKED_PORKCHOP, 8, 12.8f ),
        COOKED_RABBIT( Material.COOKED_RABBIT, 5, 6f ),
        COOKIE( Material.COOKIE, 2, 0.4f ),
        DRIED_KELP( Material.DRIED_KELP, 1, 0.6f ),
        GOLDEN_APPLE( Material.GOLDEN_APPLE, 4, 9.6f ),
        ENCHANTED_GOLDEN_APPLE( Material.ENCHANTED_GOLDEN_APPLE, 4, 9.6f ),
        GOLDEN_CARROT( Material.GOLDEN_CARROT, 6, 14.4f ),
        MELON_SLICE( Material.MELON_SLICE , 2, 1.2f ),
        MUSHROOM_STEW( Material.MUSHROOM_STEW, 6, 7.2f ),
        POISONOUS_POTATO( Material.POISONOUS_POTATO, 2, 1.2f ),
        POTATO( Material.POTATO, 1, 0.6f ),
        PUMPKIN_PIE( Material.PUMPKIN_PIE, 8, 4.8f ),
        RABBIT_STEW( Material.RABBIT_STEW, 10, 12f ),
        BEEF( Material.BEEF, 3, 1.8f ),
        CHICKEN( Material.CHICKEN, 2, 1.2f ),
        COD( Material.COD, 2, 0.4f ),
        SALMON( Material.SALMON, 2, 0.4f ),
        TROPICAL_FISH( Material.TROPICAL_FISH, 1, 0.2f ),
        PUFFERFISH( Material.PUFFERFISH, 1, 0.2f ),
        MUTTON( Material.MUTTON, 2, 1.2f ),
        PORKCHOP( Material.PORKCHOP, 3, 1.8f ),
        RABBIT( Material.RABBIT, 2, 1.8f ),
        ROTTEN_FLESH( Material.ROTTEN_FLESH, 4, 0.8f ),
        SPIDER_EYE( Material.SPIDER_EYE, 2, 3.2f );

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
                            // Reruns the code that registers the CitizensNPC
                            // into DwarfCraft
                            npc.getTrait( DwarfTrainerTrait.class ).loadHeldItem();
                            DwarfTrainer trainer = new DwarfTrainer( plugin, ( AbstractNPC ) npc );
                            plugin.getDataManager().trainerList.put( npc.getId(), trainer );
                        }
                    }
                }
            }

        }
    }
}
