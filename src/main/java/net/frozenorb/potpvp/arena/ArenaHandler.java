package net.frozenorb.potpvp.arena;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.event.ArenaAllocatedEvent;
import net.frozenorb.potpvp.arena.event.ArenaReleasedEvent;
import net.frozenorb.potpvp.arena.listener.ArenaClearListener;
import net.frozenorb.qlib.qLib;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;

/**
 * Facilitates easy access to {@link ArenaSchematic}s and to {@link Arena}s
 * based on their schematic+copy pair
 */
public final class ArenaHandler {

    private static final String ARENA_INSTANCES_FILE_NAME = "arenaInstances.json";
    private static final String SCHEMATICS_FILE_NAME = "schematics.json";

    // schematic -> (instance id -> Arena instance)
    private Map<ArenaSchematic, Map<Integer, Arena>> arenaInstances = new HashMap<>();
    // schematic name -> ArenaSchematic instance
    private Map<String, ArenaSchematic> schematics = new HashMap<>();

    public ArenaHandler() {
        Bukkit.getPluginManager().registerEvents(new ArenaClearListener(), PotPvPSI.getInstance());

        // even though the locations stored in Arenas contain a World object we
        // store arenas in (and host matches in) the overworld
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
            // just rethrow, can't recover from arenas failing to load
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

    /**
     * Attempts to allocate an arena for use, using the Predicate provided to determine
     * which arenas are eligible for use. Handles calling {@link net.frozenorb.potpvp.arena.event.ArenaAllocatedEvent}
     * automatically.
     * @param acceptableSchematicPredicate Predicate to use to determine if an {@link ArenaSchematic}
     *                                     is eligible for use.
     * @return The arena which has been allocated for use, or null, if one was not found.
     */
    public Arena allocateUnusedArena(Predicate<ArenaSchematic> acceptableSchematicPredicate) {
        for (ArenaSchematic schematic : schematics.values()) {
            if (!acceptableSchematicPredicate.test(schematic)) {
                continue;
            }

            for (Arena arena : arenaInstances.get(schematic).values()) {
                if (arena.isInUse()) {
                    continue;
                }

                arena.setInUse(true);
                Bukkit.getPluginManager().callEvent(new ArenaAllocatedEvent(arena));

                return arena;
            }
        }

        return null;
    }

    /**
     * Releases (unallocates) an arena so that it may be used again. Handles calling
     * {@link net.frozenorb.potpvp.arena.event.ArenaReleasedEvent} automatically.
     * @param arena the arena to release
     */
    public void releaseArena(Arena arena) {
        Preconditions.checkArgument(arena.isInUse(), "Cannot release arena not in use.");

        arena.setInUse(false);
        Bukkit.getPluginManager().callEvent(new ArenaReleasedEvent(arena));
    }

}