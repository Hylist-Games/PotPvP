package net.frozenorb.potpvp.match.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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

        if (!settingHandler.getSetting(target, Setting.ALLOW_SPECTATORS)) {
            if (sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "Bypassing " + target.getName() + "'s no spectators preference...");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " doesn't allow spectators at the moment.");
                return;
            }
        }

        Player teleportTo = null;

        // /spectate looks up matches being played OR watched by the target,
        // so we can only target them if they're not spectating
        if (!targetMatch.isSpectator(target.getUniqueId())) {
            teleportTo = target;
        }

        if (PotPvPValidation.canUseSpectateItem(sender)) {
            targetMatch.addSpectator(sender, teleportTo);
        }
    }

}