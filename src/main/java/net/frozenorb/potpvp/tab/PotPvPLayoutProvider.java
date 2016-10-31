package net.frozenorb.potpvp.tab;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.qlib.tab.LayoutProvider;
import net.frozenorb.qlib.tab.TabLayout;
import net.frozenorb.qlib.util.UUIDUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class PotPvPLayoutProvider implements LayoutProvider {

    @Override
    public TabLayout provide(Player player) {
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        TabLayout layout = TabLayout.create(player);

        Party party = partyHandler.getParty(player);

        // {}s are only used for organizations
        {
            // Column 1
            layout.set(0, 1, ChatColor.GRAY + "Online: " + Bukkit.getOnlinePlayers().size());

            layout.set(0, 4, ChatColor.GOLD + "HCTeams - 2000");
            layout.set(0, 5, ChatColor.GOLD + "Classic - 2000");
        }

        {
            // Column 2
            layout.set(1, 0, ChatColor.GOLD.toString() + ChatColor.BOLD + "MineHQ Practice");
            layout.set(1, 1, ChatColor.GRAY + "Your Connection", ((CraftPlayer)player).getHandle().ping);

            layout.set(1, 3, ChatColor.GOLD.toString() + ChatColor.BOLD + "Your Rankings");
            layout.set(1, 4, ChatColor.GOLD + "No Ench - 2000");
            layout.set(1, 5, ChatColor.GOLD + "Vanilla - 2000");

            if (party != null) {
                int partySize = party.getMembers().size();
                layout.set(1, 7, ChatColor.BLUE.toString() + ChatColor.BOLD + "Your Party " + ChatColor.BLUE + "(" + partySize + ")");
            }
        }

        {
            // Column 3
            layout.set(2, 1, ChatColor.GRAY + "In Fights: " + matchHandler.countPlayersPlayingMatches());

            layout.set(2, 4, ChatColor.GOLD + "Gapple - 2000");
            layout.set(2, 5, ChatColor.GOLD + "Archer - 2000");
        }

        if (party != null) {
            int i = 0;
            int y = 8;

            for (UUID member : party.getMembers()) {
                layout.set(i++, y, ChatColor.BLUE + UUIDUtils.name(member));

                if (i == 3) {
                    i = 0;
                    y++;
                }
            }
        }

        return layout;
    }

}