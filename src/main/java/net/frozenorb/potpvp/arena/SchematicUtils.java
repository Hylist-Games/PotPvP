package net.frozenorb.potpvp.arena;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;

import org.bukkit.Bukkit;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SchematicUtils {

    // we use a ton of deperecated methods/classes here - due to the fact we're going to stay
    // on 1.7.10 (and theefore same version of WorldEdit) for a while this isn't too concerning.
    // when we have the time we should learn the WorldEdit API and update this
    public static CuboidClipboard paste(ArenaSchematic schematic, Vector pasteAt) throws Exception {
        EditSession session = new EditSession(new BukkitWorld(Bukkit.getWorlds().get(0)), 999999999);
        CuboidClipboard clipboard = CuboidClipboard.loadSchematic(schematic.getSchematicFile());

        // systems like the ArenaGrid assume that pastes will 'begin' directly at the Vector
        // provided. to ensure we can do this, we manually clear any offset (distance from
        // corner of schematic to player) to ensure our pastes aren't dependant on the
        // location of the player when copied
        clipboard.setOffset(new Vector(0, 0, 0));
        clipboard.paste(session, pasteAt, false);

        return clipboard;
    }

}