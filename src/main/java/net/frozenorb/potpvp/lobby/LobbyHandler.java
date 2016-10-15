package net.frozenorb.potpvp.lobby;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kit.KitEditorItems;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.party.PartyItems;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.queue.QueueItems;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public final class LobbyHandler {

    public LobbyHandler() {
        Bukkit.getPluginManager().registerEvents(new LobbyListener(), PotPvPSI.getInstance());
    }

    public static void resetInventory(Player player) {
        if (player.getOpenInventory().getType() != InventoryType.CRAFTING) {
            return;
        }

        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        Party party = partyHandler.getParty(player);

        if (party != null) {
            player.getInventory().setItem(0, PartyItems.icon(party));

            if (party.isLeader(player.getUniqueId())) {
                int partySize = party.getMembers().size();

                if (partySize == 2) {
                    if (!queueHandler.isQueued(party)) {
                        player.getInventory().setItem(1, QueueItems.JOIN_PARTY_QUEUE_ITEM);
                    } else {
                        player.getInventory().setItem(1, QueueItems.LEAVE_PARTY_QUEUE_ITEM);
                    }
                } else if (partySize > 2) {
                    player.getInventory().setItem(1, LobbyItems.START_TEAM_SPLIT_ITEM);
                }
            }

            player.getInventory().setItem(5, LobbyItems.OTHER_PARTIES_ITEM);
            player.getInventory().setItem(6, LobbyItems.PENDING_INVITES_ITEM);
            player.getInventory().setItem(8, LobbyItems.LEAVE_PARTY_ITEM);
        } else {
            if (!queueHandler.isQueued(player.getUniqueId())) {
                player.getInventory().setItem(1, QueueItems.JOIN_SOLO_QUEUE_ITEM);
            } else {
                player.getInventory().setItem(1, QueueItems.LEAVE_SOLO_QUEUE_ITEM);
            }

            player.getInventory().setItem(5, LobbyItems.EVENTS_ITEM);
        }

        player.getInventory().setItem(7, KitEditorItems.OPEN_EDITOR_ITEM);
        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), player::updateInventory, 1L);
    }

}