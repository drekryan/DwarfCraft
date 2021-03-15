package com.jessy1237.dwarfcraft.data;

import com.jessy1237.dwarfcraft.DwarfCraft;
import com.jessy1237.dwarfcraft.RaceManager;
import com.jessy1237.dwarfcraft.models.DwarfRace;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.bukkit.Material;

import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;

public
class RaceReader {

    private final DwarfCraft plugin;
    private final RaceManager manager;
    private boolean vanillaEnabled = true;

    public
    RaceReader( DwarfCraft plugin, RaceManager manager ) {
        this.plugin = plugin;
        this.manager = manager;
        this.vanillaEnabled = plugin.getConfigManager().vanilla;

        parseRaces();
    }

    private
    void parseRaces() {
        String content;
        File directory = new File( plugin.getDataFolder().getAbsolutePath() + "/data/dwarfcraft/races/" );
        File customRaces = new File( plugin.getDataFolder().getAbsolutePath() + "/data/custom/races/" );
        ArrayList<String> overrideNames = new ArrayList<>( Arrays.asList( Objects.requireNonNull( customRaces.list() ) ) );
        overrideNames.removeIf( override->!override.endsWith( ".json" ) );

        if ( vanillaEnabled ) {
            System.out.println("Adding vanilla race");
            manager.addRace( new DwarfRace( "vanilla", "Vanilla", "The all around balanced race (vanilla).", Material.GRASS ) );
        }

        int maxAllowed = vanillaEnabled ? 44 : 45;
        if ( directory.list() != null && Objects.requireNonNull( directory.list() ).length > 0 )
        {
            for (String file_name : Objects.requireNonNull( directory.list() ) ) {
                try
                {
                    if ( overrideNames.contains( file_name ) ) {
                        content = new String( Files.readAllBytes( Paths.get( customRaces + "/" + file_name ) ) );
                    } else {
                        content = new String( Files.readAllBytes( Paths.get( directory + "/" + file_name ) ) );
                        if ( !file_name.endsWith( ".json" ) ) continue;
                    }

                    JsonReader reader = new JsonReader( new StringReader( content.trim() ) );
                    reader.setLenient( true );
                    JsonElement element = new JsonParser().parse( reader );
                    JsonObject json = element.getAsJsonObject();

                    // Race ID
                    String race_id = file_name.split( "\\." )[0].toLowerCase();

                    // Display Name
                    String display_name = json.get( "display_name" ).getAsString();

                    // Description
                    String description = json.get( "description" ).getAsString();

                    // Prefix Colour
                    String prefix_colour = json.get( "prefix_colour" ).getAsString();

                    // Icon
                    Material icon = Material.matchMaterial( json.get( "material_icon" ).getAsString() );

                    DwarfRace race = new DwarfRace(race_id, display_name, description, icon);
                    if ( manager.count() < maxAllowed ) {
                        race.setPrefixColour(prefix_colour);
                        manager.addRace(race);
                    } else {
                        plugin.getUtil().consoleLog( Level.WARNING, "Did not load race: " + race.getName() + " as already at cap of " + maxAllowed + " races" );
                    }
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
