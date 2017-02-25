package net.frozenorb.potpvp.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MatchStatusCommand {

    @Command(names = { "match status" }, permission = "")
    public static void matchStatus(CommandSender sender, @Param(name = "target") Player target) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(target);

        if (match == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not playing in or spectating a match.");
            return;
        }

        for (String line : PotPvPSI.getGson().toJson(match).split("\n")) {
            sender.sendMessage("  " + ChatColor.GRAY + line);
        }
    }

}