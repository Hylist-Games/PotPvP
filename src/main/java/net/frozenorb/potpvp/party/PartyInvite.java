package net.frozenorb.potpvp.party;

import com.google.common.base.Preconditions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import lombok.Getter;

/**
 * Represents an invitation for a player (the target)
 * to join a {@link Party}
 */
public final class PartyInvite {

    /**
     * Player that will be joining the party,
     * if they accept this invitation.
     */
    @Getter private UUID target;

    /**
     * Player who sent this invite (via /party invite)
     *
     * Not guaranteed to be in the same party the target will be
     * joining, as it's possible the sender left before the target
     * accepts the invitation.
     *
     * The party the target will be joining must be known by
     * context (ex the {@link Party} this PartyInvite was obtained
     * from)
     * @see Party#getInvites()
     */
    @Getter private UUID sender;

    /**
     * The time this invite was sent, used to determine if this
     * invitation is still active.
     * Sent and created are synonymous in this context
     */
    @Getter private Instant timeSent;

    PartyInvite(UUID target, UUID sender) {
        this.target = Preconditions.checkNotNull(target, "target");
        this.sender = Preconditions.checkNotNull(sender, "sender");
        this.timeSent = Instant.now();
    }

    /**
     * Checks if this invitation is still active, where active means
     * that the invitation has not expired (time between {@link #timeSent}
     * and now are less than {@link PartyHandler#INVITE_EXPIRATION_SECONDS})
     * @return if this invitation is still active
     */
    public boolean isActive() {
        long sentAgoSeconds = ChronoUnit.SECONDS.between(timeSent, Instant.now());
        return sentAgoSeconds < PartyHandler.INVITE_EXPIRATION_SECONDS;
    }

}