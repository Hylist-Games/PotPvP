package net.frozenorb.potpvp.match.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.qLib;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MatchStatusCommand {

    @Command(names = { "match status" }, permission = "")
    public static void matchStatus(CommandSender sender, @Param(name = "target") Player target) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        sendMatchInfo(sender, "Cached", matchHandler.getMatchPlaying(target));

        for (Match match : matchHandler.getHostedMatches()) {
            if (match.getTeam(target.getUniqueId()) != null) {
                sendMatchInfo(sender, "Actual", match);
                return;
            }
        }

        sendMatchInfo(sender, "Actual", null);
    }

    private static void sendMatchInfo(CommandSender sender, String title, Match match) {
        if (match == null) {
            sender.sendMessage(ChatColor.RED + title + " match not found.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + title + ":");

        for (String line : qLib.GSON.toJson(match).split("\n")) {
            sender.sendMessage("  " + ChatColor.GRAY + line);
        }
    }

}