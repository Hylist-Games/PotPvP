package net.frozenorb.potpvp.queue;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.party.Party;
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

public final class QueueHandler {

    private static final String JOIN_SOLO_MESSAGE = ChatColor.GREEN + "You are now queued for %s" + ChatColor.GREEN + ".";
    private static final String LEAVE_SOLO_MESSAGE = ChatColor.GREEN + "You are no longer queued for %s" + ChatColor.GREEN + ".";
    private static final String JOIN_PARTY_MESSAGE = ChatColor.GREEN + "Your party is now queued for %s" + ChatColor.GREEN + ".";
    private static final String LEAVE_PARTY_MESSAGE = ChatColor.GREEN + "Your party is no longer queued for %s" + ChatColor.GREEN + ".";

    // we never call .put outside of the constructor so no concurrency is needed
    private final Map<KitType, SoloQueue> soloQueues = new HashMap<>();
    private final Map<KitType, PartyQueue> partyQueues = new HashMap<>();

    public QueueHandler() {
        Bukkit.getPluginManager().registerEvents(new QueueItemListener(this), PotPvPSI.getInstance());

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

    public void joinQueue(Player player, KitType kitType) {
        // will never be null, queues are created in constructor
        // and KitTypes are static
        SoloQueue queue = soloQueues.get(kitType);

        // will message players about validation errors
        if (PotPvPValidation.canJoinQueue(player)) {
            queue.addToQueue(player.getUniqueId());

            player.sendMessage(String.format(JOIN_SOLO_MESSAGE, kitType.getDisplayName()));
            InventoryUtils.resetInventory(player);
        }
    }

    public void leaveQueue(Player player) {
        SoloQueueEntry queueEntry = getQueueEntry(player.getUniqueId());

        // fail silently
        if (queueEntry != null) {
            SoloQueue queue = queueEntry.getQueue();

            queue.removeFromQueue(player.getUniqueId());

            player.sendMessage(String.format(LEAVE_SOLO_MESSAGE, queue.getKitType().getDisplayName()));
            InventoryUtils.resetInventory(player);
        }
    }

    public void joinQueue(Party party, KitType kitType) {
        // will never be null, queues are created in constructor
        // and KitTypes are static
        PartyQueue queue = partyQueues.get(kitType);

        // will message players about validation errors
        if (PotPvPValidation.canJoinQueue(party)) {
            queue.addToQueue(party);

            party.message(String.format(JOIN_PARTY_MESSAGE, kitType.getDisplayName()));
            party.resetInventories();
        }
    }

    public void leaveQueue(Party party) {
        PartyQueueEntry queueEntry = getQueueEntry(party);

        // fail silently
        if (queueEntry != null) {
            PartyQueue queue = queueEntry.getQueue();

            queue.removeFromQueue(party);

            party.message(String.format(LEAVE_PARTY_MESSAGE, queue.getKitType().getDisplayName()));
            party.resetInventories();
        }
    }

    public boolean isQueued(UUID player) {
        return getQueueEntry(player) != null;
    }

    public SoloQueueEntry getQueueEntry(UUID player) {
        for (SoloQueue queue : soloQueues.values()) {
            SoloQueueEntry queueEntry = queue.getQueueEntry(player);

            if (queueEntry != null) {
                return queueEntry;
            }
        }

        return null;
    }

    public boolean isQueued(Party party) {
        return getQueueEntry(party) != null;
    }

    public PartyQueueEntry getQueueEntry(Party party) {
        for (PartyQueue queue : partyQueues.values()) {
            PartyQueueEntry queueEntry = queue.getQueueEntry(party);

            if (queueEntry != null) {
                return queueEntry;
            }
        }

        return null;
    }

}