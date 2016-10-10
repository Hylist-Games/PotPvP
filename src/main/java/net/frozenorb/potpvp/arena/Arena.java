package net.frozenorb.potpvp.arena;

import net.frozenorb.qlib.cuboid.Cuboid;

import org.bukkit.Location;

import java.util.Objects;

import lombok.Getter;

/**
 * Represents an arena instance. See {@link net.frozenorb.potpvp.arena}
 * for a comparision of {@link Arena}s and {@link ArenaSchematic}s.
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
     * First spawn location for this arena (team 1)
     */
    @Getter private Location spawn1;

    /**
     * Second spawn location for this arena (team 2)
     */
    @Getter private Location spawn2;

    /**
     * Spectator spawn location for this arena.
     */
    @Getter private Location spectatorSpawn;

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