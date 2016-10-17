package net.frozenorb.potpvp.lobby;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kit.KitItems;
import net.frozenorb.potpvp.lobby.listener.LobbyGeneralListener;
import net.frozenorb.potpvp.lobby.listener.LobbyItemListener;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyItems;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.queue.QueueItems;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public final class LobbyHandler {

    public LobbyHandler() {
        Bukkit.getPluginManager().registerEvents(new LobbyGeneralListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new LobbyItemListener(), PotPvPSI.getInstance());
    }

    public static void resetInventory(Player player) {
        // previously this prevented players with an open inventory from having
        // their inventories updated (InventoryType.CRAFTING = player inv, apparently)
        // currently kept for historical purposes + a reminder it was here (if need arises)
        /*if (player.getOpenInventory().getType() != InventoryType.CRAFTING) {
            return;
        }*/

        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
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
                    inventory.setItem(1, LobbyItems.START_TEAM_SPLIT_ITEM);
                }
            }

            inventory.setItem(5, LobbyItems.OTHER_PARTIES_ITEM);
            inventory.setItem(6, LobbyItems.PENDING_INVITES_ITEM);
            inventory.setItem(8, PartyItems.LEAVE_PARTY_ITEM);
        } else {
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