package net.frozenorb.potpvp.queue;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchStartResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;

public abstract class Queue<T extends QueueEntry> {

    @Getter protected final KitType kitType;
    protected final List<T> queueEntries = new CopyOnWriteArrayList<>();

    public Queue(KitType kitType) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
    }

    public void tick() {
        // we clone queueEntries + always remove from the copy
        // to prevent infinite loops (when matches fail to create),
        // but only remove from the queueEntries on success
        List<T> queueCopy = new ArrayList<>(queueEntries);

        while (queueCopy.size() >= 2) {
            T entryA = queueCopy.remove(0);
            T entryB = queueCopy.remove(1);

            if (createMatch(entryA, entryB)) {
                queueEntries.remove(entryA);
                queueEntries.remove(entryB);
            }
        }
    }

    private boolean createMatch(T entryA, T entryB) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Set<Set<UUID>> teams = ImmutableSet.of(
            entryA.getMembers(),
            entryB.getMembers()
        );

        MatchStartResult result = matchHandler.startMatch(teams, kitType);
        return result == MatchStartResult.SUCCESSFUL;
    }

}