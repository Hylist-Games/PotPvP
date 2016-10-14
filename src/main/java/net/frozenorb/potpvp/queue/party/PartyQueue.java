package net.frozenorb.potpvp.queue.party;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.queue.Queue;

import java.util.Set;
import java.util.UUID;

public final class PartyQueue extends Queue<PartyQueueEntry> {

    public PartyQueue(KitType kitType) {
        super(kitType);
    }

    public boolean isInQueue(Party party) {
        return getQueueEntry(party) != null;
    }

    public void addToQueue(Party party) {
        queueEntries.add(new PartyQueueEntry(this, party));
    }

    public void removeFromQueue(Party party) {
        queueEntries.remove(getQueueEntry(party));
    }

    @Override
    public int countPlayersQueued() {
        int result = 0;

        for (PartyQueueEntry queueEntry : queueEntries) {
            Set<UUID> members = queueEntry.getParty().getMembers();
            result += members.size();
        }

        return result;
    }

    public PartyQueueEntry getQueueEntry(Party party) {
        for (PartyQueueEntry queueEntry : queueEntries) {
            if (queueEntry.getParty().equals(party)) {
                return queueEntry;
            }
        }

        return null;
    }

}