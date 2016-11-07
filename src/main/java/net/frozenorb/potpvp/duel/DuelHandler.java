package net.frozenorb.potpvp.duel;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.duel.listener.DuelListener;
import net.frozenorb.potpvp.party.Party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class DuelHandler {

    public static final int DUEL_INVITE_TIMEOUT_SECONDS = 30;

    // this does mean lookups are O(n), but unlike matches or parties
    // there are isn't enough volume + frequency to become an issue
    private Set<DuelInvite> activeInvites = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public DuelHandler() {
        Bukkit.getPluginManager().registerEvents(new DuelListener(), PotPvPSI.getInstance());
    }

    public void insertInvite(DuelInvite invite) {
        activeInvites.add(invite);
    }

    public void removeInvite(DuelInvite invite) {
        activeInvites.remove(invite);
    }

    // TODO: EXPIRE

    public void removeInvitesTo(Player player) {
        findInvitesTo(player).forEach(activeInvites::remove);
    }

    public void removeInvitesFrom(Player player) {
        findInvitesFrom(player).forEach(activeInvites::remove);
    }

    public void removeInvitesTo(Party party) {
        findInvitesTo(party).forEach(activeInvites::remove);
    }

    public void removeInvitesFrom(Party party) {
        findInvitesFrom(party).forEach(activeInvites::remove);
    }

    public Set<PartyDuelInvite> findInvitesFrom(Party sender) {
        return getPartyInvites().stream()
            .filter(i -> i.getSender() == sender)
            .collect(Collectors.toSet());
    }

    public Set<PartyDuelInvite> findInvitesTo(Party target) {
        return getPartyInvites().stream()
            .filter(i -> i.getTarget() == target)
            .collect(Collectors.toSet());
    }

    public PartyDuelInvite findInvite(Party sender, Party target) {
        return getPartyInvites().stream()
            .filter(i -> i.getSender() == sender)
            .filter(i -> i.getTarget() == target)
            .findFirst().orElse(null);
    }

    public Set<PlayerDuelInvite> findInvitesFrom(Player sender) {
        return getPlayerInvites().stream()
            .filter(i -> i.getSender().equals(sender.getUniqueId()))
            .collect(Collectors.toSet());
    }

    public Set<PlayerDuelInvite> findInvitesTo(Player target) {
        return getPlayerInvites().stream()
            .filter(i -> i.getTarget().equals(target.getUniqueId()))
            .collect(Collectors.toSet());
    }

    public PlayerDuelInvite findInvite(Player sender, Player target) {
        return getPlayerInvites().stream()
            .filter(i -> i.getSender().equals(sender.getUniqueId()))
            .filter(i -> i.getTarget().equals(target.getUniqueId()))
            .findFirst().orElse(null);
    }

    private List<PlayerDuelInvite> getPlayerInvites() {
        List<PlayerDuelInvite> playerInvites = new ArrayList<>();

        for (DuelInvite invite : activeInvites) {
            if (invite instanceof PlayerDuelInvite) {
                playerInvites.add((PlayerDuelInvite) invite);
            }
        }

        return playerInvites;
    }

    private List<PartyDuelInvite> getPartyInvites() {
        List<PartyDuelInvite> partyInvites = new ArrayList<>();

        for (DuelInvite invite : activeInvites) {
            if (invite instanceof PartyDuelInvite) {
                partyInvites.add((PartyDuelInvite) invite);
            }
        }

        return partyInvites;
    }

}
