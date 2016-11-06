package net.frozenorb.potpvp.duels;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.duels.listener.DuelListener;
import net.frozenorb.potpvp.setting.Setting;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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

    public DuelInvite purgeInvitesFrom(Player player) {
        return invites.remove(player.getUniqueId());
    }

    public void purgeInvitesTo(Player player) {
        UUID playerUuid = player.getUniqueId();
        invites.values().removeIf(v -> v.sentTo() == playerUuid);
    }

    public DuelInvite inviteBy(Player sender) {
        DuelInvite invite = invites.get(sender.getUniqueId());

        if (invite != null && invite.isExpired()) {
            invites.remove(sender.getUniqueId());
            return null;
        }

        return invite;
    }

    public List<DuelInvite> invitesTo(Player target) {
        return invites.values().stream()
                .filter((invite) -> invite.sentTo().equals(target.getUniqueId()))
                .collect(Collectors.toList());
    }

    public DuelInvite inviteTo(Player target, Player sender) {
        for (DuelInvite invite : invites.values()) {
            if (
                invite.sender().equals(sender.getUniqueId()) &&
                invite.sentTo().equals(target.getUniqueId())
            ) {
                return invite;
            }
        }

        return null;
    }

}
