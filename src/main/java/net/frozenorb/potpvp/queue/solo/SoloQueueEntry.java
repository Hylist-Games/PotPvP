package net.frozenorb.potpvp.queue.solo;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import net.frozenorb.potpvp.queue.QueueEntry;

import java.util.Set;
import java.util.UUID;

import lombok.Getter;

/**
 * Represents a single player waiting
 * in a {@link SoloQueue}
 */
public final class SoloQueueEntry extends QueueEntry<SoloQueue> {

    @Getter private final UUID player;

    SoloQueueEntry(SoloQueue queue, UUID player) {
        super(queue);

        this.player = Preconditions.checkNotNull(player, "player");
    }

    @Override
    public Set<UUID> getMembers() {
        return ImmutableSet.of(player);
    }

}