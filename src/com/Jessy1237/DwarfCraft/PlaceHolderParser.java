package com.Jessy1237.DwarfCraft;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Jessy1237.DwarfCraft.models.DwarfEffect;
import com.Jessy1237.DwarfCraft.models.DwarfEffectType;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;

import me.clip.placeholderapi.external.EZPlaceholderHook;

public class PlaceHolderParser
{

    private DwarfCraft plugin;
    private PlaceholderHook placeholderHook;

    public PlaceHolderParser( DwarfCraft plugin )
    {
        this.plugin = plugin;
    }

    public enum PlaceHolder
    {
        COLON( "%colon%" ),
        EFFECT_AMOUNT( "%effectamount%" ),
        EFFECT_AMOUNT_DIG( "%effectamountdig%" ),
        EFFECT_AMOUNT_FOOD( "%effectamountfood%" ),
        EFFECT_AMOUNT_FOOD_ORIGINAL( "%originalfoodlevel%" ),
        EFFECT_AMOUNT_HIGH( "%effectamounthigh%" ),
        EFFECT_AMOUNT_INT( "%effectamountint%" ),
        EFFECT_AMOUNT_LOW( "%effectamountlow%" ),
        EFFECT_AMOUNT_MINOR( "%minoramount%" ),
        EFFECT_AMOUNT_NORMAL( "%normallevel%" ),
        EFFECT_CREATURE_NAME( "%creaturename%" ),
        EFFECT_DAMAGE( "%effectdamage%" ),
        EFFECT_DAMAGE_BOW( "%effectbowdamage%" ),
        EFFECT_DAMAGE_TAKEN( "%effecttakedamage%" ),
        EFFECT_ID( "%effectid%" ),
        EFFECT_INITIATOR( "%initiator%" ),
        EFFECT_OUTPUT( "%output%" ),
        EFFECT_TOOL_TYPE( "%tooltype%" ),
        EFFECT_LEVEL_COLOR( "%effectlevelcolor%" ),
        ITEM_NAME( "%itemname%" ),
        MAX_SKILL_LEVEL( "%maxskilllevel%" ),
        PLAYER_LEVEL( "%playerlevel%" ),
        PLAYER_NAME( "%playername%" ),
        PLAYER_RACE( "%playerrace%" ),
        RACE_LEVEL_LIMIT( "%racelevellimit%" ),
        RACE_NAME( "%racename%" ),
        SKILL_COST_AMOUNT( "%costamount%" ),
        SKILL_DEPOSIT_AMOUNT( "%depositedamount%" ),
        SKILL_ID( "%skillid%" ),
        SKILL_TOTAL_COST( "%totalcost%" ),
        SKILL_ITEM_TYPE( "%itemtype%" ),
        SKILL_LEVEL( "%skilllevel%" ),
        SKILL_LEVEL_NEXT( "%nextskilllevel%" ),
        SKILL_NAME( "%skillname%" );

        PlaceHolder( String placeHolder )
        {
            this.placeHolder = placeHolder;
        }

        private String placeHolder;

        public String getPlaceHolder()
        {
            return placeHolder;
        }
    }

    public String generalParse( String text )
    {
        return text.replaceAll( PlaceHolder.COLON.getPlaceHolder(), ":" ).replaceAll( PlaceHolder.MAX_SKILL_LEVEL.getPlaceHolder(), "" + plugin.getConfigManager().getMaxSkillLevel() ).replaceAll( PlaceHolder.RACE_LEVEL_LIMIT.getPlaceHolder(), "" + plugin.getConfigManager().getRaceLevelLimit() );
    }

