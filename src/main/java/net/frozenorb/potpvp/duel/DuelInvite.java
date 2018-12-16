package net.frozenorb.potpvp.duel;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import com.google.common.base.Preconditions;

import lombok.Getter;
import net.frozenorb.potpvp.kittype.KitType;

public abstract class DuelInvite<T> {

    @Getter private final T sender;
    @Getter private final T target;
    @Getter private final KitType kitType;
    @Getter private final Instant timeSent;
    @Getter private final Set<String> maps;

    public DuelInvite(T sender, T target, KitType kitType, Set<String> maps) {
        this.sender = Preconditions.checkNotNull(sender, "sender");
        this.target = Preconditions.checkNotNull(target, "target");
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.timeSent = Instant.now();
        this.maps = maps;
    }

    public boolean isExpired() {
        long sentAgo = ChronoUnit.SECONDS.between(timeSent, Instant.now());
        return sentAgo > DuelHandler.DUEL_INVITE_TIMEOUT_SECONDS;
    }

}