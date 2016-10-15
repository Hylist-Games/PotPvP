package net.frozenorb.potpvp.command;

import net.frozenorb.qlib.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class SetSpawnCommand {

    @Command(names = {"setspawn"}, permission = "op")
    public static void setSpawn(Player sender) {
        Location loc = sender.getLocation();

        sender.getWorld().setSpawnLocation(
            loc.getBlockX(),
            loc.getBlockY(),
            loc.getBlockZ(),
            loc.getYaw(),
            loc.getPitch()
        );

        sender.sendMessage(ChatColor.YELLOW + "Spawn point updated!");
    }

}