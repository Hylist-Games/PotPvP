package net.frozenorb.potpvp.arena.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;

public final class ArenaEnableDisableCommands {

    @Command(names = { "arena enable" }, permission = "op")
    public static void arenaEnable(Player sender, @Param(name="schematic") String schematicName) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        ArenaSchematic schematic = arenaHandler.getSchematic(schematicName);

        if (schematic != null) {
            schematic.setEnabled(true);
            sender.sendMessage(ChatColor.GREEN + schematic.getName() + " is now enabled.");
        } else {
            sender.sendMessage(ChatColor.RED + "Schematic " + schematicName + " not found.");
            sender.sendMessage(ChatColor.RED + "List all schematics with /arena listSchematics");
        }

        try {
            arenaHandler.saveSchematics();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Command(names = { "arena disable" }, permission = "op")
    public static void arenaDisable(Player sender, @Param(name="schematic") String schematicName) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        ArenaSchematic schematic = arenaHandler.getSchematic(schematicName);

        if (schematic != null) {
            schematic.setEnabled(false);
            sender.sendMessage(ChatColor.RED + schematic.getName() + " is now disabled.");
        } else {
            sender.sendMessage(ChatColor.RED + "Schematic " + schematicName + " not found.");
            sender.sendMessage(ChatColor.RED + "List all schematics with /arena listSchematics");
        }

        try {
            arenaHandler.saveSchematics();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}