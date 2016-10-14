package net.frozenorb.potpvp.party;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.InventoryUtils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import lombok.Getter;

/**
 * Represents a collection of players which can perform
 * various actions (ex queue, have elo, etc) together.
 *
 * All members, the leader, and all {@link PartyInvite}
 * targets (although not senders) are guaranteed to be online.
 */
public final class Party {

    /**
     * Leader of the party, given permission to perform
     * administrative commands (and perform actions like queueing)
     * on behalf of the party. Guaranteed to be online.
     */
    @Getter private UUID leader;

    /**
     * All players who are currently part of this party.
     * Each player will only be a member of one party at a time.
     * Guaranteed to all be online.
     */
    private final Set<UUID> members = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * All active (non-expired) {@link PartyInvite}s. Players can have
     * active invitations from more than one party at a time. All targets
     * (but not senders) are guaranteed to be online.
     */
    // TODO: Validate invitations are active (PartyInvite#isActive()) before returning them to clients.
    private final Set<PartyInvite> invites = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Current access restriction in place for joining this party
     * @see PartyAccessRestriction
     */
    @Getter private PartyAccessRestriction accessRestriction = PartyAccessRestriction.INVITE_ONLY;

    /**
     * Password requires to join this party, only active if
     * {@link #accessRestriction} is {@link PartyAccessRestriction#PASSWORD}.
     * @see PartyAccessRestriction#PASSWORD
     */
    @Getter private String password = null;

    Party(UUID leader) {
        this.leader = Preconditions.checkNotNull(leader, "leader");
    }

    /**
     * Checks if the player provided is a member of this party
     * @param playerUuid the player to check
     * @return true if the player provided is a member of this party,
     *          false otherwise.
     */
    public boolean isMember(UUID playerUuid) {
        return members.contains(playerUuid);
    }

    /**
     * Checks if the player provided is the leader of this party
     * @param playerUuid the player to check
     * @return true if the player provided is the leader of this party,
     *          false otherwise.
     */
    public boolean isLeader(UUID playerUuid) {
        return leader.equals(playerUuid);
    }

    /**
     * Gets an immutable set of all players currently
     * in this party.
     * @see Party#members
     * @return immutable set of all members
     */
    public Set<UUID> getMembers() {
        return ImmutableSet.copyOf(members);
    }

    /**
     * Gets an immutable set of all active {@link PartyInvite}s
     * @see PartyInvite#getSender())
     * @return immutable set of all active invites
     */
    public Set<PartyInvite> getInvites() {
        return ImmutableSet.copyOf(invites);
    }

    /**
     * Finds an active {@link PartyInvite} whose target is equal to the
     * player provided
     * @param target player who must match the result's {@link PartyInvite#getTarget()}
     * @return a PartyInvite targeting the player provided, if one exists
     */
    public PartyInvite getInvite(UUID target) {
        for (PartyInvite invite : invites) {
            if (invite.getTarget().equals(target)) {
                return invite;
            }
        }

        return null;
    }

    /**
     * Sends a basic chat message to all members
     * @param message the message to send
     */
    public void message(String message) {
        forEachOnline(p -> p.sendMessage(message));
    }

    /**
     * Plays a sound for all members
     * @param sound the Sound to play
     * @param pitch the pitch to play the provided sound at
     */
    public void playSound(Sound sound, float pitch) {
        forEachOnline(p -> p.playSound(p.getLocation(), sound, 10F, pitch));
    }

    /**
     * Resets all members' inventories
     * @see InventoryUtils#resetInventory(Player)
     */
    public void resetInventories() {
        forEachOnline(InventoryUtils::resetInventory);
    }

    /**
     * Resets all members' inventories after a delay
     * @see InventoryUtils#resetInventoryLater(Player, int)
     * @param delay the number of ticks to delay
     */
    public void resetInventoriesLater(int delay) {
        // we use one runnable and then call resetInventories instead of
        // directly using to InventoryUtils#resetInventoryLater to reduce
        // the number of tasks we submit to the scheduler
        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), this::resetInventories, delay);
    }

    private void forEachOnline(Consumer<Player> consumer) {
        for (UUID member : members) {
            Player memberBukkit = Bukkit.getPlayer(member);

            if (memberBukkit != null) {
                consumer.accept(memberBukkit);
            }
        }
    }

}