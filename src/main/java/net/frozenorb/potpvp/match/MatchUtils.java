package net.frozenorb.potpvp.match;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class MatchUtils {

    public static void resetInventory(Player player) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchSpectating(player.getUniqueId());

        if (match == null) {
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        // if they've been on any team or are staff they'll be able to
        // use this item on at least 1 player. if they can't use it all
        // we just don't give it to them (UX purposes)
        boolean canViewInventories = player.hasPermission("basic.staff");

        if (!canViewInventories) {
            for (MatchTeam team : match.getTeams()) {
                if (team.getAllMembers().contains(player.getUniqueId())) {
                    canViewInventories = true;
                    break;
                }
            }
        }

        // fill inventory with spectator items
        player.getInventory().setItem(0, SpectatorItems.CARPET_ITEM);
        player.getInventory().setItem(1, SpectatorItems.TOGGLE_SPECTATORS_ITEM);

        if (canViewInventories) {
            player.getInventory().setItem(2, SpectatorItems.VIEW_INVENTORY_ITEM);
        }

        player.getInventory().setItem(8, SpectatorItems.RETURN_TO_LOBBY_ITEM);

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), player::updateInventory, 1L);
    }

    public static void updateVisibility(Player updateFor) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();

        Match updateForMatch = matchHandler.getMatchPlayingOrSpectating(updateFor);

        if (updateForMatch == null) {
            return;
        }

        // reads as "updateFor is spectator?"
        boolean updateForIsSpectator = updateForMatch.isSpectator(updateFor.getKiller().getUniqueId());

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