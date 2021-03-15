/*
 * Copyright (c) 2018.
 *
 * DwarfCraft is an RPG plugin that allows players to improve their characters
 * skills and capabilities through training, not experience.
 *
 * Authors: Jessy1237 and Drekryan
 * Original Authors: smartaleq, LexManos and RCarretta
 */

package com.jessy1237.dwarfcraft;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.jessy1237.dwarfcraft.models.DwarfEffect;
import com.jessy1237.dwarfcraft.models.DwarfEffectType;
import com.jessy1237.dwarfcraft.models.DwarfPlayer;
import com.jessy1237.dwarfcraft.models.DwarfSkill;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderParser
{

    private DwarfCraft plugin;

    public PlaceholderParser( DwarfCraft plugin )
    {
        this.plugin = plugin;
    }

    public enum PlaceHolder
    {
        EFFECT_AMOUNT( "<effect.amount>" ),
        EFFECT_AMOUNT_DIG( "<effect.amount.dig>" ),
        EFFECT_AMOUNT_FOOD( "<effect.amount.food>" ),
        EFFECT_AMOUNT_FOOD_ORIGINAL( "<effect.amount.food.original>" ),
        EFFECT_AMOUNT_HIGH( "<effect.amount.high>" ),
        EFFECT_AMOUNT_INT( "<effect.amount.int>" ),
        EFFECT_AMOUNT_LOW( "<effect.amount.low>" ),
        EFFECT_AMOUNT_MINOR( "<effect.minor.amount>" ),
        EFFECT_AMOUNT_NORMAL( "<effect.normal.level>" ),
        EFFECT_CREATURE_NAME( "<effect.creature.name>" ),
        EFFECT_DAMAGE( "<effect.damage>" ),
        EFFECT_DAMAGE_BOW( "<effect.damage.bow>" ),
        EFFECT_DAMAGE_TAKEN( "<effect.damage.taken>" ),
        EFFECT_INITIATOR( "<effect.initiator>" ),
        EFFECT_OUTPUT( "<effect.output>" ),
        EFFECT_TOOL_TYPE( "<effect.tool.type>" ),
        EFFECT_LEVEL_COLOR( "<effect.level.color>" ),
        ITEM_NAME( "<item.name>" ),
        LEVEL( "<level>" ),
        SKILL_MAX_LEVEL( "<skill.max.level>" ),
        PLAYER_LEVEL( "<player.level>" ),
        PLAYER_NAME( "<player.name>" ),
        PLAYER_RACE( "<player.race>" ),
        RACE_LEVEL_LIMIT( "<race.level.limit>" ),
        RACE_NAME( "<race.name>" ),
        SKILL_COST_AMOUNT( "<skill.cost.amount>" ),
        SKILL_TOTAL_COST( "<skill.cost.total>" ),
        SKILL_DEPOSIT_AMOUNT( "<skill.deposit.amount>" ),
        SKILL_ID( "<skill.id>" ),
        SKILL_ITEM_TYPE( "<skill.item.type>" ),
        SKILL_LEVEL( "<skill.level>" ),
        SKILL_LEVEL_NEXT( "<skill.level.next>" ),
        SKILL_NAME( "<skill.name>" );

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
        return text.replaceAll( PlaceHolder.SKILL_MAX_LEVEL.getPlaceHolder(), "" + plugin.getConfigManager().getMaxSkillLevel() ).replaceAll( PlaceHolder.RACE_LEVEL_LIMIT.getPlaceHolder(), "" + plugin.getConfigManager().getRaceLevelLimit() );
    }

    public String parseByDwarfEffect( String text, DwarfEffect effect )
    {
        String origFoodLevel = "";
        if ( effect.getInitiatorMaterial() != null )
            origFoodLevel = String.format( "%.2f", ( ( double ) Util.FoodLevel.getLvl( effect.getInitiatorMaterial() ) ) / 2.0 );

        String initiator;
        if ( effect.getCreature() != null ) {
            initiator = plugin.getUtil().getCleanName( effect.getCreature() );
        } else {
            initiator = plugin.getUtil().getCleanName( effect.getInitiator() );
            if (effect.getEffectType() == DwarfEffectType.SMELT) {
                List<Recipe> recipes = plugin.getServer().getRecipesFor(new ItemStack(effect.getInitiatorMaterial()));

                if (!recipes.isEmpty() && recipes.get(0) instanceof FurnaceRecipe) {
                    FurnaceRecipe recipe = (FurnaceRecipe) recipes.get(0);
                    initiator = plugin.getUtil().getCleanName(recipe.getInput());
                }
            }
        }

        return generalParse( text.replaceAll( PlaceHolder.EFFECT_AMOUNT_FOOD_ORIGINAL.getPlaceHolder(), origFoodLevel ).replaceAll( PlaceHolder.EFFECT_INITIATOR.getPlaceHolder(), initiator ).replaceAll( PlaceHolder.EFFECT_OUTPUT.getPlaceHolder(), plugin.getUtil().getCleanName( effect.getResult() ) )
                .replaceAll( PlaceHolder.EFFECT_TOOL_TYPE.getPlaceHolder(), effect.toolType() ).replaceAll( PlaceHolder.EFFECT_AMOUNT_NORMAL.getPlaceHolder(), "" + effect.getNormalLevel() ) );
    }

    public String parseByDwarfPlayerAndDwarfEffect( String text, DwarfPlayer dwarfPlayer, DwarfEffect effect )
    {
        double effectAmount = effect.getEffectAmount( dwarfPlayer );
        String effectLevelColor = effect.effectLevelColor( dwarfPlayer.getSkill( effect.getSkillId() ).getLevel() );
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

        if ( effect.getEffectType() == DwarfEffectType.SMELT && effectAmount <= 0 ) {
            effectAmount = Math.max( 1.0, effectAmount );
        }

        return parseByDwarfEffect( text.replaceAll( PlaceHolder.EFFECT_AMOUNT_DIG.getPlaceHolder(), String.format( "%.0f", +( effectAmount * 100 ) ) ).replaceAll( PlaceHolder.EFFECT_DAMAGE_BOW.getPlaceHolder(), String.format( "%.0f", ( effectAmount + 2 ) ) )
                .replaceAll( PlaceHolder.EFFECT_AMOUNT_INT.getPlaceHolder(), "" + ( int ) effectAmount ).replaceAll( PlaceHolder.EFFECT_DAMAGE.getPlaceHolder(), "" + ( int ) ( effectAmount * 100 ) ).replaceAll( PlaceHolder.EFFECT_DAMAGE_TAKEN.getPlaceHolder(), "" + ( int ) ( effectAmount * 100 ) )
                .replaceAll( PlaceHolder.EFFECT_LEVEL_COLOR.getPlaceHolder(), effectLevelColor ).replaceAll( PlaceHolder.EFFECT_AMOUNT_MINOR.getPlaceHolder(), minorAmountStr ).replaceAll( PlaceHolder.EFFECT_AMOUNT_FOOD.getPlaceHolder(), String.format( "%.2f", ( effectAmount / 2.0 ) ) )
                .replaceAll( PlaceHolder.EFFECT_AMOUNT_LOW.getPlaceHolder(), String.format( "%.2f", effectAmountLow ) ).replaceAll( PlaceHolder.EFFECT_AMOUNT_HIGH.getPlaceHolder(), String.format( "%.2f", effectAmountHigh ) )
                .replaceAll( PlaceHolder.EFFECT_AMOUNT.getPlaceHolder(), String.format( "%.2f", effectAmount ) ).replaceAll( PlaceHolder.EFFECT_CREATURE_NAME.getPlaceHolder(), plugin.getUtil().getCleanName( effect.getCreature() ) ), effect );
    }

    public String parseByDwarfSkill( String text, DwarfSkill skill )
    {
        return generalParse( text.replaceAll( PlaceHolder.SKILL_ID.getPlaceHolder(), "" + skill.getId() ).replaceAll( PlaceHolder.SKILL_NAME.getPlaceHolder(), skill.getDisplayName() ).replaceAll( PlaceHolder.SKILL_LEVEL.getPlaceHolder(), "" + skill.getLevel() )
                .replaceAll( PlaceHolder.SKILL_LEVEL_NEXT.getPlaceHolder(), "" + ( skill.getLevel() + 1 ) ) );
    }

    public String parseByDwarfPlayer( String text, DwarfPlayer dwarfPlayer )
    {
        return generalParse( text.replaceAll( PlaceHolder.PLAYER_LEVEL.getPlaceHolder(), "" + dwarfPlayer.getDwarfLevel() ).replaceAll( PlaceHolder.PLAYER_NAME.getPlaceHolder(), dwarfPlayer.getPlayer().getDisplayName() )
                .replaceAll( PlaceHolder.PLAYER_RACE.getPlaceHolder(), dwarfPlayer.getRace().getName() ) );
    }

    public String parseByDwarfPlayerAndDwarfSkill( String text, DwarfPlayer dwarfPlayer, DwarfSkill skill )
    {
        // Calculate max level limit for skill. Checks to see if the players race specializes in the skill to see if skill should be locked to level cap.
        int levelLimit = skill.getMaxLevel( dwarfPlayer );
        return parseByDwarfSkill( parseByDwarfPlayer( text.replaceAll( PlaceHolder.SKILL_MAX_LEVEL.getPlaceHolder(), "" + levelLimit ), dwarfPlayer ), skill );
    }

    public String parseForTrainCosts( String text, int deposited, int costAmount, int totalCost, String itemType )
    {
        return generalParse( text.replaceAll( PlaceHolder.SKILL_DEPOSIT_AMOUNT.getPlaceHolder(), "" + deposited ).replaceAll( PlaceHolder.SKILL_TOTAL_COST.getPlaceHolder(), "" + totalCost ).replaceAll( PlaceHolder.SKILL_ITEM_TYPE.getPlaceHolder(), itemType )
                .replaceAll( PlaceHolder.SKILL_COST_AMOUNT.getPlaceHolder(), "" + costAmount ).replaceAll( PlaceHolder.ITEM_NAME.getPlaceHolder(), itemType ) );
    }

    public class PlaceholderExpansionHook extends PlaceholderExpansion
    {
        @Override
        public boolean canRegister()
        {
            return true;
        }

        @Override
        public String getIdentifier()
        {
            return "DwarfCraft";
        }

        @Override
        public String getAuthor()
        {
            return plugin.getDescription().getAuthors().toString();
        }

        @Override
        public String getVersion()
        {
            DwarfCraft plugin = (DwarfCraft) Bukkit.getPluginManager().getPlugin( "DwarfCraft" );
            return plugin.getDescription().getVersion();
        }

        @Override
        public String onPlaceholderRequest( Player player, String identifier )
        {
            String out = generalParse( "<" + identifier + ">" );

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