    public String parseByDwarfEffect( String text, DwarfEffect effect )
    {
        String origFoodLevel = "";
        if ( effect.getInitiatorMaterial() != null )
            origFoodLevel = String.format( "%.2f", ( ( double ) Util.FoodLevel.getLvl( effect.getInitiatorMaterial() ) ) / 2.0 );

        String initiator = plugin.getUtil().getCleanName( effect.getInitiator() );

        // TODO: use checkEquivalentBlocks to future proof the 1.13 changes
        if ( effect.getEffectType() == DwarfEffectType.SMELT )
        {
            if ( effect.getInitiatorMaterial() == Material.IRON_INGOT )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.IRON_ORE ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.GOLD_INGOT )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.GOLD_ORE ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.GRILLED_PORK )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.PORK ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.COOKED_FISH )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.RAW_FISH ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.COOKED_CHICKEN )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.RAW_CHICKEN ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.COOKED_RABBIT )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.RABBIT ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.COOKED_MUTTON )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.MUTTON ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.COOKED_BEEF )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.RAW_BEEF ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.GLASS )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.SAND ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.BAKED_POTATO )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.POTATO_ITEM ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.COAL )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.LOG ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.STONE )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.COBBLESTONE ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.BRICK )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.CLAY_BALL ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.NETHER_BRICK )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.NETHERRACK ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.HARD_CLAY )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.CLAY ) );
            }
            else if ( effect.getInitiatorMaterial() == Material.INK_SACK )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.CACTUS ) );
            }

            else if ( effect.getInitiatorMaterial() == Material.WHITE_GLAZED_TERRACOTTA || plugin.getUtil().checkEquivalentBuildBlocks( effect.getInitiatorMaterial(), Material.WHITE_GLAZED_TERRACOTTA ) != null )
            {
                initiator = plugin.getUtil().getCleanName( new ItemStack( Material.HARD_CLAY ) );
            }
        }

        return generalParse( text.replaceAll( PlaceHolder.EFFECT_AMOUNT_FOOD_ORIGINAL.getPlaceHolder(), origFoodLevel ).replaceAll( PlaceHolder.EFFECT_INITIATOR.getPlaceHolder(), initiator ).replaceAll( PlaceHolder.EFFECT_OUTPUT.getPlaceHolder(), plugin.getUtil().getCleanName( effect.getOutput() ) )
                .replaceAll( PlaceHolder.EFFECT_TOOL_TYPE.getPlaceHolder(), effect.toolType() ).replaceAll( PlaceHolder.EFFECT_AMOUNT_NORMAL.getPlaceHolder(), "" + effect.getNormalLevel() ).replaceAll( PlaceHolder.EFFECT_ID.getPlaceHolder(), "" + effect.getId() ) );
    }

    public String parseByDwarfPlayerAndDwarfEffect( String text, DwarfPlayer dwarfPlayer, DwarfEffect effect )
    {
        double effectAmount = effect.getEffectAmount( dwarfPlayer );
        String effectLevelColor = effect.effectLevelColor( dwarfPlayer.getSkill( effect ).getLevel() );
        double minorAmount = effect.getEffectAmount( effect.getNormalLevel(), null );
        String minorAmountStr;
        double effectAmountLow = effect.getEffectAmount( 0, dwarfPlayer );
        double effectAmountHigh = effect.getEffectAmount( plugin.getConfigManager().getMaxSkillLevel(), dwarfPlayer );
        if ( effect.getEffectType() == DwarfEffectType.CRAFT )
        {
            minorAmountStr = String.format( "%.0f", minorAmount );
        }
        else
        {
            minorAmountStr = String.format( "%.2f", minorAmount );
        }

        return parseByDwarfEffect( text.replaceAll( PlaceHolder.EFFECT_AMOUNT_DIG.getPlaceHolder(), String.format( "%.0f", +( effectAmount * 100 ) ) ).replaceAll( PlaceHolder.EFFECT_DAMAGE_BOW.getPlaceHolder(), String.format( "%.0f", ( effectAmount + 2 ) ) )
                .replaceAll( PlaceHolder.EFFECT_AMOUNT_INT.getPlaceHolder(), "" + ( int ) effectAmount ).replaceAll( PlaceHolder.EFFECT_DAMAGE.getPlaceHolder(), "" + ( int ) ( effectAmount * 100 ) ).replaceAll( PlaceHolder.EFFECT_DAMAGE_TAKEN.getPlaceHolder(), "" + ( int ) ( effectAmount * 100 ) )
                .replaceAll( PlaceHolder.EFFECT_LEVEL_COLOR.getPlaceHolder(), effectLevelColor ).replaceAll( PlaceHolder.EFFECT_AMOUNT_MINOR.getPlaceHolder(), minorAmountStr ).replaceAll( PlaceHolder.EFFECT_AMOUNT_FOOD.getPlaceHolder(), String.format( "%.2f", ( effectAmount / 2.0 ) ) )
                .replaceAll( PlaceHolder.EFFECT_AMOUNT_LOW.getPlaceHolder(), String.format( "%.2f", effectAmountLow ) ).replaceAll( PlaceHolder.EFFECT_AMOUNT_HIGH.getPlaceHolder(), String.format( "%.2f", effectAmountHigh ) )
                .replaceAll( PlaceHolder.EFFECT_AMOUNT.getPlaceHolder(), String.format( "%.2f", effectAmount ) ), effect );
    }

    public String parseByDwarfSkill( String text, DwarfSkill skill )
    {
        return generalParse( text.replaceAll( PlaceHolder.SKILL_ID.getPlaceHolder(), "" + skill.getId() ).replaceAll( PlaceHolder.SKILL_NAME.getPlaceHolder(), skill.getDisplayName() ).replaceAll( PlaceHolder.SKILL_LEVEL.getPlaceHolder(), "" + skill.getLevel() )
                .replaceAll( PlaceHolder.SKILL_LEVEL_NEXT.getPlaceHolder(), "" + ( skill.getLevel() + 1 ) ) );
    }

    public String parseByDwarfPlayer( String text, DwarfPlayer dwarfPlayer )
    {
        return generalParse( text.replaceAll( PlaceHolder.PLAYER_LEVEL.getPlaceHolder(), "" + dwarfPlayer.getDwarfLevel() ).replaceAll( PlaceHolder.PLAYER_NAME.getPlaceHolder(), dwarfPlayer.getPlayer().getDisplayName() )
                .replaceAll( PlaceHolder.PLAYER_RACE.getPlaceHolder(), dwarfPlayer.getRace() ) );
    }

    public String parseByDwarfPlayerAndDwarfSkill( String text, DwarfPlayer dwarfPlayer, DwarfSkill skill )
    {
        // Calculate max level limit for skill. Checks to see if the players race specializes in the skill to see if skill should be locked to level cap.
        int levelLimit = plugin.getConfigManager().getMaxSkillLevel();
        ArrayList<Integer> skills = plugin.getConfigManager().getAllSkills( dwarfPlayer.getRace() );
        if ( skills != null )
        {
            if ( !skills.contains( skill.getId() ) )
            {
                levelLimit = plugin.getConfigManager().getRaceLevelLimit();
            }
        }

        return parseByDwarfSkill( parseByDwarfPlayer( text.replaceAll( PlaceHolder.MAX_SKILL_LEVEL.getPlaceHolder(), "" + levelLimit ), dwarfPlayer ), skill );
    }

    public String parseForTrainCosts( String text, int deposited, int costAmount, int totalCost, String itemType )
    {
        return generalParse( text.replaceAll( PlaceHolder.SKILL_DEPOSIT_AMOUNT.getPlaceHolder(), "" + deposited ).replaceAll( PlaceHolder.SKILL_TOTAL_COST.getPlaceHolder(), "" + totalCost ).replaceAll( PlaceHolder.SKILL_ITEM_TYPE.getPlaceHolder(), itemType )
                .replaceAll( PlaceHolder.SKILL_COST_AMOUNT.getPlaceHolder(), "" + costAmount ).replaceAll( PlaceHolder.ITEM_NAME.getPlaceHolder(), itemType ) );
    }

    protected void hookAPI()
    {
        placeholderHook = new PlaceholderHook( plugin, "dwarfcraft" );
        placeholderHook.hook();
    }

    public class PlaceholderHook extends EZPlaceholderHook
    {
        private DwarfCraft plugin;

        public PlaceholderHook( DwarfCraft plugin, String identifier )
        {
            super( plugin, identifier );
            this.plugin = plugin;
        }

        @Override
        public String onPlaceholderRequest( Player player, String identifier )
        {
            String out = generalParse( "%" + identifier + "%" );

            DwarfPlayer dwarfPlayer = plugin.getDataManager().find( player );

            if ( dwarfPlayer != null )
                out = parseByDwarfPlayer( out, dwarfPlayer );

            // If we didn't change the text then it wasn't out identifier so return null as per API wiki
            if ( out.equals( identifier ) )
                out = null;

            return out;
        }
    }
}
