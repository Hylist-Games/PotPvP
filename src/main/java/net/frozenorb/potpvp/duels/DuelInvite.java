package net.frozenorb.potpvp.duels;

import net.frozenorb.potpvp.kittype.KitType;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Mazen Kotb
 */
public class DuelInvite {
    public static long EXPIRE_TIME = TimeUnit.SECONDS.toMillis(30);
    private UUID sender;
    private UUID sentTo;
    private boolean party; // is the sender a party?
    private KitType kitType;
    private long timestamp;

    public DuelInvite(UUID sender, UUID sentTo, boolean party, KitType kitType, long timestamp) {
        this.sender = sender;
        this.sentTo = sentTo;
        this.party = party;
        this.kitType = kitType;
        this.timestamp = timestamp;
    }

    public UUID sender() {
        return sender;
    }

    public UUID sentTo() {
        return sentTo;
    }

    public boolean isParty() {
        return party;
    }

    public KitType kitType() {
        return kitType;
    }

    public long timestamp() {
        return timestamp;
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() - timestamp) >= EXPIRE_TIME;
    }

    public boolean matches(UUID sentTo, KitType type) {
        return this.sentTo.equals(sentTo) && this.kitType.equals(type);
    }
}
