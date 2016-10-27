package net.frozenorb.potpvp.match;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

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
 * Represents one team participating in a {@link Match}
 */
public final class MatchTeam {

    /**
     * Unique identifier for this MatchTeam. An id field is necessary
     * because {@link Match}s can have any number of teams
     * @see Match#winner
     */
    @Getter private final String id;

    /**
     * All players who were ever part of this team, including those who logged off / died
     */
    @Getter private final Set<UUID> allMembers;

    /**
     * All players who are currently alive.
     */
    private final Set<UUID> aliveMembers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public MatchTeam(String id, Set<UUID> initialMembers) {
        this.id = Preconditions.checkNotNull(id, "id");
        this.allMembers = ImmutableSet.copyOf(initialMembers);
        this.aliveMembers.addAll(initialMembers);
    }

    /**
     * Marks the given player as dead (will no longer appear in {@link MatchTeam#getAliveMembers()}, etc)
     * @param playerUuid the player to mark as dead
     */
    public void markDead(UUID playerUuid) {
        aliveMembers.remove(playerUuid);
    }

    /**
     * Checks if the given player is still alive (shorthand for .getAliveMembers().contains())
     * @param playerUuid the player to check
     * @return if the given player is still alive
     */
    public boolean isAlive(UUID playerUuid) {
        return aliveMembers.contains(playerUuid);
    }

    /**
     * Gets a immutable set of all alive team members
     * @see MatchTeam#aliveMembers
     * @return immutable set of all alive team members
     */
    public Set<UUID> getAliveMembers() {
        return ImmutableSet.copyOf(aliveMembers);
    }

    /**
     * Sends a basic chat message to all alive members
     * @see MatchTeam#aliveMembers
     * @param message the message to send
     */
    public void messageAlive(String message) {
        forEachAlive(p -> p.sendMessage(message));
    }

    /**
     * Plays a sound for all alive members
     * @param sound the Sound to play
     * @param pitch the pitch to play the provided sound at
     */
    public void playSoundAlive(Sound sound, float pitch) {
        forEachAlive(p -> p.playSound(p.getLocation(), sound, 10F, pitch));
    }

    private void forEachAlive(Consumer<Player> consumer) {
        for (UUID member : aliveMembers) {
            Player memberBukkit = Bukkit.getPlayer(member);

            if (memberBukkit != null) {
                consumer.accept(memberBukkit);
            }
        }
    }

}