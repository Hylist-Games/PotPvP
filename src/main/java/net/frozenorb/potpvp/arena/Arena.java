package net.frozenorb.potpvp.arena;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.util.AngleUtils;
import net.frozenorb.qlib.cuboid.Cuboid;
import net.frozenorb.qlib.util.Callback;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;

import java.util.Objects;
import java.util.function.Predicate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a pasted instance of an {@link ArenaSchematic}.
 * See {@link net.frozenorb.potpvp.arena} for a comparision of
 * {@link Arena}s and {@link ArenaSchematic}s.
 */
public final class Arena {

    /**
     * The name of the {@link ArenaSchematic} this Arena is
     * copied from.
     * @see net.frozenorb.potpvp.arena
     */
    @Getter private String schematic;

    /**
     * What number copy this arena is.
     * @see net.frozenorb.potpvp.arena
     */
    @Getter private int copy;

    /**
     * Bounding box for this arena
     */
    @Getter private Cuboid bounds;

    /**
     * Team 1 spawn location for this arena
     * For purposes of arena definition we ignore
     * non-two-teamed matches.
     */
    @Getter private Location team1Spawn;

    /**
     * Team 2 spawn location for this arena
     * For purposes of arena definition we ignore
     * non-two-teamed matches.
     */
    @Getter private Location team2Spawn;

    /**
     * Spectator spawn location for this arena
     */
    private Location spectatorSpawn;

    /**
     * If this arena is currently being used
     * @see ArenaHandler#allocateUnusedArena(Predicate)
     * @see ArenaHandler#releaseArena(Arena)
     */
    // AccessLevel.NONE so arenas can only marked as in use
    // or not in use by the appropriate methods in ArenaHandler
    @Getter @Setter(AccessLevel.PACKAGE) private transient boolean inUse;

    public Arena() {} // for gson

    public Arena(String schematic, int copy, Cuboid bounds) {
        this.schematic = Preconditions.checkNotNull(schematic);
        this.copy = copy;
        this.bounds = Preconditions.checkNotNull(bounds);

        scanLocations();
    }

    public Location getSpectatorSpawn() {
        // if it's been defined in the actual map file or calculated before
        if (spectatorSpawn != null) {
            return spectatorSpawn;
        }

        int xDiff = Math.abs(team1Spawn.getBlockX() - team2Spawn.getBlockX());
        int yDiff = Math.abs(team1Spawn.getBlockY() - team2Spawn.getBlockY());
        int zDiff = Math.abs(team1Spawn.getBlockZ() - team2Spawn.getBlockZ());

        int newX = Math.min(team1Spawn.getBlockX(), team2Spawn.getBlockX()) + (xDiff / 2);
        int newY = Math.min(team1Spawn.getBlockY(), team2Spawn.getBlockY()) + (yDiff / 2);
        int newZ = Math.min(team1Spawn.getBlockZ(), team2Spawn.getBlockZ()) + (zDiff / 2);

        spectatorSpawn = new Location(team1Spawn.getWorld(), newX, newY, newZ);

        while (spectatorSpawn.getBlock().getType().isSolid()) {
            spectatorSpawn = spectatorSpawn.add(0, 1, 0);
        }

        return spectatorSpawn;
    }

    private void scanLocations() {
        // iterating the cuboid doesn't work because
        // its iterator is broken :(
        forEachBlock(block -> {
            Material type = block.getType();

            if (type != Material.SKULL) {
                return;
            }

            Skull skull = (Skull) block.getState();

            Location skullLocation = block.getLocation().clone().add(0.5, 1.5, 0.5);
            skullLocation.setYaw(AngleUtils.faceToYaw(skull.getRotation()) + 90);

            switch (skull.getSkullType()) {
                case SKELETON:
                    spectatorSpawn = skullLocation;

                    block.setType(Material.AIR);
                    block.getRelative(BlockFace.DOWN).setType(Material.AIR);
                    break;
                case PLAYER:
                    if (team1Spawn == null) {
                        team1Spawn = skullLocation;
                    } else {
                        team2Spawn = skullLocation;
                    }

                    block.setType(Material.AIR);
                    block.getRelative(BlockFace.DOWN).setType(Material.AIR);
                    break;
                default:
                    break;
            }
        });

        Preconditions.checkNotNull(spectatorSpawn, "Spectator spawn (skeleton skull) cannot be null.");
        Preconditions.checkNotNull(team1Spawn, "Team 1 spawn (player skull) cannot be null.");
        Preconditions.checkNotNull(team2Spawn, "Team 2 spawn (player skull) cannot be null.");
    }

    public void forEachBlock(Callback<Block> callback) {
        Location start = bounds.getLowerNE();
        Location end = bounds.getUpperSW();
        World world = bounds.getWorld();

        for (int x = start.getBlockX(); x < end.getBlockX(); x++) {
            for (int y = start.getBlockY(); y < end.getBlockY(); y++) {
                for (int z = start.getBlockZ(); z < end.getBlockZ(); z++) {
                    callback.callback(world.getBlockAt(x, y, z));
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Arena) {
            Arena a = (Arena) o;
            return a.schematic.equals(schematic) && a.copy == copy;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(schematic, copy);
    }

}