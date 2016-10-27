package net.frozenorb.potpvp.queue;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.queue.listener.QueueGeneralListener;
import net.frozenorb.potpvp.queue.listener.QueueItemListener;
import net.frozenorb.potpvp.queue.party.PartyQueue;
import net.frozenorb.potpvp.queue.party.PartyQueueEntry;
import net.frozenorb.potpvp.queue.solo.SoloQueue;
import net.frozenorb.potpvp.queue.solo.SoloQueueEntry;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.validation.PotPvPValidation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class QueueHandler {

    private static final String JOIN_SOLO_MESSAGE = ChatColor.GREEN + "You are now queued for %s" + ChatColor.GREEN + ".";
    private static final String LEAVE_SOLO_MESSAGE = ChatColor.GREEN + "You are no longer queued for %s" + ChatColor.GREEN + ".";
    private static final String JOIN_PARTY_MESSAGE = ChatColor.GREEN + "Your party is now queued for %s" + ChatColor.GREEN + ".";
    private static final String LEAVE_PARTY_MESSAGE = ChatColor.GREEN + "Your party is no longer queued for %s" + ChatColor.GREEN + ".";

    // we never call .put outside of the constructor so no concurrency is needed
    private final Map<KitType, SoloQueue> soloQueues = new HashMap<>();
    private final Map<KitType, PartyQueue> partyQueues = new HashMap<>();

    // maps players (and parties) to the queue they're in for fast O(1) lookup
    private final Map<UUID, SoloQueue> soloQueueCache = new ConcurrentHashMap<>();
    private final Map<Party, PartyQueue> partyQueueCache = new ConcurrentHashMap<>();

    public QueueHandler() {
        Bukkit.getPluginManager().registerEvents(new QueueGeneralListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new QueueItemListener(), PotPvPSI.getInstance());

        for (KitType kitType : KitType.values()) {
            soloQueues.put(kitType, new SoloQueue(kitType));
            partyQueues.put(kitType, new PartyQueue(kitType));
        }

        Bukkit.getScheduler().runTaskTimer(PotPvPSI.getInstance(), () -> {
            soloQueues.values().forEach(SoloQueue::tick);
            partyQueues.values().forEach(PartyQueue::tick);
        }, 20L, 20L);
    }

    public int countPlayersQueued() {
        int result = 0;

        for (SoloQueue queue : soloQueues.values()) {
            result += queue.countPlayersQueued();
        }

        for (PartyQueue queue : partyQueues.values()) {
            result += queue.countPlayersQueued();
        }

        return result;
    }

    public int countPlayersQueued(KitType kitType) {
        return soloQueues.get(kitType).countPlayersQueued() +
               partyQueues.get(kitType).countPlayersQueued();
    }

    public void joinQueue(Player player, KitType kitType, boolean silent) {
        // will never be null, queues are created in constructor
        // and KitTypes are static
        SoloQueue queue = soloQueues.get(kitType);

        // will message players about validation errors
        if (PotPvPValidation.canJoinQueue(player)) {
            queue.addToQueue(player.getUniqueId());
            soloQueueCache.put(player.getUniqueId(), queue);

            if (!silent) {
                player.sendMessage(String.format(JOIN_SOLO_MESSAGE, kitType.getDisplayName()));
                InventoryUtils.resetInventoryDelayed(player);
            }
        }
    }

    public void leaveQueue(Player player, boolean silent) {
        SoloQueueEntry queueEntry = getQueueEntry(player.getUniqueId());

        // fail silently
        if (queueEntry != null) {
            SoloQueue queue = queueEntry.getQueue();

            queue.removeFromQueue(player.getUniqueId());
            soloQueueCache.remove(player.getUniqueId());

            if (!silent) {
                player.sendMessage(String.format(LEAVE_SOLO_MESSAGE, queue.getKitType().getDisplayName()));
                InventoryUtils.resetInventoryDelayed(player);
            }
        }
    }

    public void joinQueue(Party party, KitType kitType, boolean silent) {
        // will never be null, queues are created in constructor
        // and KitTypes are static
        PartyQueue queue = partyQueues.get(kitType);

        // will message players about validation errors
        if (PotPvPValidation.canJoinQueue(party)) {
            queue.addToQueue(party);
            partyQueueCache.put(party, queue);

            if (!silent) {
                party.message(String.format(JOIN_PARTY_MESSAGE, kitType.getDisplayName()));
                party.resetInventoriesDelayed();
            }
        }
    }

    public void leaveQueue(Party party, boolean silent) {
        PartyQueueEntry queueEntry = getQueueEntry(party);

        // fail silently
        if (queueEntry != null) {
            PartyQueue queue = queueEntry.getQueue();

            queue.removeFromQueue(party);
            partyQueueCache.remove(party);

            if (!silent) {
                party.message(String.format(LEAVE_PARTY_MESSAGE, queue.getKitType().getDisplayName()));
                party.resetInventoriesDelayed();
            }
        }
    }

    public boolean isQueued(UUID player) {
        return soloQueueCache.containsKey(player);
    }

    public SoloQueueEntry getQueueEntry(UUID player) {
        SoloQueue queue = soloQueueCache.get(player);
        return queue != null ? queue.getQueueEntry(player) : null;
    }

    public boolean isQueued(Party party) {
        return partyQueueCache.containsKey(party);
    }

    public PartyQueueEntry getQueueEntry(Party party) {
        PartyQueue queue = partyQueueCache.get(party);
        return queue != null ? queue.getQueueEntry(party) : null;
    }

    void removeFromQueueCache(Object entry) {
        if (entry instanceof SoloQueueEntry) {
            soloQueueCache.remove(((SoloQueueEntry) entry).getPlayer());
        } else if (entry instanceof PartyQueueEntry) {
            partyQueueCache.remove(((PartyQueueEntry) entry).getParty());
        }
    }

}