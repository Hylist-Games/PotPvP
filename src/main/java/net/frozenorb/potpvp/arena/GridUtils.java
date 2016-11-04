package net.frozenorb.potpvp.arena;

import org.bukkit.block.BlockFace;

import java.util.EnumMap;
import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass
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

    static {
        for (int i = 0; i < RADIAL.length; i++) {
            NOTCHES.put(RADIAL[i], i);
        }
    }

    public static int faceToYaw(BlockFace face) {
        return wrapAngle(45 * NOTCHES.getOrDefault(face, 0));
    }

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

}