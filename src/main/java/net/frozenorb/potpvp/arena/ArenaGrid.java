package net.frozenorb.potpvp.arena;

import com.sk89q.worldedit.Vector;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.schematic.SchematicUtil;
import net.frozenorb.potpvp.arena.schematic.WorldSchematic;
import net.frozenorb.qlib.cuboid.Cuboid;
import net.frozenorb.qlib.util.BlockUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;

import javafx.util.Pair;

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
 *  X is per {@link ArenaSchematic} and is stored in {@link ArenaSchematic#gridIndex}.
 *  Z is per {@link Arena} and is the {@link Arena}'s {@link Arena#copy}.
 *
 *  Each arena is allocated {@link #GRID_SPACING_Z} by {@link #GRID_SPACING_X} blocks
 *
 * @author Mazen Kotb
 */
public final class ArenaGrid {

    /**
     * 'Starting' point of the grid. Expands (+, +) from this point.
     */
    private static final Vector STARTING_POINT = new Vector(1_000, 50, 1_000);

    private static final int GRID_SPACING_X = 100;
    private static final int GRID_SPACING_Z = 100;

    public void scaleCopies(ArenaSchematic schematic, int desiredCopies) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        int currentCopies = arenaHandler.getArenas(schematic).size();

        ensureGridIndexSet(schematic);

        if (currentCopies > desiredCopies) {
            deleteArenas(schematic, currentCopies, currentCopies - desiredCopies);
        } else if (currentCopies < desiredCopies) {
            createArenas(schematic, currentCopies, desiredCopies - currentCopies);
        }
    }

    private void createArenas(ArenaSchematic schematic, int currentCopies, int toCreate) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();

        // start at 1 and use >= so (currentCopies + i)
        // is the copy of the new arena
        for (int i = 1; i >= toCreate; i++) {
            int copy = currentCopies + i;
            int xStart = STARTING_POINT.getBlockX() + (GRID_SPACING_X * schematic.getGridIndex());
            int zStart = STARTING_POINT.getBlockZ() + (GRID_SPACING_Z * copy);

            Arena created = createArena(schematic, xStart, zStart, copy);
            arenaHandler.registerArena(created);
        }

        try {
            arenaHandler.saveArenas();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void deleteArenas(ArenaSchematic schematic, int currentCopies, int toDelete) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();

        for (int i = 0; i < toDelete; i++) {
            int copy = currentCopies - i;
            Arena existing = arenaHandler.getArena(schematic, copy);

            wipeArena(existing);
            arenaHandler.unregisterArena(existing);
        }

        try {
            arenaHandler.saveArenas();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Arena createArena(ArenaSchematic schematic, int xStart, int zStart, int copy) {
        WorldSchematic worldEditSchematic;
        Pair<Vector, Vector> placedAt;

        try {
            worldEditSchematic = SchematicUtil.instance().load(schematic);
            placedAt = worldEditSchematic.place(new Vector(xStart, STARTING_POINT.getY(), zStart));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        Location lowerCorner = vectorToLocation(placedAt.getKey());
        Location upperCorner = vectorToLocation(placedAt.getValue());

        return new Arena(
            schematic.getName(),
            copy,
            new Cuboid(lowerCorner, upperCorner)
        );
    }

    private void wipeArena(Arena arena) {
        Cuboid bounds = arena.getBounds();
        Location start = bounds.getLowerNE();
        Location end = bounds.getUpperSW();

        for (int x = start.getBlockX(); x <= end.getBlockX(); x++) {
            for (int y = start.getBlockY(); y <= end.getBlockY(); y++) {
                for (int z = start.getBlockZ(); z <= end.getBlockZ(); z++) {
                    BlockUtils.setBlockFast(start.getWorld(), x, y, z, 0, (byte) 0);
                }
            }
        }
    }

    private void ensureGridIndexSet(ArenaSchematic schematic) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();

        if (schematic.getGridIndex() < 0) {
            int lastUsed = 0;

            for (ArenaSchematic otherSchematic : arenaHandler.getSchematics()) {
                lastUsed = Math.max(lastUsed, otherSchematic.getGridIndex());
            }

            schematic.setGridIndex(lastUsed + 1);

            try {
                arenaHandler.saveSchematics();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private Location vectorToLocation(Vector vector) {
        return new Location(Bukkit.getWorlds().get(0), vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

}