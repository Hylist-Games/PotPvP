package net.frozenorb.potpvp.arena;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.qlib.cuboid.Cuboid;

import org.bukkit.Location;

import java.io.IOException;

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
    public static final Vector STARTING_POINT = new Vector(1_000, 60, 1_000);

    public static final int GRID_SPACING_X = 300;
    public static final int GRID_SPACING_Z = 300;

    public void scaleCopies(ArenaSchematic schematic, int desiredCopies) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        int currentCopies = arenaHandler.countArenas(schematic);

        if (currentCopies == 0 && desiredCopies > 0) {
            try {
                schematic.removeModelArena();
                schematic.pasteModelArena();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (currentCopies > desiredCopies) {
            deleteArenas(schematic, currentCopies, currentCopies - desiredCopies);
        } else if (currentCopies < desiredCopies) {
            createArenas(schematic, currentCopies, desiredCopies - currentCopies);
        } else {
            // if we're not actually changing anything return
            // early to avoid unneeded arena save (see below)
            return;
        }

        try {
            arenaHandler.saveArenas();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void createArenas(ArenaSchematic schematic, int currentCopies, int toCreate) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();

        // start at 1 and use >= so (currentCopies + i)
        // is the copy of the new arena
        for (int i = 1; i <= toCreate; i++) {
            int copy = currentCopies + i;
            int xStart = STARTING_POINT.getBlockX() + (GRID_SPACING_X * schematic.getGridIndex());
            int zStart = STARTING_POINT.getBlockZ() + (GRID_SPACING_Z * copy);

            Arena created = createArena(schematic, xStart, zStart, copy);
            arenaHandler.registerArena(created);
        }
    }

    private void deleteArenas(ArenaSchematic schematic, int currentCopies, int toDelete) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();

        for (int i = 0; i < toDelete; i++) {
            int copy = currentCopies - i;
            Arena existing = arenaHandler.getArena(schematic, copy);

            WorldEditUtils.clear(existing.getBounds());
            arenaHandler.unregisterArena(existing);
        }
    }

    private Arena createArena(ArenaSchematic schematic, int xStart, int zStart, int copy) {
        Vector pasteAt = new Vector(xStart, STARTING_POINT.getY(), zStart);
        CuboidClipboard clipboard;

        try {
            clipboard = WorldEditUtils.paste(schematic, pasteAt);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        Location lowerCorner = WorldEditUtils.vectorToLocation(pasteAt);
        Location upperCorner = WorldEditUtils.vectorToLocation(pasteAt.add(clipboard.getSize()));

        return new Arena(
            schematic.getName(),
            copy,
            new Cuboid(lowerCorner, upperCorner)
        );
    }

}