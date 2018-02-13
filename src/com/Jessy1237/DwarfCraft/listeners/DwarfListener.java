package com.Jessy1237.DwarfCraft.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.events.DwarfEffectEvent;
import com.Jessy1237.DwarfCraft.events.DwarfLevelUpEvent;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;

public class DwarfListener implements Listener
{

    private final DwarfCraft plugin;

    public DwarfListener( final DwarfCraft plugin )
    {
        this.plugin = plugin;
    }

    @EventHandler( priority = EventPriority.NORMAL, ignoreCancelled = true )
    public void onDwarfLevelUp( DwarfLevelUpEvent event )
    {
        DwarfPlayer player = event.getDwarfPlayer();
        DwarfSkill skill = event.getSkill();

        if ( skill.getLevel() % plugin.getConfigManager().getAnnouncementInterval() == 0 && plugin.getConfigManager().announce )
        {
            String name = plugin.getChat().getPlayerPrefix( player.getPlayer() ) + player.getPlayer().getName() + plugin.getChat().getPlayerSuffix( player.getPlayer() );
            plugin.getOut().sendBroadcast( plugin.getConfigManager().getAnnouncementMessage().replace( "%playername%", name ).replace( "%skillname%", skill.getDisplayName() ).replace( "%level%", "" + skill.getLevel() ) );
        }
    }

    public void onDwarfEffectEvent( DwarfEffectEvent event )
    {
        DwarfPlayer player = event.getDwarfPlayer();
        if ( player == null )
            return;
        
        if ( player.getRace().equalsIgnoreCase( plugin.getConfigManager().getVanillaRace() ) )
        {
            event.setCancelled( true );
        }
    }
}
