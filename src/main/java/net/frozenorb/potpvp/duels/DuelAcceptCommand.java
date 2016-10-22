package net.frozenorb.potpvp.duels;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Mazen Kotb
 */
public class DuelAcceptCommand {
    @Command(names = {"accept"}, permission = "")
    public static void accept(Player sender, @Param(name = "player") UUID player) {
        if (sender.getUniqueId().equals(player)) {
            sender.sendMessage(DuelLang.CANT_ACCEPT_DUEL_FROM_YOURSELF.toString());
            return;
        }

        DuelInvite invite = DuelHandler.instance().inviteBy(player);

        if (invite == null || !invite.sentTo().equals(sender.getUniqueId())) {
            sender.sendMessage(DuelLang.NO_INVITE_HAS_BEEN_SENT.toString());
            return;
        }

        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(sender);

        if (party != null && !party.isLeader(sender.getUniqueId())) {
            party.leave(sender);
        }

        DuelHandler.instance().purgeInvite(player);
        // TODO start a match with the members
    }
}
