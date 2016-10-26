package net.frozenorb.potpvp.arena;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.event.ArenaAllocatedEvent;
import net.frozenorb.potpvp.arena.event.ArenaReleasedEvent;
import net.frozenorb.potpvp.arena.listener.ArenaClearListener;
import net.frozenorb.qlib.qLib;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import lombok.Getter;

/**
 * Facilitates easy access to {@link ArenaSchematic}s and to {@link Arena}s
 * based on their schematic+copy pair
 */
public final class ArenaHandler {

    public static final File WORLD_EDIT_SCHEMATICS_FOLDER = new File(JavaPlugin.getPlugin(WorldEditPlugin.class).getDataFolder(), "schematics");
    private static final String ARENA_INSTANCES_FILE_NAME = "arenaInstances.json";
    private static final String SCHEMATICS_FILE_NAME = "schematics.json";

    // schematic -> (instance id -> Arena instance)
    private final Map<String, Map<Integer, Arena>> arenaInstances = new HashMap<>();
    // schematic name -> ArenaSchematic instance
    private final Map<String, ArenaSchematic> schematics = new HashMap<>();
    @Getter private final ArenaGrid grid = new ArenaGrid();

    public ArenaHandler() {
        Bukkit.getPluginManager().registerEvents(new ArenaClearListener(), PotPvPSI.getInstance());

        // even though the locations stored in Arenas contain a World object we
        // store arenas in (and host matches in) the overworld
        World arenaWorld = Bukkit.getWorlds().get(0);

        File arenaInstancesFile = new File(arenaWorld.getWorldFolder(), ARENA_INSTANCES_FILE_NAME);
        File schematicsFile = new File(arenaWorld.getWorldFolder(), SCHEMATICS_FILE_NAME);

        try {
            // parsed as a List<Arena> and then inserted into Map<String, Map<Integer. Arena>>
            if (arenaInstancesFile.exists()) {
                try (Reader arenaInstancesReader = Files.newReader(arenaInstancesFile, Charsets.UTF_8)) {
                    Type arenaListType = new TypeToken<List<Arena>>(){}.getType();
                    List<Arena> arenaList = qLib.GSON.fromJson(arenaInstancesReader, arenaListType);

                    for (Arena arena : arenaList) {
                        // create inner Map for schematic if not present
                        arenaInstances.computeIfAbsent(arena.getSchematic(), i -> new HashMap<>());

                        // register this copy with the inner Map
                        arenaInstances.get(arena.getSchematic()).put(arena.getCopy(), arena);
                    }
                }
            }

            // parsed as a List<ArenaSchematic> and then inserted into Map<String, ArenaSchematic>
            if (schematicsFile.exists()) {
                try (Reader schematicsFileReader = Files.newReader(schematicsFile, Charsets.UTF_8)) {
                    Type schematicListType = new TypeToken<List<ArenaSchematic>>() {}.getType();
                    List<ArenaSchematic> schematicList = qLib.GSON.fromJson(schematicsFileReader, schematicListType);

                    for (ArenaSchematic schematic : schematicList) {
                        this.schematics.put(schematic.getName(), schematic);
                    }
                }
            }
        } catch (IOException ex) {
            // just rethrow, can't recover from arenas failing to load
            throw new RuntimeException(ex);
        }

        Bukkit.getScheduler().runTask(PotPvPSI.getInstance(), grid::loadSchematics);
    }

    void saveSchematics() throws IOException {
        World arenaWorld = Bukkit.getWorlds().get(0);

        Files.write(
            qLib.GSON.toJson(schematics.values()),
            new File(arenaWorld.getWorldFolder(), SCHEMATICS_FILE_NAME),
            Charsets.UTF_8
        );
    }

    /**
     * Finds an arena by its schematic and copy pair
     * @param schematic ArenaSchematic to use when looking up arena
     * @param copy copy of arena to look up
     * @return Arena object existing for specified schematic and copy pair, if one exists
     */
    public Arena getArena(ArenaSchematic schematic, int copy) {
        Map<Integer, Arena> arenaCopies = arenaInstances.get(schematic.getName());

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
        Map<Integer, Arena> arenaCopies = arenaInstances.get(schematic.getName());

        if (arenaCopies != null) {
            return ImmutableSet.copyOf(arenaCopies.values());
        } else {
            return ImmutableSet.of();
        }
    }

    /**
     * Counts the number of arena instances present for the given schematic
     * @param schematic schematic to count arenas for
     * @return number of copies present of the given schematic
     */
    public int countArenas(ArenaSchematic schematic) {
        Map<Integer, Arena> arenaCopies = arenaInstances.get(schematic.getName());
        return arenaCopies != null ? arenaCopies.size() : 0;
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

            for (Arena arena : arenaInstances.get(schematic.getName()).values()) {
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