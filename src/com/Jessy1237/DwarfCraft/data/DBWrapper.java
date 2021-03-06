package com.Jessy1237.DwarfCraft.data;

import java.util.UUID;

import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;

public interface DBWrapper
{

    void dbInitialize();

    void dbFinalize();

    void createDwarfData(DwarfPlayer dCPlayer);

    boolean checkDwarfData(DwarfPlayer player);

    /**
     * Used for creating and populating a dwarf with a null(off line) player
     *
     * @param player
     * @param uuid
     */
    boolean checkDwarfData( DwarfPlayer player, UUID uuid );

    boolean saveDwarfData(DwarfPlayer dwarfPlayer, DwarfSkill[] skills);

}
