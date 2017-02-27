package net.frozenorb.potpvp.match.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class SpectateCommand {

    @Command(names = {"spectate", "spec"}, permission = "")
    public static void spectate(Player sender, @Param(name = "target") Player target) {
        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You cannot spectate yourself.");
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();

        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target);

        if (targetMatch == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not in a match.");
            return;
        }

        // only check the seting if the target is actually playing in the match
        if (targetMatch.getTeam(target.getUniqueId()) != null && !settingHandler.getSetting(target, Setting.ALLOW_SPECTATORS)) {
            if (sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "Bypassing " + target.getName() + "'s no spectators preference...");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " doesn't allow spectators at the moment.");
                return;
            }
        }

        if (!sender.isOp() && targetMatch.getTeams().size() == 2) {
            MatchTeam teamA = targetMatch.getTeams().get(0);
            MatchTeam teamB = targetMatch.getTeams().get(1);

            if (teamA.getAllMembers().size() == 1 && teamB.getAllMembers().size() == 1) {
                UUID teamAPlayer = teamA.getAllMembers().iterator().next();
                UUID teamBPlayer = teamB.getAllMembers().iterator().next();

                if (
                    !settingHandler.getSetting(Bukkit.getPlayer(teamAPlayer), Setting.ALLOW_SPECTATORS) ||
                    !settingHandler.getSetting(Bukkit.getPlayer(teamBPlayer), Setting.ALLOW_SPECTATORS)
                ) {
                    sender.sendMessage(ChatColor.RED + "Not all players in that 1v1 have spectators enabled.");
                    return;
                }
            }
        }

        Player teleportTo = null;

        // /spectate looks up matches being played OR watched by the target,
        // so we can only target them if they're not spectating
        if (!targetMatch.isSpectator(target.getUniqueId())) {
            teleportTo = target;
        }

        if (PotPvPValidation.canUseSpectateItemIgnoreMatchSpectating(sender)) {
            Match currentlySpectating = matchHandler.getMatchSpectating(sender);

            if (currentlySpectating != null) {
                currentlySpectating.removeSpectator(sender);
            }

            targetMatch.addSpectator(sender, teleportTo);
        }
    }

}