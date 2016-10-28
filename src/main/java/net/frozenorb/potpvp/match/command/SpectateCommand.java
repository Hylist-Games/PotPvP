package net.frozenorb.potpvp.match.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class SpectateCommand {

    @Command(names = {"spectate", "spec"}, permission = "op")
    public static void spectate(Player sender, @Param(name = "target") Player target) {
        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You cannot spectate yourself.");
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        matchHandler.getMatchPlayingOrSpectating(target).addSpectator(sender, null);
    }

}