package com.Jessy1237.DwarfCraft.guis;

import java.util.ArrayList;
import java.util.Collections;

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

    public RaceGUI( DwarfCraft plugin, DwarfPlayer player )
    {
        super( plugin, player );
    }

    @Override
    public void init()
    {
        inventory = plugin.getServer().createInventory( dwarfPlayer.getPlayer(), 18, "Race List || You're a " + plugin.getOut().parseColors( plugin.getConfigManager().getRace( dwarfPlayer.getRace().toLowerCase() ).getPrefixColour() ) + dwarfPlayer.getRace() );
        inventory.clear();
        int i = 0;
        for ( DwarfRace race : plugin.getConfigManager().getRaceList() )
        {
            if ( !plugin.getPermission().has( dwarfPlayer.getPlayer(), "dwarfcraft.norm.race." + race.getName().toLowerCase() ) )
                continue;

            ItemStack item = new ItemStack( Material.PAPER );
            ArrayList<String> lore = new ArrayList<String>();
            lore.add( ChatColor.GOLD + "Description:" );
            lore.addAll( parseStringToLore( race.getDesc(), "" + ChatColor.WHITE ) );
            lore.add( ChatColor.GOLD + "Specialised Skills:" );

            ArrayList<Integer> skills = race.getSkills();
            Collections.sort( skills );

            lore.addAll( parseStringToLore( skills.toString().replaceAll( "\\[", "" ).replaceAll( "\\]", "" ), "" + ChatColor.WHITE ) );
            lore.add( ChatColor.LIGHT_PURPLE + "Pick a race to change to" );
            addItem( race.getName(), lore, i++, item );
        }

        addItem( "Cancel", null, 13, new ItemStack( Material.BARRIER ) );
    }

    @Override
    public void remove()
    {
    }

    @Override
    public void click( InventoryClickEvent event )
    {
        if ( event.isLeftClick() && event.getRawSlot() <= 17 )
        {
            if ( event.getCurrentItem() == null )
                return;

            if ( event.getCurrentItem().getType() == Material.AIR )
                return;

            if ( event.getCurrentItem().getType() == Material.PAPER && event.getCurrentItem().getItemMeta().hasDisplayName() )
            {
                race = event.getCurrentItem().getItemMeta().getDisplayName();
                if ( !race.equals( ChatColor.RED + "WARNING" ) && race != null )
                {
                    confirmInit();
                }
            }
            else if ( event.getCurrentItem().getType() == Material.BARRIER )
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
        }
    }

    private void confirmInit()
    {
        inventory.clear();
        addItem( ChatColor.RED + "WARNING", parseStringToLore( "Are you sure you want to change race to " + race + ". All your skills will be reset.", "" ), 0, new ItemStack( Material.PAPER ) );

        for ( int i = 9; i < 18; i++ )
        {
            addItem( "Cancel", null, i, new ItemStack( Material.BARRIER ) );
        }

        addItem( "Confirm", null, 13, new ItemStack( Material.INK_SACK, 1, ( short ) 10 ) );
        dwarfPlayer.getPlayer().updateInventory();
    }
}
