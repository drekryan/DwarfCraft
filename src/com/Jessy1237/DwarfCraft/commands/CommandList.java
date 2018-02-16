package com.Jessy1237.DwarfCraft.commands;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Jessy1237.DwarfCraft.DwarfCraft;
import com.Jessy1237.DwarfCraft.guis.ListGUI;
import com.Jessy1237.DwarfCraft.models.DwarfPlayer;
import com.Jessy1237.DwarfCraft.models.DwarfSkill;
import com.Jessy1237.DwarfCraft.models.DwarfTrainer;

/**
 * Original Authors: smartaleq, LexManos and RCarretta
 */
public class CommandList extends Command
{
    private final DwarfCraft plugin;

    public CommandList( final DwarfCraft plugin )
    {
        super( "List" );
        this.plugin = plugin;
    }

    @Override
    public boolean execute( CommandSender sender, String commandLabel, String[] args )
    {
        if ( DwarfCraft.debugMessagesThreshold < 1 )
            System.out.println( "DC1: started command 'list'" );

        int page = 1;

        Collection<DwarfTrainer> col = plugin.getDataManager().trainerList.values();
        DwarfTrainer[] trainers = new DwarfTrainer[col.size()];
        col.toArray( trainers );

        if ( sender instanceof Player )
        {
            // Use GUI implementation

            DwarfPlayer dwarfPlayer = new DwarfPlayer( plugin, ( Player ) sender );

            ListGUI listTrainersGUI = new ListGUI( plugin, dwarfPlayer );

            plugin.getDwarfInventoryListener().addDwarfGUI( dwarfPlayer.getPlayer(), listTrainersGUI );

            return true;
        }
        else
        {
            if ( args.length > 0 )
            {
                try
                {
                    Integer.parseInt( args[0] );
                }
                catch ( NumberFormatException e )
                {
                    page = 1;
                }
            }

            if ( trainers.length == 0 )
            {
                sender.sendMessage( "There are currently no trainers." );
                return true;
            }

            int maxpage = ( int ) Math.ceil( trainers.length / 10.0 );
            Collection<DwarfSkill> skills = plugin.getConfigManager().getAllSkills().values();

            page = Math.min( page, maxpage );
            page = Math.max( page, 1 );

            int idx = ( page - 1 ) * 10;
            sender.sendMessage( String.format( "Trainers page %d/%d", page, maxpage ) );

            for ( int x = 0; x < 10; x++ )
            {
                if ( idx + x >= trainers.length )
                    return true;

                DwarfTrainer trainer = trainers[idx + x];
                Location loc = trainer.getLocation();

                String skillName = "Unknown";
                for ( DwarfSkill skill : skills )
                {
                    if ( skill.getId() == trainer.getSkillTrained() )
                        skillName = skill.getDisplayName();
                }
                sender.sendMessage( String.format( "Trainer ID: %s Name: %s Trains: %d %s (%d, %d, %d)", trainer.getUniqueId(), trainer.getName(), trainer.getMaxSkill(), skillName, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() ) );

            }
            return true;
        }
    }

}
