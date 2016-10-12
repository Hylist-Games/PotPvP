package net.frozenorb.potpvp.queue;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.PotPvPPlugin;
import net.frozenorb.potpvp.kittype.DetailedKitType;
import net.frozenorb.potpvp.kit.menu.SelectDetailedKitTypeMenu;
import net.frozenorb.potpvp.PotPvPLobby;
import net.frozenorb.potpvp.LobbyItems;
import net.frozenorb.potpvp.validation.PotPvPValidation;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

final class QueueItemListener implements Listener {

    private final QueueHandler queueHandler;

    QueueItemListener(QueueHandler queueHandler) {
        this.queueHandler = Preconditions.checkNotNull(queueHandler, "queueHandler");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();

        /*
            There's a lot of if statements here but it does break down well.
            If statements (in order):

            Join quick match queue (party)
            Join ranked queue (party)
            Leave quick match / ranked queue (party)

            Join quick match queue (solo)
            Join ranked queue (solo)
            Leave quick match / ranked queue (solo)
         */
        if (item.isSimilar(QueueItems.JOIN_PARTY_QUICK_MATCH_QUEUE_ITEM)) {

        } else if (item.isSimilar(QueueItems.JOIN_PARTY_RANKED_QUEUE_ITEM)) {

        } else if (item.isSimilar(QueueItems.LEAVE_PARTY_QUICK_MATCH_QUEUE_ITEM) || item.isSimilar(QueueItems.LEAVE_PARTY_RANKED_QUEUE_ITEM)) {

        } else if (item.isSimilar(QueueItems.JOIN_SOLO_QUICK_MATCH_QUEUE_ITEM)) {

        } else if (item.isSimilar(QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM)) {

        } else if (item.isSimilar(QueueItems.LEAVE_SOLO_QUICK_MATCH_QUEUE_ITEM) || item.isSimilar(QueueItems.LEAVE_SOLO_RANKED_QUEUE_ITEM)) {

        }

        if (event.getItem().isSimilar(LobbyItems.JOIN_RANKED_QUEUE_ITEM)) {
            event.setCancelled(true); // the item used is throwable

            if (PotPvPLobby.getInstance().getMatchHandler().isRankedMatchesDisabled()) {
                event.getPlayer().sendMessage(ChatColor.RED + "Ranked matches are currently disabled!");
                return;
            }

            if (!PotPvPValidation.validatePlayerForRanked(event.getPlayer())) {
                return;
            }

            new SelectDetailedKitTypeMenu((detailedKitType) -> {
                event.getPlayer().closeInventory();
            }).openMenu(event.getPlayer());

            (new SelectDetailedKitTypeMenu(new SelectDetailedKitTypeMenu.DetailedKitTypeCallback() {

                @Override
                public void selected(DetailedKitType kitType) {
                    new BukkitRunnable() {

                        public void run() {
                            PotPvPLobby.getInstance().getQueueHandler().addToSoloRankedQueue(event.getPlayer(), kitType);
                        }

                    }.runTaskLater(PotPvPLobby.getInstance(), 1L);

                    event.getPlayer().closeInventory();
                }

                @Override
                public void cancelled() {} // Nothing

            })).openMenu(event.getPlayer());
        } else if (event.getItem().isSimilar(LobbyItems.LEAVE_RANKED_QUEUE_ITEM) || (event.getItem().isSimilar(LobbyItems.LEAVE_QUICK_MATCH_QUEUE_ITEM))) {
            event.setCancelled(true); // the item used is throwable.

            PotPvPLobby.getInstance().getQueueHandler().removeFromQueues(event.getPlayer().getUniqueId());
            PotPvPPlugin.getInstance().getInventoryResetHandler().resetInventory(event.getPlayer());
        } else if (event.getItem().isSimilar(LobbyItems.JOIN_QUICK_MATCH_QUEUE_ITEM)) {
            event.setCancelled(true); // the item used is throwable

            if (!PotPvPValidation.validatePlayerForQuickmatch(event.getPlayer())) {
                return;
            }

            (new SelectDetailedKitTypeMenu(new SelectDetailedKitTypeMenu.DetailedKitTypeCallback() {

                @Override
                public void selected(DetailedKitType detailedKitType) {
                    new BukkitRunnable() {

                        public void run() {
                            PotPvPLobby.getInstance().getQueueHandler().joinQuickMatchQueue(event.getPlayer(), detailedKitType);
                        }

                    }.runTaskLater(PotPvPLobby.getInstance(), 1L);
                    event.getPlayer().closeInventory();
                }

                @Override
                public void cancelled() {} // Nothing

            })).openMenu(event.getPlayer());
        }

        else if (event.getItem().isSimilar(LobbyItems.JOIN_2V2_RANKED_ITEM)) {
            event.setCancelled(true); // The item we use for this is throwable, so we cancel it.

            final Party party = PotPvPLobby.getInstance().getPartyHandler().getLocalParty(event.getPlayer());

            // Ensure party exists and is valid for ranked queue
            if (party != null && PotPvPValidation.validatePartyForRanked(party)) {
                (new SelectDetailedKitTypeMenu(new SelectDetailedKitTypeMenu.DetailedKitTypeCallback() {

                    @Override
                    public void selected(DetailedKitType kitType) {
                        new BukkitRunnable() {

                            public void run() {
                                PotPvPLobby.getInstance().getQueueHandler().addToPartyRankedQueue(party, kitType);
                            }

                        }.runTaskLater(PotPvPLobby.getInstance(), 1L);

                        event.getPlayer().closeInventory();
                    }

                    @Override
                    public void cancelled() {} // Nothing

                })).openMenu(event.getPlayer());
            }
        } else if (event.getItem().isSimilar(LobbyItems.JOIN_2V2_QUICK_MATCH_ITEM)) {
            event.setCancelled(true); // The item we use for this is throwable, so we cancel it.

            final Party party = PotPvPLobby.getInstance().getPartyHandler().getLocalParty(event.getPlayer());

                /* Ensure party exists and is valid for quick match queue */
            if (party != null && PotPvPValidation.validatePartyForQuickmatch(party)) {
                (new SelectDetailedKitTypeMenu(new SelectDetailedKitTypeMenu.DetailedKitTypeCallback() {

                    @Override
                    public void selected(DetailedKitType detailedKitType) {
                        new BukkitRunnable() {

                            public void run() {
                                PotPvPLobby.getInstance().getQueueHandler().joinQuickMatchQueue(party, detailedKitType);
                            }

                        }.runTaskLater(PotPvPLobby.getInstance(), 1L);

                        event.getPlayer().closeInventory();
                    }

                    @Override
                    public void cancelled() {} // Nothing

                })).openMenu(event.getPlayer());
            }
        } else if (event.getItem().isSimilar(LobbyItems.LEAVE_2V2_QUICK_MATCH_ITEM) || (event.getItem().isSimilar(LobbyItems.LEAVE_2V2_RANKED_ITEM))) {
            event.setCancelled(true); // The item we use for this is throwable, so we cancel it.

            Party party = PotPvPLobby.getInstance().getPartyHandler().getParty(event.getPlayer().getUniqueId());
            PotPvPLobby.getInstance().getQueueHandler().removeFromQueues(party);
            PotPvPPlugin.getInstance().getInventoryResetHandler().resetInventory(event.getPlayer());
        }
    }

}