package net.frozenorb.potpvp.duels;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.setting.Setting;

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
    }

    public static DuelHandler instance() {
        return INSTANCE;
    }

    public void insertInvite(DuelInvite invite) {
        invites.put(invite.sender(), invite);
    }

    public DuelInvite purgeInvite(UUID sender) {
        return invites.remove(sender);
    }

    public DuelInvite inviteBy(Player player) {
        DuelInvite invite = invites.get(player.getUniqueId());

        if (invite != null && invite.isExpired()) {
            invites.remove(player.getUniqueId());
            return null;
        }

        return invite;
    }

    public List<DuelInvite> invitesTo(UUID player) {
        return invites.values().stream()
                .filter((invite) -> invite.sentTo().equals(player))
                .collect(Collectors.toList());
    }

    public boolean canInvite(Player player) {
        return PotPvPSI.getInstance().getSettingHandler().getSetting(player.getUniqueId(), Setting.RECEIVE_DUELS) &&
                !PotPvPSI.getInstance().getMatchHandler().isPlayingOrSpectatingMatch(player);
    }
}
