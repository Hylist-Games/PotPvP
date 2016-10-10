package net.frozenorb.potpvp.arena;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;

import net.frozenorb.qlib.qLib;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ArenaHandler {

    private static final String ARENA_INSTANCES_FILE_NAME = "arenaInstances.json";
    private static final String SCHEMATICS_FILE_NAME = "schematics.json";

    // schematic name + copy combo -> Arena instance
    private Map<ArenaSchematic, Map<Integer, Arena>> arenaInstances = new HashMap<>();
    // schematic name -> ArenaSchematic instance
    private Map<String, ArenaSchematic> schematics = new HashMap<>();

    public ArenaHandler() {
        World arenaWorld = Bukkit.getWorlds().get(0);
        File arenaInstancesFile = new File(arenaWorld.getWorldFolder(), ARENA_INSTANCES_FILE_NAME);
        File schematicsFile = new File(arenaWorld.getWorldFolder(), SCHEMATICS_FILE_NAME);

        try {
            if (arenaInstancesFile.exists()) {
                String arenaInstancesJson = Files.readFirstLine(arenaInstancesFile, Charsets.UTF_8);
                Type arenaInstancesType = new TypeToken<Map<ArenaSchematic, Map<Integer, Arena>>>(){}.getType();

                arenaInstances = qLib.GSON.fromJson(arenaInstancesJson, arenaInstancesType);
            }

            if (schematicsFile.exists()) {
                String schematicsJson = Files.readFirstLine(schematicsFile, Charsets.UTF_8);
                Type schematicsType = new TypeToken<Map<String, ArenaSchematic>>(){}.getType();

                schematics = qLib.GSON.fromJson(schematicsJson, schematicsType);
            }
        } catch (IOException ex) {
            // just rethrow, can't recover from maps failing to load
            throw new RuntimeException(ex);
        }
    }

    /**
     * Finds an arena by its schematic and copy pair
     * @param schematic ArenaSchematic to use when looking up arena
     * @param copy copy of arena to look up
     * @return Arena object existing for specified schematic and copy pair, if one exists
     */
    public Arena getArena(ArenaSchematic schematic, int copy) {
        Map<Integer, Arena> arenaCopies = arenaInstances.get(schematic);

        if (arenaCopies != null) {
            return arenaCopies.get(copy);
        } else {
            return null;
        }
    }

    /**
     * Finds all arena instances for the given schematic
     * @param schematic schematic to look up arenas for
     * @return immutable set of all arenas for given schematic
     */
    public Set<Arena> getArenas(ArenaSchematic schematic) {
        Map<Integer, Arena> arenaCopies = arenaInstances.get(schematic);

        if (arenaCopies != null) {
            return ImmutableSet.copyOf(arenaCopies.values());
        } else {
            return ImmutableSet.of();
        }
    }

    /**
     * Finds all schematic instances registered
     * @return immutable set of all schematics registered
     */
    public Set<ArenaSchematic> getSchematics() {
        return ImmutableSet.copyOf(schematics.values());
    }

    /**
     * Finds an ArenaSchematic by its id
     * @param schematicName schematic id to search with
     * @return ArenaSchematic present for the given id, if one exists
     */
    public ArenaSchematic getSchematic(String schematicName) {
        return schematics.get(schematicName);
    }

}