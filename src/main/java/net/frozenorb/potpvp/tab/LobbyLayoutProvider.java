package net.frozenorb.potpvp.tab;

import com.google.common.collect.Sets;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.qlib.tab.TabLayout;
import net.frozenorb.qlib.util.PlayerUtils;
import net.frozenorb.qlib.util.UUIDUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

final class LobbyLayoutProvider implements BiConsumer<Player, TabLayout> {

    @Override
    public void accept(Player player, TabLayout tabLayout) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

        rankings: {
            tabLayout.set(0, 4, ChatColor.GOLD + "HCTeams - 2000");
            tabLayout.set(0, 5, ChatColor.GOLD + "Classic - 2000");

            tabLayout.set(1, 3, ChatColor.GOLD.toString() + ChatColor.BOLD + "Your Rankings");
            tabLayout.set(1, 4, ChatColor.GOLD + "No Ench - 2000");
            tabLayout.set(1, 5, ChatColor.GOLD + "Vanilla - 2000");

            tabLayout.set(2, 4, ChatColor.GOLD + "Gapple - 2000");
            tabLayout.set(2, 5, ChatColor.GOLD + "Archer - 2000");
        }

        party: {
            if (party == null) {
                return;
            }

            int x = 0;
            int y = 8;

            for (UUID member : getOrderedMembers(player, party)) {
                int ping = PotPvPLayoutProvider.getPingOrDefault(member);
                String suffix = member == party.getLeader() ? ChatColor.GRAY + "*" : "";
                String displayName = ChatColor.BLUE + UUIDUtils.name(member) + suffix;

                tabLayout.set(x++, y, displayName, ping);

                if (x == 3 && y == PotPvPLayoutProvider.MAX_TAB_Y) {
                    break;
                }

                if (x == 3) {
                    x = 0;
                    y++;
                }
            }
        }
    }

    // player first, leader next, then all other members
    private Set<UUID> getOrderedMembers(Player viewer, Party party) {
        Set<UUID> orderedMembers = Sets.newSetFromMap(new LinkedHashMap<>());
        UUID leader = party.getLeader();

        orderedMembers.add(viewer.getUniqueId());

        // if they're the leader we don't display them twice
        if (viewer.getUniqueId() != leader) {
            orderedMembers.add(leader);
        }

        for (UUID member : party.getMembers()) {
            // don't display the leader or the viewer again
            if (member == leader || member == viewer.getUniqueId()) {
                continue;
            }

            orderedMembers.add(member);
        }

        return orderedMembers;
    }

}