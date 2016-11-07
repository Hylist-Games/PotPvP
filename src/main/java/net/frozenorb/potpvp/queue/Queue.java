package net.frozenorb.potpvp.queue;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;

public abstract class Queue<T extends QueueEntry> {

    @Getter protected final KitType kitType;
    protected final List<T> queueEntries = new CopyOnWriteArrayList<>();

    public Queue(KitType kitType) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
    }

    // IntelliJ says protected isn't needed, but that's incorrect.
    // because of the inheritance (SoloQueue and PartyQueue) this needs to be protected
    protected void tick() {
        // we clone queueEntries + always remove from the copy
        // to prevent infinite loops (when matches fail to create),
        // but only remove from the queueEntries on success
        List<T> queueCopy = new ArrayList<>(queueEntries);
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();

        while (queueCopy.size() >= 2) {
            // remove from 0 both times because index shifts down
            T entryA = queueCopy.remove(0);
            T entryB = queueCopy.remove(0);

            if (createMatch(entryA, entryB)) {
                queueEntries.remove(entryA);
                queueHandler.removeFromQueueCache(entryA);

                queueEntries.remove(entryB);
                queueHandler.removeFromQueueCache(entryB);
            }
        }
    }

    public abstract int countPlayersQueued();

    private boolean createMatch(T entryA, T entryB) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        List<MatchTeam> teams = ImmutableList.of(
            new MatchTeam(entryA.getMembers()),
            new MatchTeam(entryB.getMembers())
        );

        return matchHandler.startMatch(teams, kitType, true) != null;
    }

}