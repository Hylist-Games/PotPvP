package net.frozenorb.potpvp.util;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class VisibilityUtils {

    public static void updateVisibility(Player target) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (shouldSeePlayer(otherPlayer, target)) {
                otherPlayer.showPlayer(target);
            } else {
                otherPlayer.hidePlayer(target);
            }

            if (shouldSeePlayer(target, otherPlayer)) {
                target.showPlayer(otherPlayer);
            } else {
                target.hidePlayer(otherPlayer);
            }
        }
    }

    private static boolean shouldSeePlayer(Player viewer, Player target) {
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target);

        if (targetMatch == null) {
            // we're not in a match so we hide other players based on their party/match
            Party targetParty = partyHandler.getParty(target);

            boolean viewerPlayingMatch = matchHandler.isPlayingOrSpectatingMatch(viewer);
            boolean viewerSameParty = targetParty != null && targetParty.isMember(viewer.getUniqueId());

            return viewerPlayingMatch || viewerSameParty;
        } else {
            // we're in a match so we only hide other spectators (if our settings say so)
            boolean targetIsSpectator = targetMatch.isSpectator(target.getUniqueId());
            boolean viewerSpecSetting = settingHandler.getSetting(viewer, Setting.VIEW_OTHER_SPECTATORS);
            boolean viewerIsSpectator = matchHandler.isSpectatingMatch(viewer.getUniqueId());

            return !targetIsSpectator || (viewerSpecSetting && viewerIsSpectator && !target.hasMetadata("ModMode"));
        }
    }

}