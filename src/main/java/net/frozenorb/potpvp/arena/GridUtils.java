package net.frozenorb.potpvp.arena;

import com.sk89q.worldedit.Vector;

import net.frozenorb.qlib.cuboid.Cuboid;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.material.Sign;

import java.util.EnumMap;
import java.util.Map;

public final class GridUtils {
    private static final Map<BlockFace, Integer> NOTCHES = new EnumMap<>(BlockFace.class);
    private static final BlockFace[] RADIAL = {
            BlockFace.WEST,
            BlockFace.NORTH_WEST,
            BlockFace.NORTH,
            BlockFace.NORTH_EAST,
            BlockFace.EAST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH,
            BlockFace.SOUTH_WEST
    };

    private GridUtils() {
    }

    static {
        for (int i = 0; i < RADIAL.length; i++) {
            NOTCHES.put(RADIAL[i], i);
        }
    }

    /* Helper methods */

    private static int wrapAngle(int angle) {
        int wrappedAngle = angle;

        while (wrappedAngle <= -180) {
            wrappedAngle += 360;
        }

        while (wrappedAngle > 180) {
            wrappedAngle -= 360;
        }

        return wrappedAngle;
    }

    private static int faceToYaw(final BlockFace face) {
        return wrapAngle(45 * faceToNotch(face));
    }

    private static int faceToNotch(BlockFace face) {
        return NOTCHES.get(face) == null ? 0 : NOTCHES.get(face);
    }

    private static Vector relativeTo(Vector start, Location from) {
        return new Vector(from.getX(), from.getY(), from.getZ()).subtract(start);
    }

    public static Location locationFrom(Vector vector, World world) {
        return new Location(world, vector.getX(), vector.getY(), vector.getZ());
    }

    public static Vector[] getSpawns(Vector start, Cuboid cuboid) {
        Vector[] spawns = new Vector[3]; // 0 = spectator, 1 = team 1, 2 = team 2

            cuboid.forEach((block) -> {
            if (block.getType() == Material.SIGN_POST || block.getType() == Material.SIGN) {
                Location cloned = block.getLocation().clone().add(0.5, 1.5, 0.5);
                cloned.setYaw(faceToYaw(((Sign) block.getState().getData()).getFacing()) + 90);

                String[] lines = ((org.bukkit.block.Sign) block.getState()).getLines();

                Integer numba = 0;

                try {
                    numba = Integer.parseInt(lines[0]);
                } catch (Exception ignored) {}

                if (numba > 0) { // TODO look more into this, should the number just be plugged in like this
                    spawns[numba - 1] = relativeTo(start, cloned);
                }

            } else if (block.getType() == Material.SKULL) {
                Skull skull = (Skull) block.getState();
                Location cloned = block.getLocation().clone().add(0.5, 1.5, 0.5);
                BlockFace facing = skull.getRotation();

                cloned.setYaw(faceToYaw(facing) + 90);
                String owner = skull.hasOwner() ? skull.getOwner() : "";

                Integer numba = 0;

                try {
                    numba = Integer.parseInt(owner);
                } catch (Exception ignored) {}

                if (numba > 0) { // TODO see todo above
                    spawns[numba - 1] = relativeTo(start, cloned);
                }
            }
        });

        return spawns;
    }
}
