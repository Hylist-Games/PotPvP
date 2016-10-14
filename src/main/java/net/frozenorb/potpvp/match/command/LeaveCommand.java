package net.frozenorb.potpvp.match.command;

import net.frozenorb.qlib.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class LeaveCommand {

    @Command(names = { "spawn", "leave" }, permission = "")
    public static void leave(Player sender) {
        sender.sendMessage(ChatColor.YELLOW + "Leaving match...");

        /*PotPvPSlave.getInstance().getGcdClient().leaveMatch(
            new LeaveMatchRequest(sender.getUniqueId()),
            (ignored) -> {}, // ignore the result, they're just leaving.
            (ex) -> {
                sender.sendMessage(PotPvPMessages.COULD_NOT_CONTACT_MATCH_SERVER);
                sender.sendMessage(ChatColor.RED + "Please return to a hub with /hub.");
            }
        );*/
    }

}