package com.Jessy1237.DwarfCraft.guis;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.events.DwarfRaceChangeEvent;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfRace;

public class RaceGUI extends DwarfGUI
{
    private String race = null;
    int inventorySize = 18;

    public RaceGUI( DwarfCraft plugin, DwarfPlayer player )
    {
        super( plugin, player );
    }

    @Override
    public void init()
    {
        int numRaces = plugin.getConfigManager().getRaceList().size();
        if (numRaces > 0) {
            if (numRaces > 45) numRaces = 45;
            int numRows = Math.max(1, (int) Math.ceil(numRaces / 9.0));
            this.inventorySize = (numRows * 9) + 9;
        }

        DwarfRace playerRace = plugin.getConfigManager().getRace( dwarfPlayer.getRace().toLowerCase() );
        if ( playerRace != null )
        {
            inventory = plugin.getServer().createInventory(dwarfPlayer.getPlayer(), this.inventorySize, "Change Race || Currently: " + plugin.getOut().parseColors(playerRace.getPrefixColour()) + dwarfPlayer.getRace());
        }
        else
        {
            inventory = plugin.getServer().createInventory(dwarfPlayer.getPlayer(), this.inventorySize, "Choose a race...");
        }

        inventory.clear();
        int i = 0;
        for ( DwarfRace race : plugin.getConfigManager().getRaceList() )
        {
            if ( !plugin.getPermission().has( dwarfPlayer.getPlayer(), "dwarfcraft.norm.race." + race.getName().toLowerCase() ) )
                continue;

            ItemStack item = new ItemStack( race.getIcon() );
            ArrayList<String> lore = new ArrayList<>();
            lore.add( ChatColor.GOLD + "Description:" );
            lore.addAll( parseStringToLore( race.getDesc(), "" + ChatColor.WHITE ) );
            lore.add("");
            lore.add(ChatColor.LIGHT_PURPLE + "Left click to change to " + race.getName());
            addItem( race.getName(), lore, i++, item );
        }

        dwarfPlayer.getPlayer().sendMessage("" + inventorySize);

        for (i = this.inventorySize - 9; i < this.inventorySize; i++)
            addItem( "Cancel", null, i, new ItemStack( Material.BARRIER ) );
    }

    @Override
    public void remove()
    {

    }

    @Override
    public void click( InventoryClickEvent event )
    {
        if (event.isLeftClick() && event.getRawSlot() <= this.inventorySize)
        {
            if ( event.getCurrentItem() == null )
                return;

            if ( event.getCurrentItem().getType() == Material.AIR )
                return;

            if ( event.getCurrentItem().getType() == Material.BARRIER )
            {
                dwarfPlayer.getPlayer().closeInventory();
            }
            else if ( event.getCurrentItem().getType() == Material.INK_SACK && race != null && !race.equals( "" ) )
            {
                DwarfRaceChangeEvent e = new DwarfRaceChangeEvent( dwarfPlayer, plugin.getConfigManager().getRace( race.toLowerCase() ) );
                plugin.getServer().getPluginManager().callEvent( e );

                if ( !e.isCancelled() )
                {
                    dwarfPlayer.getPlayer().closeInventory();
                    plugin.getOut().changedRace( dwarfPlayer.getPlayer(), dwarfPlayer, e.getRace().getName() );
                    dwarfPlayer.changeRace( e.getRace().getName() );
                }
            }
            else if ( event.getCurrentItem().getItemMeta().hasDisplayName() )
            {
                race = event.getCurrentItem().getItemMeta().getDisplayName();
                if ( !race.equals( ChatColor.RED + "WARNING" ) && race != null )
                {
                    if ( dwarfPlayer.getRace().equalsIgnoreCase( "NULL" ) )
                    {
                        DwarfRaceChangeEvent e = new DwarfRaceChangeEvent( dwarfPlayer, plugin.getConfigManager().getRace( race.toLowerCase() ) );
                        plugin.getServer().getPluginManager().callEvent( e );

                        if ( !e.isCancelled() )
                        {
                            dwarfPlayer.getPlayer().closeInventory();
                            plugin.getOut().changedRace( dwarfPlayer.getPlayer(), dwarfPlayer, e.getRace().getName() );
                            dwarfPlayer.changeRace( e.getRace().getName() );
                        }
                    }
                    else
                    {
                        confirmInit();
                    }
                }
            }
        }
    }

    private void confirmInit()
    {
        inventory.clear();
        addItem( ChatColor.RED + "WARNING", parseStringToLore( "Are you sure you want to change race to " + race + ". All your skills will be reset.", "" ), 0, new ItemStack( Material.PAPER ) );

        for (int i = this.inventorySize - 9; i < this.inventorySize; i++)
        {
            addItem( "Cancel", null, i, new ItemStack( Material.BARRIER ) );
        }

        addItem( "Confirm", null, ( this.inventorySize - 9 ) + 4, new ItemStack(Material.INK_SACK, 1, ( short ) 10 ) );
        dwarfPlayer.getPlayer().updateInventory();
    }
}
