package net.frozenorb.potpvp.queue.solo;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.queue.Queue;

import java.util.UUID;

public final class SoloQueue extends Queue<SoloQueueEntry> {

    public SoloQueue(KitType kitType) {
        super(kitType);
    }

    public boolean isInQueue(UUID player) {
        return getQueueEntry(player) != null;
    }

    public void addToQueue(UUID player) {
        queueEntries.add(new SoloQueueEntry(this, player));
    }

    public void removeFromQueue(UUID player) {
        queueEntries.remove(getQueueEntry(player));
    }

    @Override
    public int countPlayersQueued() {
        // easy, one queue entry = one player
        return queueEntries.size();
    }

    public SoloQueueEntry getQueueEntry(UUID player) {
        for (SoloQueueEntry queueEntry : queueEntries) {
            if (queueEntry.getPlayer().equals(player)) {
                return queueEntry;
            }
        }

        return null;
    }

}