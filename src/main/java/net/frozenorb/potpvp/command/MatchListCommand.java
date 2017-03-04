package net.frozenorb.potpvp.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.qlib.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class MatchListCommand {

    @Command(names = { "match list" }, permission = "op")
    public static void matchList(Player sender) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        for (Match match : matchHandler.getHostedMatches()) {
            sender.sendMessage(ChatColor.RED + match.getSimpleDescription(true));
        }
    }

}