package net.frozenorb.potpvp.arena;

import java.io.File;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an arena schematic. See {@link net.frozenorb.potpvp.arena}
 * for a comparision of {@link Arena}s and {@link ArenaSchematic}s.
 */
public final class ArenaSchematic {

    /**
     * Name of this schematic (ex "Candyland")
     */
    @Getter private String name;

    /**
     * Maximum number of players that can occupy an instance of this arena.
     * Some small schematics should only be used for smaller fights
     */
    @Getter private int maxPlayerCount;

    /**
     * Minimum number of players that can occupy an instance of this arena.
     * Some large schematics should only be used for larger fights
     */
    @Getter private int minPlayerCount;

    /**
     * If this schematic can be used for ranked matches
     * Some "joke" schematics cannot be used for ranked (due to their nature)
     */
    @Getter private boolean supportsRanked;

    /**
     * If this schematic can be only be used for archer matches
     * Some schematics are built for specifically archer fights
     */
    @Getter private boolean archerOnly;

    /**
     * Index on the X axis on the grid. 0 if not set yet
     * @see ArenaGrid
     */
    @Getter @Setter(AccessLevel.PACKAGE) private int gridIndex = 0;

    public File getSchematicFile() {
        return new File(ArenaHandler.WORLD_EDIT_SCHEMATICS_FOLDER, name + ".schematic");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ArenaSchematic && ((ArenaSchematic) o).name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}