package net.frozenorb.potpvp.match.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.qlib.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class LeaveCommand {

    @Command(names = { "spawn", "leave" }, permission = "")
    public static void leave(Player sender) {
        sender.sendMessage(ChatColor.YELLOW + "Leaving match...");

        Match spectating = PotPvPSI.getInstance().getMatchHandler().getMatchSpectating(sender);

        if (spectating == null) {
            sender.sendMessage(ChatColor.RED + "You are not currently spectating a match.");
        } else {
            spectating.removeSpectator(sender);
        }
    }

}