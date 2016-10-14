package net.frozenorb.potpvp.queue.party;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.queue.QueueEntry;

import java.util.Set;
import java.util.UUID;

import lombok.Getter;

/**
 * Represents a {@link net.frozenorb.potpvp.party.Party} waiting
 * in a {@link PartyQueue}
 */
public final class PartyQueueEntry extends QueueEntry<PartyQueue> {

    @Getter private final Party party;

    PartyQueueEntry(PartyQueue queue, Party party) {
        super(queue);

        this.party = Preconditions.checkNotNull(party, "party");
    }

    @Override
    public Set<UUID> getMembers() {
        return party.getMembers();
    }

}