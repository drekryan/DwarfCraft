package com.Jessy1237.DwarfCraft.model;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */

public enum DwarfEffectType
{
    // IMPLEMENTATION PRIORITY ORDER
    BLOCKDROP,
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
