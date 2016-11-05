package net.frozenorb.potpvp.arena.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ArenaEnableDisableCommands {

    @Command(names = { "arena enable" }, permission = "op")
    public static void arenaEnable(Player sender, @Param(name="schematic") String schematicName) {
        ArenaSchematic schematic = PotPvPSI.getInstance().getArenaHandler().getSchematic(schematicName);

        if (schematic != null) {
            schematic.setEnabled(true);
            sender.sendMessage(ChatColor.GREEN + schematic.getName() + " is now enabled.");
        } else {
            sender.sendMessage(ChatColor.RED + "Schematic " + schematicName + " not found.");
            sender.sendMessage(ChatColor.RED + "List all schematics with /arena listSchematics");
        }
    }

    @Command(names = { "arena disable" }, permission = "op")
    public static void arenaDisable(Player sender, @Param(name="schematic") String schematicName) {
        ArenaSchematic schematic = PotPvPSI.getInstance().getArenaHandler().getSchematic(schematicName);

        if (schematic != null) {
            schematic.setEnabled(true);
            sender.sendMessage(ChatColor.GREEN + schematic.getName() + " is now enabled.");
        } else {
            sender.sendMessage(ChatColor.RED + "Schematic " + schematicName + " not found.");
            sender.sendMessage(ChatColor.RED + "List all schematics with /arena listSchematics");
        }
    }

}