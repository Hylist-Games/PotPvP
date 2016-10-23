package net.frozenorb.potpvp.arena;

import lombok.AccessLevel;
import lombok.Setter;
import net.frozenorb.qlib.cuboid.Cuboid;

import org.bukkit.Location;

import java.util.Objects;
import java.util.function.Predicate;

import lombok.Getter;

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
    @Getter private Location spectatorSpawn;

    /**
     * If this arena is currently being used
     * @see ArenaHandler#allocateUnusedArena(Predicate)
     * @see ArenaHandler#releaseArena(Arena)
     */
    // AccessLevel.NONE so arenas can only marked as in use
    // or not in use by the appropriate methods in ArenaHandler
    @Getter @Setter(AccessLevel.PACKAGE) private transient boolean inUse;

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