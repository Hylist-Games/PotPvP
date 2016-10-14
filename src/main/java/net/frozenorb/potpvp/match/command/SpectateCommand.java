package net.frozenorb.potpvp.match.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class SpectateCommand {

    @Command(names = {"spectate", "spec"}, permission = "")
    public static void spectate(Player sender, @Param(name = "target") UUID target) {
        if (sender.getUniqueId().equals(target)) {
            sender.sendMessage(ChatColor.RED + "You cannot spectate yourself.");
            return;
        }

        /*if (WaitingForGcd.partyFromCache(sender.getUniqueId()) != null) {
            sender.sendMessage(ChatColor.RED + "You cannot spectate while in your party. Leave to /spec");
            return;
        }

        PotPvPLobby.getInstance().getMatchHandler().requestSpectate(target, sender);*/
    }

}