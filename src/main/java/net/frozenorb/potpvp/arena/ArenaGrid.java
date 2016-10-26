package net.frozenorb.potpvp.arena;

import com.sk89q.worldedit.Vector;
import net.frozenorb.potpvp.arena.schematic.WorldSchematic;
import net.frozenorb.qlib.cuboid.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the grid on the world
 *
 *   X ------------->
 *  Z  (1,1) (1,2)
 *  |  (2,1) (2,2)
 *  |  (3,1) (3,2)
 *  |  (4,1) (4,2)
 *  V
 *
 *  Each arena is allocated a width of 400 blocks, however can expand as long as
 *  it wants based on it's schematics details
 *
 *
 * @author Mazen Kotb
 */
public class ArenaGrid {
    private static final Vector STARTING_POINT = new Vector(1000, 50, 1000);
    /* The maximum static width (of an arena) */
    private static final int MAX_ARENA_WIDTH = 400;
    /* Distance between two arenas on the X axis */
    private static final int X_DISTANCE = 250;
    /* Distance between two arenas on the Z axis */
    private static final int Z_DISTANCE = 500;
    /* Saves the spawn locations relative to the schematic in cache */
    private final Map<PotPvPSchematic, Vector[]> cachedSpawns = new HashMap<>();
    private final Map<PotPvPSchematic, Integer> schematicIndices = new HashMap<>();
    private final Map<PotPvPSchematic, Arena[]> grid = new HashMap<>();

    public int getCopies(PotPvPSchematic schematic) {
        if (schematic == null || !grid.containsKey(schematic)) {
            return -1;
        }

        return grid.get(schematic).length;
    }

    public int getIndex(PotPvPSchematic schematic) {
        if (schematic == null || !schematicIndices.containsKey(schematic)) {
            return -1;
        }

        return schematicIndices.get(schematic);
    }

    public void scaleCopies(Player initiator, PotPvPSchematic schematic, int copies) {
        if (schematic == null || !schematic.getFile().exists()) {
            initiator.sendMessage("Not a valid schematic!");
            return;
        }

        if (grid.containsKey(schematic)) {
            int index = schematicIndices.get(schematic);

            if (getCopies(schematic) > copies) {
                // TODO remove
            } else {
                // TODO create
            }
        }
    }

    private Arena createArena(int x, int z, PotPvPSchematic schematic) throws Exception {
        World world = Bukkit.getWorlds().get(0);
        WorldSchematic worldSchematic = schematic.asWorldSchematic();

        // calculate the starting position of the area
        int schematicLength = (int) worldSchematic.getSize().getZ();
        int realX = (MAX_ARENA_WIDTH * x) + (X_DISTANCE * x);
        int realZ = (schematicLength * z) + (Z_DISTANCE * z);
        Vector arenaStart = STARTING_POINT.add(new Vector(realX, 0, realZ));

        /* place the schematic and get the end point, find the spawns */
        Vector endPoint = worldSchematic.place(arenaStart).getValue();
        Cuboid cuboid = new Cuboid(GridUtils.locationFrom(arenaStart, world), GridUtils.locationFrom(endPoint, world));
        Vector[] spawns = getSpawns(schematic, arenaStart, cuboid);

        return new Arena(schematic.getName(), z, cuboid,
                objectiveVector(world, arenaStart, spawns[1]),
                objectiveVector(world, arenaStart, spawns[2]),
                objectiveVector(world, arenaStart, spawns[0]),
                false);
    }

    private Location objectiveVector(World world, Vector start, Vector point) {
        Vector general = start.add(point);
        return new Location(world, general.getX(), general.getY(), general.getZ());
    }

    private Vector[] getSpawns(PotPvPSchematic schematic, Vector start, Cuboid cube) {
        return cachedSpawns.computeIfAbsent(schematic, (ignored) -> GridUtils.getSpawns(start, cube));
    }
}
