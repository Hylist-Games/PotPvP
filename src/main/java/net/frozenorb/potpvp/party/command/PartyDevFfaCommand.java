package net.frozenorb.potpvp.party.command;

import com.google.common.collect.ImmutableSet;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.qlib.command.Command;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PartyDevFfaCommand {

    @Command(names = {"party devffa", "p devffa", "t devffa", "team devffa"}, permission = "op")
    public static void partyDevFfa(Player sender) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else {
            List<MatchTeam> teams = new ArrayList<>();

            for (UUID member : party.getMembers()) {
                teams.add(new MatchTeam(UUID.randomUUID().toString(), ImmutableSet.of(member)));
            }

            PotPvPSI.getInstance().getMatchHandler().startMatch(teams, KitType.HCTEAMS);
        }
    }

}