package net.frozenorb.potpvp.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.PlayerUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PingCommand {

    @Command(names = "ping", permission = "")
    public static void ping(Player sender, @Param(name = "target", defaultValue = "self") Player target) {
        int ping = PlayerUtils.getPing(target);

        sender.sendMessage(target.getDisplayName() + ChatColor.YELLOW + "'s Ping: " + ChatColor.RED + ping);
    }

}
