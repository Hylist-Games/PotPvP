package net.frozenorb.potpvp.arena.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.Arena;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ArenaViewSchematicCommand {

    @Command(names = { "arena viewSchematic" }, permission = "op")
    public static void arenaViewSchematic(Player sender, @Param(name="schematic") String schematicName) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        ArenaSchematic schematic = arenaHandler.getSchematic(schematicName);

        if (schematic == null) {
            sender.sendMessage(ChatColor.RED + "Schematic " + schematicName + " not found.");
            sender.sendMessage(ChatColor.RED + "List all schematics with /arena listSchematics");
            return;
        }

        int totalCopies = 0;
        int inUseCopies = 0;

        for (Arena arena : arenaHandler.getArenas(schematic)) {
            totalCopies++;

            if (arena.isInUse()) {
                inUseCopies++;
            }
        }

        sender.sendMessage(ChatColor.RED + "------ " + ChatColor.WHITE + schematic.getName() + ChatColor.RED + " ------");
        sender.sendMessage(ChatColor.GREEN + "Enabled: " + ChatColor.WHITE + (schematic.isEnabled() ? "Yes" : "No"));
        sender.sendMessage(ChatColor.GREEN + "Max player count: " + ChatColor.WHITE + schematic.getMaxPlayerCount());
        sender.sendMessage(ChatColor.GREEN + "Min player count: " + ChatColor.WHITE + schematic.getMinPlayerCount());
        sender.sendMessage(ChatColor.GREEN + "Supports ranked: " + ChatColor.WHITE + (schematic.isSupportsRanked() ? "Yes" : "No"));
        sender.sendMessage(ChatColor.GREEN + "Archer only: " + ChatColor.WHITE + (schematic.isArcherOnly() ? "Yes" : "No"));
        sender.sendMessage(ChatColor.GREEN + "Copies: " + ChatColor.WHITE + totalCopies);
        sender.sendMessage(ChatColor.GREEN + "Copies in use: " + ChatColor.WHITE + inUseCopies);
    }

}