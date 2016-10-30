package net.frozenorb.potpvp.match.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.qlib.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class LeaveCommand {

    @Command(names = { "spawn", "leave" }, permission = "")
    public static void leave(Player sender) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match spectating = matchHandler.getMatchSpectating(sender);

        if (matchHandler.isPlayingMatch(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot do this while in a match.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Teleporting you to spawn...");

        if (spectating == null) {
            PotPvPSI.getInstance().getLobbyHandler().returnToLobby(sender);
        } else {
            spectating.removeSpectator(sender);
        }
    }

}