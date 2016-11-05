package net.frozenorb.potpvp.arena.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;

public final class ArenaCreateSchematicCommand {

    @Command(names = { "arena createSchematic" }, permission = "op")
    public static void arenaCreateSchematic(Player sender, @Param(name="schematic") String schematicName) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();

        if (arenaHandler.getSchematic(schematicName) != null) {
            sender.sendMessage(ChatColor.RED + "Schematic " + schematicName + " already exists");
            return;
        }

        ArenaSchematic schematic = new ArenaSchematic(schematicName);
        arenaHandler.registerSchematic(schematic);

        try {
            arenaHandler.saveSchematics();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        sender.sendMessage(ChatColor.GREEN + "Schematic created.");
    }

}