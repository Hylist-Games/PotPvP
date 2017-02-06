package net.frozenorb.potpvp.validation;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PotPvPValidation {

    private static final String CANNOT_DUEL_SELF = ChatColor.RED + "You can't duel yourself!";
    private static final String CANNOT_DUEL_OWN_PARTY = ChatColor.RED + "You can't duel your own party!";
    private static final String CAN_ONLY_DUEL_OTHER_PARTIES = ChatColor.RED + "Your party can only duel other parties!";
    private static final String CANNOT_DO_THIS_IN_PARTY = ChatColor.RED + "You can't do this while in a party!";
    private static final String CANNOT_DO_THIS_WHILE_QUEUED = ChatColor.RED + "You can't do this while queued!";
    private static final String CANNOT_DO_THIS_WHILE_IN_MATCH = ChatColor.RED + "You can't do this while participating in or spectating a match!";

    private static final String TARGET_PLAYER_IN_MATCH = ChatColor.RED + "That player is participating in or spectating a match!";
    private static final String TARGET_PLAYER_HAS_DUELS_DISABLED = ChatColor.RED + "The player has duels disabled!";

    private static final String TARGET_PARTY_IN_MATCH = ChatColor.RED + "That party is currently in a match!";
    private static final String TARGET_PARTY_HAS_DUELS_DISABLED = ChatColor.RED + "The party has duels disabled!";

    public static boolean canSendDuel(Player sender, Player target) {
        if (sender == target) {
            sender.sendMessage(CANNOT_DUEL_SELF);
            return false;
        }

        if (isInParty(sender)) {
            sender.sendMessage(CAN_ONLY_DUEL_OTHER_PARTIES);
            return false;
        }

        if (isInOrSpectatingMatch(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            return false;
        }

        if (isInOrSpectatingMatch(target)) {
            sender.sendMessage(TARGET_PLAYER_IN_MATCH);
            return false;
        }

        if (!getSetting(target, Setting.RECEIVE_DUELS)) {
            sender.sendMessage(TARGET_PLAYER_HAS_DUELS_DISABLED);
            return false;
        }

        return true;
    }

    public static boolean canAcceptDuel(Player target, Player sender) {
        if (isInOrSpectatingMatch(sender)) {
            sender.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            return false;
        }

        if (isInOrSpectatingMatch(target)) {
            sender.sendMessage(TARGET_PLAYER_IN_MATCH);
            return false;
        }

        return true;
    }

    public static boolean canSendDuel(Party sender, Party target, Player initiator) {
        if (sender == target) {
            initiator.sendMessage(CANNOT_DUEL_OWN_PARTY);
            return false;
        }

        if (isInOrSpectatingMatch(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            return false;
        }

        if (isInOrSpectatingMatch(Bukkit.getPlayer(target.getLeader()))) {
            initiator.sendMessage(TARGET_PARTY_IN_MATCH);
            return false;
        }

        if (!getSetting(target.getLeader(), Setting.RECEIVE_DUELS)) {
            initiator.sendMessage(TARGET_PARTY_HAS_DUELS_DISABLED);
            return false;
        }

        return true;
    }

    public static boolean canAcceptDuel(Party target, Party sender, Player initiator) {
        if (isInOrSpectatingMatch(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            return false;
        }

        if (isInOrSpectatingMatch(Bukkit.getPlayer(target.getLeader()))) {
            initiator.sendMessage(TARGET_PARTY_IN_MATCH);
            return false;
        }

        return true;
    }

    public static boolean canJoinParty(Player player, Party party) {
        if (isInParty(player)) {
            player.sendMessage(CANNOT_DO_THIS_IN_PARTY);
            return false;
        }

        if (isInOrSpectatingMatch(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            return false;
        }

        return true;
    }

    public static boolean canSpectate(Player player) {
        return canSpectate(player, false);
    }

    public static boolean canSpectate(Player player, boolean silent) {
        if (isInOrSpectatingMatch(player)) {
            if (!silent) {
                player.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            }

            return false;
        }

        return canSpectateIgnoreMatchSpectating(player, silent);
    }

    public static boolean canSpectateIgnoreMatchSpectating(Player player) {
        return canSpectateIgnoreMatchSpectating(player, false);
    }

    public static boolean canSpectateIgnoreMatchSpectating(Player player, boolean silent) {
        if (isInParty(player)) {
            if (!silent) {
                player.sendMessage(CANNOT_DO_THIS_IN_PARTY);
            }

            return false;
        }

        if (isInQueue(player)) {
            if (!silent) {
                player.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            }

            return false;
        }

        if (isInMatch(player)) {
            if (!silent) {
                player.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            }

            return false;
        }

        return true;
    }

    public static boolean canJoinQueue(Player player) {
        if (isInParty(player)) {
            player.sendMessage(CANNOT_DO_THIS_IN_PARTY);
            return false;
        }

        if (isInQueue(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (isInOrSpectatingMatch(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            return false;
        }

        return true;
    }

    public static boolean canJoinQueue(Party party) {
        if (isInQueue(party)) {
            // TODO: Is it best to message the whole party here?
            party.message(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        return true;
    }

    public static boolean canStartTeamSplit(Party party, Player initiator) {
        if (isInQueue(party)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (isInOrSpectatingMatch(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            return false;
        }

        return true;
    }

    public static boolean canStartFfa(Party party, Player initiator) {
        if (isInQueue(party)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        if (isInOrSpectatingMatch(initiator)) {
            initiator.sendMessage(CANNOT_DO_THIS_WHILE_IN_MATCH);
            return false;
        }

        return true;
    }

    private static boolean getSetting(Player player, Setting setting) {
        return getSetting(player.getUniqueId(), setting);
    }

    private static boolean getSetting(UUID playerUuid, Setting setting) {
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        return settingHandler.getSetting(playerUuid, setting);
    }

    private static boolean isInParty(Player player) {
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        return partyHandler.hasParty(player);
    }

    private static boolean isInQueue(Player player) {
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        return queueHandler.isQueued(player.getUniqueId());
    }

    private static boolean isInQueue(Party party) {
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        return queueHandler.isQueued(party);
    }

    private boolean isInMatch(Player player) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        return matchHandler.isPlayingMatch(player);
    }

    private boolean isInOrSpectatingMatch(Player player) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        return matchHandler.isPlayingOrSpectatingMatch(player);
    }

}