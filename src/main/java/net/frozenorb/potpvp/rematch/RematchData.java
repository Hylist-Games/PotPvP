package net.frozenorb.potpvp.rematch;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.kittype.KitType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import lombok.Getter;
import lombok.ToString;

@ToString
public final class RematchData {

    @Getter private final UUID sender;
    @Getter private final UUID target;
    @Getter private final KitType kitType;
    @Getter private final Instant expiresAt;

    RematchData(UUID sender, UUID target, KitType kitType, int durationSeconds) {
        this.sender = Preconditions.checkNotNull(sender, "sender");
        this.target = Preconditions.checkNotNull(target, "target");
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.expiresAt = Instant.now().plusSeconds(durationSeconds);
    }

    public boolean isExpired() {
        return expiresAt.isAfter(Instant.now());
    }

    public int getSecondsUntilExpiration() {
        return (int) ChronoUnit.SECONDS.between(Instant.now(), expiresAt);
    }

}