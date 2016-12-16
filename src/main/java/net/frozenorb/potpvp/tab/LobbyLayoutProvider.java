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

            Set<UUID> orderedMembers = Sets.newSetFromMap(new LinkedHashMap<>());
            UUID leader = party.getLeader();

            orderedMembers.add(player.getUniqueId());

            // if they're the leader we don't display them twice
            if (player.getUniqueId() != leader) {
                orderedMembers.add(leader);
            }

            for (UUID member : party.getMembers()) {
                // don't display the leader or the watcher again
                if (member == leader || member == player.getUniqueId()) {
                    continue;
                }

                orderedMembers.add(member);
            }

            int x = 0;
            int y = 8;

            for (UUID member : orderedMembers) {
                String displayName = ChatColor.BLUE + UUIDUtils.name(member) + (member == leader ? ChatColor.GRAY + "*" : "");
                tabLayout.set(x++, y, displayName, PotPvPLayoutProvider.getPingOrDefault(member));

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

}