package com.jessy1237.dwarfcraft.commands;

import com.jessy1237.dwarfcraft.DwarfCraft;
import com.jessy1237.dwarfcraft.models.DwarfCommand;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.LinkedHashMap;
import java.util.logging.Level;

public class CommandManager {

    private final DwarfCraft plugin;
    private Permission perms = null;
    private final LinkedHashMap<String, DwarfCommand> commands = new LinkedHashMap<>();

    public CommandManager( DwarfCraft plugin ) {
        this.plugin = plugin;

        try
        {
            if ( setupPermissions() )
                plugin.getUtil().consoleLog( Level.INFO, ChatColor.GREEN + "Success! Hooked into a Vault permissions plugin!" );
        }
        catch ( Exception e )
        {
            plugin.getUtil().consoleLog( Level.SEVERE, "Something went wrong! Unable to find a permissions plugin." );
            plugin.onDisable();
        }
    }

    public void registerCommand( DwarfCommand command ) {
        commands.put( command.getName(), command );
    }

    public DwarfCommand getCommand( String name ) {
        return commands.get(name);
    }

    public LinkedHashMap<String, DwarfCommand> getAllCommands() {
        return commands;
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration( Permission.class );
        perms = rsp.getProvider();
        return perms != null;
    }

    @SuppressWarnings("unused")
    private boolean isPermissionEnabled()
    {
        return perms != null;
    }

    public Permission getPermission()
    {
        return perms;
    }
}
