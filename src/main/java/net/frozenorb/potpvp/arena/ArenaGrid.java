package net.frozenorb.potpvp.arena;

import com.sk89q.worldedit.Vector;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.schematic.WorldSchematic;
import net.frozenorb.qlib.cuboid.Cuboid;
import net.frozenorb.qlib.util.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the grid on the world
 *
 *   Z ------------->
 *  X  (1,1) (1,2)
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
    /* Distance between two arenas on the Z axis */
    private static final int Z_DISTANCE = 250;
    /* Distance between two arenas on the X axis */
    private static final int X_DISTANCE = 500;
    private final Map<PotPvPSchematic, SchematicGridData> grid = new HashMap<>();

    public void loadSchematics() {
        PotPvPSI.getInstance().getArenaHandler().getSchematics().forEach((schematic) -> {
            SchematicGridData data = new SchematicGridData(schematic.getIndex());
            Arena[] arenas = new Arena[schematic.getCopies()];

            for (int z = 1; z <= schematic.getCopies(); z++) {
                try {
                    arenas[z - 1] = createArena(data.x, z, schematic, data, false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("[ArenaGrid] Couldn't load schematic " + schematic.getName() +
                            "'s arena at index " + z + " due to an exception!");
                }
            }

            data.arenas = arenas;
            grid.put(schematic, data);
        });
    }

    public PotPvPSchematic schematicBy(int index) {
        return grid.entrySet().stream()
                .filter((entry) -> entry.getValue().x == index)
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }

    public int getCopies(PotPvPSchematic schematic) {
        if (schematic == null || !grid.containsKey(schematic)) {
            return -1;
        }

        return grid.get(schematic).arenas.length;
    }

    public int getIndex(PotPvPSchematic schematic) {
        if (schematic == null || !grid.containsKey(schematic)) {
            return -1;
        }

        return grid.get(schematic).x;
    }

    private int nextIndex() {
        return grid.entrySet().stream().map(Map.Entry::getValue)
                .mapToInt((data) -> data.x)
                .max().orElse(0) + 1;
    }

    public void scaleCopies(Player initiator, PotPvPSchematic schematic, int copies) {
        if (schematic == null || !schematic.getFile().exists()) {
            initiator.sendMessage("Not a valid schematic!");
            return;
        }

        SchematicGridData data = grid.computeIfAbsent(schematic, (ign) -> {
            int dataIndex = nextIndex();
            System.out.println("[ArenaGrid] Assigning index " + dataIndex + " to schematic " + schematic.getName());
            schematic.setIndex(dataIndex);

            return new SchematicGridData(dataIndex);
        });

        try {
            if (data.arenas.length > copies) {
                deleteArenas(schematic, data.arenas.length - copies);
                initiator.sendMessage("Deleted arenas successfully. New count " + copies);
            } else {
                for (int z = data.arenas.length; z <= copies; z++) {
                    createArena(data.x, z, schematic, data, true);
                }

                initiator.sendMessage("Added arenas successfully. New count " + copies);
            }

            schematic.setCopies(data.arenas.length);

            try {
                PotPvPSI.getInstance().getArenaHandler().saveSchematicData();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("[ArenaGrid] Could not save the schematic data for " + schematic.getName() +
                        ". It's set index in runtime is " + data.x +
                        " and the amount of copies is " + schematic.getCopies());
            }
        } catch (Exception ex) {
            initiator.sendMessage("There was an error performing that operation: "
                    + ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }
    }

    private Arena createArena(int x, int z, PotPvPSchematic schematic, SchematicGridData data, boolean place) throws Exception {
        World world = Bukkit.getWorlds().get(0);
        WorldSchematic worldSchematic = data.getWorldSchematic(schematic);

        // calculate the starting position of the area
        int schematicLength = (int) worldSchematic.getSize().getZ();
        int realX = (MAX_ARENA_WIDTH * x) + (X_DISTANCE * x);
        int realZ = (schematicLength * z) + (Z_DISTANCE * z);
        Vector arenaStart = STARTING_POINT.add(new Vector(realX, 0, realZ));

        /* place the schematic and get the end point, find the spawns */
        Vector endPoint = (place) ? worldSchematic.place(arenaStart).getValue() : arenaStart.add(worldSchematic.getSize());
        Cuboid cuboid = new Cuboid(GridUtils.locationFrom(arenaStart, world), GridUtils.locationFrom(endPoint, world));
        Vector[] spawns = getSpawns(schematic, arenaStart, cuboid);

        return new Arena(schematic.getName(), z, cuboid,
                objectiveVector(world, arenaStart, spawns[1]),
                objectiveVector(world, arenaStart, spawns[2]),
                objectiveVector(world, arenaStart, spawns[0]),
                false);
    }

    private void deleteArenas(PotPvPSchematic schematic, int amount) throws Exception { // 3, and removing 2
        SchematicGridData data = grid.get(schematic);
        Arena[] arenas = data.arenas;
        Location start = arenas[arenas.length - amount].getBounds().getLowerNE();
        Location end = arenas[arenas.length - 1].getBounds().getUpperSW();

        for (int x = start.getBlockX(); x < end.getBlockX(); x++) {
            for (int y = start.getBlockY(); y < end.getBlockY(); y++) {
                for (int z = start.getBlockZ(); z < end.getBlockZ(); z++) {
                    BlockUtils.setBlockFast(start.getWorld(), x, y, z, 0, (byte) 0);
                }
            }
        }

        Arena[] newArenas = new Arena[arenas.length - amount];
        System.arraycopy(arenas, 0, newArenas, 0, newArenas.length);
        data.arenas = newArenas;
    }

    private Location objectiveVector(World world, Vector start, Vector point) {
        Vector general = start.add(point);
        return new Location(world, general.getX(), general.getY(), general.getZ());
    }

    private Vector[] getSpawns(PotPvPSchematic schematic, Vector start, Cuboid cube) {
        SchematicGridData data = grid.get(schematic);
        return data.spawns == null ? data.spawns = GridUtils.getSpawns(start, cube) : data.spawns;
    }

    private static class SchematicGridData {
        private Vector[] spawns;
        private int x;
        private Arena[] arenas;
        private WorldSchematic worldSchematic;

        SchematicGridData(int x) {
            this.x = x;
        }

        WorldSchematic getWorldSchematic(PotPvPSchematic schematic) throws Exception {
            return worldSchematic == null ?
                    worldSchematic = schematic.asWorldSchematic() :
                    worldSchematic;
        }
    }
}
