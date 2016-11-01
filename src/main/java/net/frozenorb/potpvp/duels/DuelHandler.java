package net.frozenorb.potpvp.duels;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.duels.listeners.DuelListener;
import net.frozenorb.potpvp.setting.Setting;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Mazen Kotb
 */
public final class DuelHandler {
    private static final DuelHandler INSTANCE = new DuelHandler();
    private Map<UUID, DuelInvite> invites = new HashMap<>();

    private DuelHandler() {
        Bukkit.getPluginManager().registerEvents(new DuelListener(), PotPvPSI.getInstance());
    }

    public static DuelHandler instance() {
        return INSTANCE;
    }

    public void insertInvite(DuelInvite invite) {
        invites.put(invite.sender(), invite);
    }

    public DuelInvite purgeInvite(Player player) {
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

    public boolean canInvite(Player player) {
        return PotPvPSI.getInstance().getSettingHandler().getSetting(player.getUniqueId(), Setting.RECEIVE_DUELS) &&
                !PotPvPSI.getInstance().getMatchHandler().isPlayingOrSpectatingMatch(player);
    }
}
