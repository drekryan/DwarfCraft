package com.Jessy1237.DwarfCraft.data;

import com.Jessy1237.DwarfCraft.ConfigManager;
import com.Jessy1237.DwarfCraft.DwarfCraft;

public class DBWrapperFactory
{

    public static DBWrapper createWrapper( DwarfCraft plugin, String type )
    {
        DBWrapper dbWrapper = null;

        if ( type.equalsIgnoreCase( "sqlite" ) )
        {
            dbWrapper = new DBWrapperSQLite( plugin, plugin.getConfigManager() );
        }
        else if ( type.equalsIgnoreCase( "MySQL" ) )
        {
            dbWrapper = new DBWrapperMySQL( plugin, plugin.getConfigManager() );
        }

        return dbWrapper;
    }

}
