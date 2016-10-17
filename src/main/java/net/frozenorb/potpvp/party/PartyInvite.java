package net.frozenorb.potpvp.party;

import com.google.common.base.Preconditions;

import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
     * The party the target will be joining upon accepting
     * this invitation.
     */
    @Getter private Party party;

    /**
     * Player that will be joining the party,
     * if they accept this invitation.
     */
    @Getter private UUID target;

    /**
     * The time this invite was sent, used to determine if this
     * invitation is still active.
     * Sent and created are synonymous in this context
     */
    @Getter private Instant timeSent;

    PartyInvite(Party party, UUID target) {
        this.party = Preconditions.checkNotNull(party, "party");
        this.target = Preconditions.checkNotNull(target, "target");
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