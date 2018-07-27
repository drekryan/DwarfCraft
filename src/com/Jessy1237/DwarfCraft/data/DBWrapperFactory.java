package com.Jessy1237.DwarfCraft.data;

import com.Jessy1237.DwarfCraft.ConfigManager;
import com.Jessy1237.DwarfCraft.DwarfCraft;

public class DBWrapperFactory
{

    public static DBWrapper createWrapper( String type, DwarfCraft plugin, ConfigManager cm )
    {
        DBWrapper dbWrapper = null;

        if ( type.equalsIgnoreCase( "sqlite" ) )
        {
            dbWrapper = new DBWrapperSQLite( plugin, cm );
        }
        else if ( type.equalsIgnoreCase( "MySQL" ) )
        {
            dbWrapper = new DBWrapperMySQL( plugin, cm );
        }

        return dbWrapper;
    }

}
