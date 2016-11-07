package net.frozenorb.potpvp.duel;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.duel.listener.DuelListener;
import net.frozenorb.potpvp.party.Party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return streamPartyInvites(sender, null).collect(Collectors.toSet());
    }

    public Set<PartyDuelInvite> findInvitesTo(Party target) {
        return streamPartyInvites(null, target).collect(Collectors.toSet());
    }

    public PartyDuelInvite findInvite(Party sender, Party target) {
        return streamPartyInvites(sender, target).findFirst().orElse(null);
    }

    public Set<PlayerDuelInvite> findInvitesFrom(Player sender) {
        return streamPlayerInvites(sender, null).collect(Collectors.toSet());
    }

    public Set<PlayerDuelInvite> findInvitesTo(Player target) {
        return streamPlayerInvites(null, target).collect(Collectors.toSet());
    }

    public PlayerDuelInvite findInvite(Player sender, Player target) {
        return streamPlayerInvites(sender, target).findFirst().orElse(null);
    }

    private Stream<PartyDuelInvite> streamPartyInvites(Party sender, Party target) {
        Stream<PartyDuelInvite> stream = activeInvites.stream()
            .filter(i -> i instanceof PartyDuelInvite)
            .map(i -> (PartyDuelInvite) i);

        if (sender != null) {
            stream.filter(i -> i.getSender() == sender);
        }

        if (target != null) {
            stream.filter(i -> i.getTarget() == target);
        }

        return stream;
    }

    private Stream<PlayerDuelInvite> streamPlayerInvites(Player sender, Player target) {
        Stream<PlayerDuelInvite> stream = activeInvites.stream()
                .filter(i -> i instanceof PlayerDuelInvite)
                .map(i -> (PlayerDuelInvite) i);

        if (sender != null) {
            stream.filter(i -> i.getSender().equals(sender.getUniqueId()));
        }

        if (target != null) {
            stream.filter(i -> i.getTarget().equals(target.getUniqueId()));
        }

        return stream;
    }

}
