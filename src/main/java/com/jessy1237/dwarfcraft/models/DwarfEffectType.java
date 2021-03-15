/*
 * Copyright (c) 2018.
 *
 * DwarfCraft is an RPG plugin that allows players to improve their characters
 * skills and capabilities through training, not experience.
 *
 * Authors: Jessy1237 and Drekryan
 * Original Authors: smartaleq, LexManos and RCarretta
 */

package com.jessy1237.dwarfcraft.models;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */
public enum DwarfEffectType
{
    // IMPLEMENTATION PRIORITY ORDER
    BLOCKDROP,
    BLOCKDROPDUPE,
    MOBDROP,
    SWORDDURABILITY,
    PVPDAMAGE,
    PVEDAMAGE,
    EXPLOSIONDAMAGE,
    FIREDAMAGE,
    FALLDAMAGE,
    FALLTHRESHOLD,
    PLOWDURABILITY,
    TOOLDURABILITY,
    EAT,
    CRAFT,
    PLOW,
    DIGTIME,
    BOWATTACK,
    VEHICLEDROP,
    VEHICLEMOVE,
    SPECIAL,
    FISH,
    RODDURABILITY,
    SMELT,
    BREW,
    SHEAR;

    protected static DwarfEffectType getEffectType( String name )
    {
        for ( DwarfEffectType effectType : DwarfEffectType.values() )
        {
            if ( effectType.toString().equalsIgnoreCase( name ) )
                return effectType;
        }
        return null;
    }

}
