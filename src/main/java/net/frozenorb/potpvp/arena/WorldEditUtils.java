package net.frozenorb.potpvp.arena;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.schematic.SchematicFormat;

import net.frozenorb.qlib.cuboid.Cuboid;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public final class WorldEditUtils {

    private static EditSession editSession;
    private static com.sk89q.worldedit.world.World worldEditWorld;

    public static void primeWorldEditApi() {
        if (editSession != null) {
            return;
        }

        EditSessionFactory esFactory = WorldEdit.getInstance().getEditSessionFactory();

        worldEditWorld = new BukkitWorld(Bukkit.getWorlds().get(0));
        editSession = esFactory.getEditSession(worldEditWorld, Integer.MAX_VALUE);
    }

    public static CuboidClipboard paste(ArenaSchematic schematic, Vector pasteAt) throws Exception {
        primeWorldEditApi();

        CuboidClipboard clipboard = SchematicFormat.MCEDIT.load(schematic.getSchematicFile());

        // systems like the ArenaGrid assume that pastes will 'begin' directly at the Vector
        // provided. to ensure we can do this, we manually clear any offset (distance from
        // corner of schematic to player) to ensure our pastes aren't dependant on the
        // location of the player when copied
        clipboard.setOffset(new Vector(0, 0, 0));
        clipboard.paste(editSession, pasteAt, true);

        return clipboard;
    }

    public static void save(ArenaSchematic schematic, Vector saveFrom) throws Exception {
        primeWorldEditApi();

        Vector schematicSize = readSchematicSize(schematic);

        CuboidClipboard newSchematic = new CuboidClipboard(saveFrom, saveFrom.add(schematicSize));
        newSchematic.copy(editSession);

        SchematicFormat.MCEDIT.save(newSchematic, schematic.getSchematicFile());
    }

    public static void clear(Cuboid bounds) {
        clear(
            new Vector(bounds.getLowerX(), bounds.getLowerY(), bounds.getLowerZ()),
            new Vector(bounds.getUpperX(), bounds.getUpperY(), bounds.getUpperZ())
        );
    }

    public static void clear(Vector lower, Vector upper) {
        primeWorldEditApi();

        BaseBlock air = new BaseBlock(Material.AIR.getId());
        Region region = new CuboidRegion(worldEditWorld, lower, upper);

        try {
            editSession.setBlocks(region, air);
        } catch (MaxChangedBlocksException ex) {
            // our block change limit is Integer.MAX_VALUE, so will never
            // have to worry about this happening
            throw new RuntimeException(ex);
        }
    }

    public static Vector readSchematicSize(ArenaSchematic schematic) throws Exception {
        File schematicFile = schematic.getSchematicFile();
        CuboidClipboard clipboard = SchematicFormat.MCEDIT.load(schematicFile);

        return clipboard.getSize();
    }

    public static Location vectorToLocation(Vector vector) {
        return new Location(
            Bukkit.getWorlds().get(0),
            vector.getBlockX(),
            vector.getBlockY(),
            vector.getBlockZ()
        );
    }

}