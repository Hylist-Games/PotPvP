package net.frozenorb.potpvp.validation;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.queue.QueueHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PotPvPValidation {

    private static final String CANNOT_DO_THIS_IN_PARTY = ChatColor.RED + "You cannot do this while in a party!";
    private static final String CANNOT_DO_THIS_WHILE_QUEUED = ChatColor.RED + "You cannot do this while queued!";
    private static final String CANNOT_DO_THIS_WHILE_IN_MATCH = ChatColor.RED + "You cannot do this while participating in or spectating a match!";

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

    private boolean isInOrSpectatingMatch(Player player) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        return matchHandler.isPlayingOrSpectatingMatch(player);
    }

}