package net.frozenorb.potpvp.util;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class VisibilityUtils {

    public static void updateVisibility(Player updateFor) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();

        Match updateForMatch = matchHandler.getMatchPlayingOrSpectating(updateFor);

        if (updateForMatch == null) {
            // we're not in a match so we see no one, and no one sees us.
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                updateFor.hidePlayer(otherPlayer);
                otherPlayer.hidePlayer(updateFor);
            }

            return;
        }

        // reads as "updateFor is spectator?"
        boolean updateForIsSpectator = updateForMatch.isSpectator(updateFor.getUniqueId());

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            // we don't care about if we can see ourself
            if (updateFor == otherPlayer) {
                continue;
            }

            MatchTeam otherPlayerTeam = updateForMatch.getTeam(otherPlayer.getUniqueId());
            boolean otherPlayerIsSpectator = updateForMatch.isSpectator(otherPlayer.getUniqueId());

            // we're not in a match together, don't show each other
            if (otherPlayerTeam == null && !otherPlayerIsSpectator) {
                updateFor.hidePlayer(otherPlayer);
                otherPlayer.hidePlayer(updateFor);
                continue;
            }

            if (updateForIsSpectator) {
                // we're a spectator

                if (otherPlayerIsSpectator) {
                    // we're a spectator, show us them based on our settings
                    if (settingHandler.getSetting(updateFor.getUniqueId(), Setting.VIEW_OTHER_SPECTATORS)) {
                        updateFor.showPlayer(otherPlayer);
                    } else {
                        updateFor.hidePlayer(otherPlayer);
                    }

                    // they're a spectator, show them us based on their settings
                    if (settingHandler.getSetting(otherPlayer.getUniqueId(), Setting.VIEW_OTHER_SPECTATORS)) {
                        otherPlayer.showPlayer(updateFor);
                    } else {
                        otherPlayer.hidePlayer(updateFor);
                    }
                } else {
                    // they're in the match, show us them
                    // we're a spectator and they're in the match, don't show them us
                    updateFor.showPlayer(otherPlayer);
                    otherPlayer.hidePlayer(updateFor);
                }
            } else {
                // we're playing in the match

                if (otherPlayerIsSpectator) {
                    // they're a spectator,  don't show us them
                    // we're in the match and they're a spectator, show them us
                    updateFor.hidePlayer(otherPlayer);
                    otherPlayer.showPlayer(updateFor);
                } else {
                    // we're both in the match, show each other
                    updateFor.showPlayer(otherPlayer);
                    otherPlayer.showPlayer(updateFor);
                }
            }
        }
    }

}