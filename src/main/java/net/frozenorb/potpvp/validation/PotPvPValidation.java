package net.frozenorb.potpvp.validation;

import net.frozenorb.potpvp.PotPvPSI;
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

    public static boolean can1v1(Player player) {
        if (isInParty(player)) {
            player.sendMessage(CANNOT_DO_THIS_IN_PARTY);
            return false;
        }

        if (isInQueue(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        return true;
    }

    public static boolean canRematch(Player player) {
        if (isInParty(player)) {
            player.sendMessage(CANNOT_DO_THIS_IN_PARTY);
            return false;
        }

        if (isInQueue(player)) {
            player.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
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

        return true;
    }

    public static boolean canJoinQueue(Player message, Party party) {
        if (isInQueue(party)) {
            message.sendMessage(CANNOT_DO_THIS_WHILE_QUEUED);
            return false;
        }

        return true;
    }

    public static boolean canStartTeamSplit(Party party) {
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

}