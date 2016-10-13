package net.frozenorb.potpvp.queue;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.queue.party.PartyQueue;
import net.frozenorb.potpvp.queue.party.PartyQueueEntry;
import net.frozenorb.potpvp.queue.solo.SoloQueue;
import net.frozenorb.potpvp.queue.solo.SoloQueueEntry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class QueueHandler {

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

    public void joinQueue(Player player, KitType kitType) {
        SoloQueue queue = soloQueues.get(kitType);

        queue.addToQueue(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You are now queued for " + kitType.getDisplayName() + ChatColor.GREEN + ".");
    }

    public void joinQueue(Party party, KitType kitType) {
        PartyQueue queue = partyQueues.get(kitType);

        queue.addToQueue(party);
        party.message(ChatColor.GREEN + "Your party is now queued for " + kitType.getDisplayName() + ChatColor.GREEN + ".");
    }

    public void leaveQueue(Player player) {
        SoloQueueEntry queueEntry = getQueueEntry(player.getUniqueId());

        if (queueEntry != null) {
            SoloQueue queue = queueEntry.getQueue();

            queue.removeFromQueue(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You are no longer queued.");
        }
    }

    public void leaveQueue(Party party) {
        PartyQueueEntry queueEntry = getQueueEntry(party);

        if (queueEntry != null) {
            PartyQueue queue = queueEntry.getQueue();

            queue.removeFromQueue(party);
            party.message(ChatColor.GREEN + "Your party is no longer queued.");
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