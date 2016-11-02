package net.frozenorb.potpvp.tab;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.qlib.tab.LayoutProvider;
import net.frozenorb.qlib.tab.TabLayout;
import net.frozenorb.qlib.util.UUIDUtils;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PotPvPLayoutProvider implements LayoutProvider {

    @Override
    public TabLayout provide(Player player) {
        TabLayout layout = TabLayout.create(player);

        if (!PotPvPSI.getInstance().getMatchHandler().isPlayingOrSpectatingMatch(player)) {
            renderLobbyEntries(player, layout);
        } else {
            Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlayingOrSpectating(player);

            boolean isParticipant = match.getTeam(player.getUniqueId()) != null;

            if (isParticipant) {
                renderParticipantEntries(layout, match, player);
            } else {
                MatchTeam previousTeam = match.getPreviousTeam(player.getUniqueId());
                renderSpectatorEntries(layout, match, player, previousTeam);
            }
        }

        return layout;
    }

    private void renderParticipantEntries(TabLayout layout, Match match, Player player) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

        List<MatchTeam> teams = match.getTeams();

        // only render tab if we have two teams
        if (teams.size() == 2) {
            // this method won't be called if the player isn't a participant
            MatchTeam ourTeam = match.getTeam(player.getUniqueId());
            MatchTeam otherTeam = teams.get(0) == ourTeam ? teams.get(1) : teams.get(0);

            {
                // Column 1
                layout.set(0, 1, ChatColor.GRAY + "Online: " + Bukkit.getOnlinePlayers().size());

                if (party != null) {
                    layout.set(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "Your Team " + ChatColor.GREEN + "(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");

                    renderTeamMemberOverviewEntries(layout, ourTeam, false, 0, 4);
                }
            }

            {
                // Column 2
                layout.set(1, 0, ChatColor.GOLD.toString() + ChatColor.BOLD + "MineHQ Practice");
                layout.set(1, 1, ChatColor.GRAY + "Your Connection", ((CraftPlayer)player).getHandle().ping);
            }

            {
                // Column 3
                layout.set(2, 1, ChatColor.GRAY + "In Fights: " + PotPvPSI.getInstance().getMatchHandler().countPlayersPlayingMatches());

                if (party != null) {
                    layout.set(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Enemy Team" + ChatColor.RED + "(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")");

                    renderTeamMemberOverviewEntries(layout, otherTeam, true, 0, 4);
                }
            }
        }
    }

    private void renderSpectatorEntries(TabLayout layout, Match match, Player player, MatchTeam oldTeam) {
        List<MatchTeam> teams = match.getTeams();

        // only render tab if we have two teams
        if (teams.size() == 2) {
            MatchTeam teamOne = teams.get(0);
            MatchTeam teamTwo = teams.get(1);

            // first, we want to check if they were a part of the match and died, and if so, render the tab differently.
            if (oldTeam != null) {
                MatchTeam ourTeam = teamOne == oldTeam ? teamOne : teamTwo;
                MatchTeam otherTeam = teamOne == ourTeam ? teamTwo : teamOne;

                {
                    // Column 1
                    layout.set(0, 1, ChatColor.GRAY + "Online: " + Bukkit.getOnlinePlayers().size());

                    layout.set(0, 3, ChatColor.GREEN + "Your Team (" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");

                    renderTeamMemberOverviewEntries(layout, ourTeam, false, 0, 4);
                }

                {
                    // Column 2
                    layout.set(1, 0, ChatColor.GOLD.toString() + ChatColor.BOLD + "MineHQ Practice");
                    layout.set(1, 1, ChatColor.GRAY + "Your Connection", ((CraftPlayer)player).getHandle().ping);
                }

                {
                    // Column 3
                    layout.set(2, 1, ChatColor.GRAY + "In Fights: " + PotPvPSI.getInstance().getMatchHandler().countPlayersPlayingMatches());

                    layout.set(2, 3, ChatColor.RED + "Enemy Team (" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")");

                    renderTeamMemberOverviewEntries(layout, otherTeam, true, 0, 4);
                }

            } else {

                {
                    // Column 1
                    layout.set(0, 1, ChatColor.GRAY + "Online: " + Bukkit.getOnlinePlayers().size());

                    layout.set(0, 3, ChatColor.LIGHT_PURPLE + "Team One (" + teamOne.getAliveMembers().size() + "/" + teamOne.getAllMembers().size() + ")");

                    renderTeamOverviewEntries(layout, teamOne, 0, 4, ChatColor.LIGHT_PURPLE);
                }

                {
                    // Column 2
                    layout.set(1, 0, ChatColor.GOLD.toString() + ChatColor.BOLD + "MineHQ Practice");
                    layout.set(1, 1, ChatColor.GRAY + "Your Connection", ((CraftPlayer)player).getHandle().ping);
                }

                {
                    // Column 3
                    layout.set(2, 1, ChatColor.GRAY + "In Fights: " + PotPvPSI.getInstance().getMatchHandler().countPlayersPlayingMatches());

                    layout.set(2, 3, ChatColor.AQUA + "Team Two (" + teamTwo.getAliveMembers().size() + "/" + teamTwo.getAllMembers().size() + ")");

                    renderTeamOverviewEntries(layout, teamTwo, 0, 4, ChatColor.AQUA);
                }

            }
        }
    }

    private void renderLobbyEntries(Player player, TabLayout layout) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

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
            layout.set(2, 1, ChatColor.GRAY + "In Fights: " + PotPvPSI.getInstance().getMatchHandler().countPlayersPlayingMatches());

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
    }

    private void renderTeamOverviewEntries(TabLayout layout, MatchTeam team, int column, int start, ChatColor color) {
        List<String> aliveLines = new ArrayList<>();
        List<String> deadLines = new ArrayList<>();

        // separate lists to sort alive players before dead
        // + color differently
        for (UUID teamMember : team.getAllMembers()) {
            if (team.isAlive(teamMember)) {
                aliveLines.add(color + FrozenUUIDCache.name(teamMember));
            } else {
                deadLines.add("&7&m" + FrozenUUIDCache.name(teamMember));
            }
        }

        List<String> result = new ArrayList<>();

        result.addAll(aliveLines);
        result.addAll(deadLines);

        int index = start;
        for (String entry : result) {
            layout.set(column, index, entry);
            index++;
        }
    }

    private void renderTeamMemberOverviewEntries(TabLayout layout, MatchTeam team, boolean enemy, int column, int start) {
        List<String> aliveLines = new ArrayList<>();
        List<String> deadLines = new ArrayList<>();

        // separate lists to sort alive players before dead
        // + color differently
        for (UUID teamMember : team.getAllMembers()) {
            if (team.isAlive(teamMember)) {
                aliveLines.add((!enemy ? ChatColor.GREEN : ChatColor.RED) + FrozenUUIDCache.name(teamMember));
            } else {
                deadLines.add("&7&m" + FrozenUUIDCache.name(teamMember));
            }
        }

        List<String> result = new ArrayList<>();

        result.addAll(aliveLines);
        result.addAll(deadLines);

        int index = start;
        for (String entry : result) {
            layout.set(column, index, entry);
            index++;
        }
    }

}