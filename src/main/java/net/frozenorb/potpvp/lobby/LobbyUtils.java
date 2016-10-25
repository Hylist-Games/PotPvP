package net.frozenorb.potpvp.lobby;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.duels.DuelHandler;
import net.frozenorb.potpvp.duels.DuelItems;
import net.frozenorb.potpvp.kit.KitItems;
import net.frozenorb.potpvp.kit.menu.editkit.EditKitMenu;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyItems;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.queue.QueueItems;
import net.frozenorb.potpvp.rematch.RematchData;
import net.frozenorb.potpvp.rematch.RematchHandler;
import net.frozenorb.potpvp.rematch.RematchItems;
import net.frozenorb.qlib.menu.Menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class LobbyUtils {

    public static void resetInventory(Player player) {
        // prevents players with the kit editor from having their
        // inventory updated (kit items go into their inventory)
        if (Menu.currentlyOpenedMenus.get(player.getName()) instanceof EditKitMenu) {
            return;
        }

        RematchHandler rematchHandler = PotPvPSI.getInstance().getRematchHandler();
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        DuelHandler duelHandler = DuelHandler.instance();

        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        if (party != null) {
            inventory.setItem(0, PartyItems.icon(party));

            if (party.isLeader(player.getUniqueId())) {
                int partySize = party.getMembers().size();

                if (partySize == 2) {
                    if (!queueHandler.isQueued(party)) {
                        inventory.setItem(1, QueueItems.JOIN_PARTY_QUEUE_ITEM);
                    } else {
                        inventory.setItem(1, QueueItems.LEAVE_PARTY_QUEUE_ITEM);
                    }
                } else if (partySize > 2) {
                    inventory.setItem(1, PartyItems.START_TEAM_SPLIT_ITEM);
                }
            }

            inventory.setItem(5, DuelItems.OTHER_PARTIES_ITEM);
            inventory.setItem(6, DuelItems.PENDING_INVITES_ITEM);
            inventory.setItem(8, PartyItems.LEAVE_PARTY_ITEM);
        } else {
            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());

                if (duelHandler.inviteTo(target, player) != null) {
                    // if we've sent an invite to them
                    inventory.setItem(0, RematchItems.SENT_REMATCH_ITEM);
                } else if (duelHandler.inviteTo(player, target) != null) {
                    // if they've sent us an invite
                    inventory.setItem(0, RematchItems.ACCEPT_REMATCH_ITEM);
                } else {
                    // if no one has sent an invite
                    inventory.setItem(0, RematchItems.REQUEST_REMATCH_ITEM);
                }
            }

            if (!queueHandler.isQueued(player.getUniqueId())) {
                inventory.setItem(1, QueueItems.JOIN_SOLO_QUEUE_ITEM);
            } else {
                inventory.setItem(1, QueueItems.LEAVE_SOLO_QUEUE_ITEM);
            }

            inventory.setItem(5, LobbyItems.EVENTS_ITEM);
        }

        inventory.setItem(7, KitItems.OPEN_EDITOR_ITEM);
        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), player::updateInventory, 1L);
    }

}