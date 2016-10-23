package net.frozenorb.potpvp.arena.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.Arena;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.potpvp.util.LocationUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ArenaViewArenaCommand {

    @Command(names = { "arena viewArena" }, permission = "op")
    public static void arenaViewArena(Player sender, @Param(name="schematic") String schematicName, @Param(name="copy") int copy) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        ArenaSchematic schematic = arenaHandler.getSchematic(schematicName);

        if (schematic == null) {
            sender.sendMessage(ChatColor.RED + "Schematic " + schematicName + " not found.");
            sender.sendMessage(ChatColor.RED + "List all schematics with /arena listSchematics");
            return;
        }

        Arena arena = arenaHandler.getArena(schematic, copy);

        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "Arena " + schematicName + " " + copy + " not found.");
            sender.sendMessage(ChatColor.RED + "List all arenas with /arena listArenas " + schematic.getName());
            return;
        }

        Location boundsLower = arena.getBounds().getLowerNE();
        Location boundsUpper = arena.getBounds().getUpperSW();

        sender.sendMessage(ChatColor.RED + "------ " + ChatColor.WHITE + schematic.getName() + " " + copy + ChatColor.RED + " ------");
        sender.sendMessage(ChatColor.GREEN + "Bounds: " + ChatColor.WHITE + LocationUtils.locToStr(boundsLower) + " -> " + LocationUtils.locToStr(boundsUpper));
        sender.sendMessage(ChatColor.GREEN + "Team 1 spawn: " + ChatColor.WHITE + LocationUtils.locToStr(arena.getTeam1Spawn()));
        sender.sendMessage(ChatColor.GREEN + "Team 2 spawn: " + ChatColor.WHITE + LocationUtils.locToStr(arena.getTeam2Spawn()));
        sender.sendMessage(ChatColor.GREEN + "Spectator spawn: " + ChatColor.WHITE + LocationUtils.locToStr(arena.getSpectatorSpawn()));
        sender.sendMessage(ChatColor.GREEN + "In use: " + ChatColor.WHITE + (arena.isInUse() ? "Yes" : "No"));
    }

}