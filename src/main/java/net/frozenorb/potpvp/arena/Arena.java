package net.frozenorb.potpvp.arena;

import lombok.*;
import net.frozenorb.qlib.cuboid.Cuboid;

import org.bukkit.Location;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents a pasted instance of an {@link ArenaSchematic}.
 * See {@link net.frozenorb.potpvp.arena} for a comparision of
 * {@link Arena}s and {@link ArenaSchematic}s.
 */
@AllArgsConstructor
@NoArgsConstructor
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