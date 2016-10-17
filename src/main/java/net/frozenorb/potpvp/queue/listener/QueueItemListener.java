package net.frozenorb.potpvp.queue.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.menu.SelectKitTypeMenu;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.queue.QueueItems;
import net.frozenorb.potpvp.validation.PotPvPValidation;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class QueueItemListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item.isSimilar(QueueItems.JOIN_SOLO_QUEUE_ITEM)) {
            event.setCancelled(true);

            // try to check validation issues in advance
            // (will be called again in QueueHandler#joinQueue)
            if (PotPvPValidation.canJoinQueue(player)) {
                new SelectKitTypeMenu(kitType -> {
                    queueHandler.joinQueue(player, kitType);
                    event.getPlayer().closeInventory();
                }).openMenu(player);
            }
        } else if (item.isSimilar(QueueItems.LEAVE_SOLO_QUEUE_ITEM)) {
            event.setCancelled(true);
            queueHandler.leaveQueue(player);
        } else if (item.isSimilar(QueueItems.JOIN_PARTY_QUEUE_ITEM)) {
            event.setCancelled(true);

            Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

            // just fail silently, players who aren't a leader
            // of a party shouldn't even have this item
            if (party == null || !party.isLeader(player.getUniqueId())) {
                return;
            }

            // try to check validation issues in advance
            // (will be called again in QueueHandler#joinQueue)
            if (PotPvPValidation.canJoinQueue(player)) {
                new SelectKitTypeMenu(kitType -> {
                    queueHandler.joinQueue(party, kitType);
                    event.getPlayer().closeInventory();
                }).openMenu(player);
            }
        } else if (item.isSimilar(QueueItems.LEAVE_PARTY_QUEUE_ITEM)) {
            event.setCancelled(true);

            Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

            // just fail silently, players who aren't a leader
            // of a party shouldn't even have this item
            if (party != null && party.isLeader(player.getUniqueId())) {
                queueHandler.leaveQueue(party);
            }
        }
    }

}