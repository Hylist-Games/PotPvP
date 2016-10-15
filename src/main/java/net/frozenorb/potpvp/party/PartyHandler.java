package net.frozenorb.potpvp.party;

import com.google.common.collect.ImmutableSet;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles accessing and storage of {@link Party} data
 */
public final class PartyHandler {

    /**
     * Number of seconds it takes for a {@link PartyInvite}
     * to expire after being sent.
     * @see PartyInvite#isActive()
     */
    static final int INVITE_EXPIRATION_SECONDS = 30;

    // TODO: O(1) player -> party lookups
    //private final Map<UUID, Party> playerParties = new ConcurrentHashMap<>();
    private final Set<Party> parties = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // TODO: icon click listener
    /*
                    Party party = PotPvPLobby.getInstance().getPartyHandler().getLocalParty(event.getPlayer());

                if (party != null) {
                    if (event.getItem().isSimilar(PotPvPLobby.getInstance().getLobbyHandler().generatePartyItem(party))) {
                        PartyInfoCommand.partyInfo(event.getPlayer(), event.getPlayer().getUniqueId()); // The same as making them type /party info.
                    }
                }
     */

    /**
     * Finds all parties with at least one member
     * @return immutable set of all existing parties
     */
    private Set<Party> getParties() {
        return ImmutableSet.copyOf(parties);
    }

    /**
     * Checks if the player provided is in a party ({@link Party#isMember(UUID) would return true}
     * @param player the player to check
     * @return if the player provided is in a party
     */
    public boolean hasParty(Player player) {
        return getParty(player.getUniqueId()) != null;
    }

    /**
     * Checks if the player provided is in a party ({@link Party#isMember(UUID) would return true}
     * @param playerUuid the player to check
     * @return if the player provided is in a party
     */
    public boolean hasParty(UUID playerUuid) {
        return getParty(playerUuid) != null;
    }

    /**
     * Looks up a player's party (a player's party is considered a party
     * for which {@link Party#isMember(UUID)} returns true)
     * @param playerUuid the player to lookup
     * @return the player's party, or null if the player is not in a party.
     */
    public Party getParty(Player player) {
        return getParty(player.getUniqueId());
    }

    /**
     * Looks up a player's party (a player's party is considered a party
     * for which {@link Party#isMember(UUID)} returns true)
     * @param playerUuid the player to lookup
     * @return the player's party, or null if the player is not in a party.
     */
    public Party getParty(UUID playerUuid) {
        for (Party party : parties) {
            if (party.isMember(playerUuid)) {
                return party;
            }
        }

        return null;
    }

}