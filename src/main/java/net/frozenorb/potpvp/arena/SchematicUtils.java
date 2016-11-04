package net.frozenorb.potpvp.arena;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;

import org.bukkit.Bukkit;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SchematicUtils {

    public static CuboidClipboard paste(ArenaSchematic schematic, Vector pasteAt) throws Exception {
        EditSession session = new EditSession(new BukkitWorld(Bukkit.getWorlds().get(0)), 999999999);
        CuboidClipboard clipboard = CuboidClipboard.loadSchematic(schematic.getSchematicFile());

        clipboard.paste(session, pasteAt, true);
        return clipboard;
    }

}