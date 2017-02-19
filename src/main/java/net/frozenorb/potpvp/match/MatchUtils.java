package net.frozenorb.potpvp.match;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.lobby.LobbyItems;
import net.frozenorb.potpvp.party.PartyHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class MatchUtils {

    public static void resetInventory(Player player) {
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        Match match = matchHandler.getMatchSpectating(player.getUniqueId());

        if (match == null || !match.isSpectator(player.getUniqueId())) {
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        // if they've been on any team or are staff they'll be able to
        // use this item on at least 1 player. if they can't use it all
        // we just don't give it to them (UX purposes)
        boolean canViewInventories = player.hasPermission("potpvp.inventory.all");

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

        // this bit is correct; see SpectatorItems file for more
        if (partyHandler.hasParty(player)) {
            player.getInventory().setItem(8, SpectatorItems.LEAVE_PARTY_ITEM);
        } else {
            player.getInventory().setItem(8, SpectatorItems.RETURN_TO_LOBBY_ITEM);

            if (!followHandler.getFollowing(player).isPresent()) {
                player.getInventory().setItem(4, LobbyItems.SPECTATE_RANDOM_ITEM);
                player.getInventory().setItem(5, LobbyItems.SPECTATE_MENU_ITEM);
            }
        }

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), player::updateInventory, 1L);
    }

}