package net.frozenorb.potpvp.tab;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.qlib.tab.LayoutProvider;
import net.frozenorb.qlib.tab.TabLayout;
import net.frozenorb.qlib.util.PlayerUtils;
import net.frozenorb.qlib.util.UUIDUtils;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class PotPvPLayoutProvider implements LayoutProvider {

    private static final int MAX_Y = 20;
    private static final List<String> TEAM_COLORS = new ArrayList<>(); // all of the colors we use to display names for spectators in an FFA match

    @Override
    public TabLayout provide(Player player) {
        TabLayout layout = TabLayout.create(player);

        renderHeader(player, layout);

        if (!PotPvPSI.getInstance().getMatchHandler().isPlayingOrSpectatingMatch(player)) {
            renderLobbyEntries(player, layout);
        } else {
            Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlayingOrSpectating(player);

            boolean isParticipant = match.getTeam(player.getUniqueId()) != null;

            if (isParticipant) {
                renderParticipantEntries(layout, match, player);
            } else {
                MatchTeam previousTeam = match.getPreviousTeam(player.getUniqueId());
                renderSpectatorEntries(layout, match, previousTeam);
            }
        }

        return layout;
    }

    /**
     * Render the tab-list header, which is global (shown in-lobby, in-match, etc).
     */
    private void renderHeader(Player player, TabLayout layout) {
        {
            // Column 1
            layout.set(0, 1, ChatColor.GRAY + "Online: " + Bukkit.getOnlinePlayers().size());
        }

        {
            // Column 2
            layout.set(1, 0, ChatColor.GOLD.toString() + ChatColor.BOLD + "MineHQ PotPvP");
            layout.set(1, 1, ChatColor.GRAY + "Your Connection", PlayerUtils.getPing(player));
        }

        {
            // Column 3
            layout.set(2, 1, ChatColor.GRAY + "In Fights: " + PotPvPSI.getInstance().getMatchHandler().countPlayersPlayingMatches());
        }

    }

    /**
     * Render the tab entries for a player who is participating in a match.
     */
    private void renderParticipantEntries(TabLayout layout, Match match, Player player) {
        List<MatchTeam> teams = match.getTeams();

        // if it's one team versus another
        if (teams.size() == 2) {
            // this method won't be called if the player isn't a participant
            MatchTeam ourTeam = match.getTeam(player.getUniqueId());
            MatchTeam otherTeam = teams.get(0) == ourTeam ? teams.get(1) : teams.get(0);

            boolean duel = ourTeam.getAllMembers().size() == 1 && otherTeam.getAllMembers().size() == 1;

            {
                // Column 1
                // we handle duels a bit differently
                if (!duel) {
                    layout.set(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "Team " + ChatColor.GREEN + "(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
                } else {
                    layout.set(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "You");
                }
                renderTeamMemberOverviewEntries(layout, ourTeam, 0, 4, ChatColor.GREEN);
            }

            {
                // Column 3
                // we handle duels a bit differently
                if (!duel) {
                    layout.set(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Enemies " + ChatColor.RED + "(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")");
                } else {
                    layout.set(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Opponent");
                }
                renderTeamMemberOverviewEntries(layout, otherTeam, 2, 4, ChatColor.RED);
            }
        } else { // it's an FFA or something else like that
            layout.set(1, 3, ChatColor.BLUE + ChatColor.BOLD.toString() + "Party FFA");

            int x = 0;
            int y = 4;

            Map<String, Integer> entries = new LinkedHashMap<>();

            MatchTeam ourTeam = match.getTeam(player.getUniqueId());

            {
                // this is where we'll be adding our team members

                Map<String, Integer> aliveLines = new LinkedHashMap<>();
                Map<String, Integer> deadLines = new LinkedHashMap<>();

                // separate lists to sort alive players before dead
                // + color differently
                for (UUID teamMember : ourTeam.getAllMembers()) {
                    if (ourTeam.isAlive(teamMember)) {
                        aliveLines.put(ChatColor.GREEN + FrozenUUIDCache.name(teamMember),  getPing(teamMember));
                    } else {
                        deadLines.put("&7&m" + FrozenUUIDCache.name(teamMember), getPing(teamMember));
                    }
                }

                entries.putAll(aliveLines);
                entries.putAll(deadLines);
            }

            {
                // this is where we'll be adding everyone else
                Map<String, Integer> deadLines = new LinkedHashMap<>();

                for (MatchTeam otherTeam : match.getTeams()) {
                    if (otherTeam == ourTeam) {
                        continue;
                    }

                    // separate lists to sort alive players before dead
                    // + color differently
                    for (UUID enemy : otherTeam.getAllMembers()) {
                        if (otherTeam.isAlive(enemy)) {
                            entries.put(ChatColor.RED + FrozenUUIDCache.name(enemy), getPing(enemy));
                        } else {
                            deadLines.put("&7&m" + FrozenUUIDCache.name(enemy), getPing(enemy));
                        }
                    }
                }

                entries.putAll(deadLines);
            }

            List<Map.Entry<String, Integer>> result = new ArrayList<>(entries.entrySet());

            // actually display our entries
            for (int index = 0; index < result.size(); index++) {
                Map.Entry<String, Integer> entry = result.get(index);

                layout.set(x++, y, entry.getKey(), entry.getValue());

                if (x == 3 && y == MAX_Y) {
                    // if we're at the last slot, we want to see if we still have alive players to show
                    int aliveLeft = 0;

                    for (int i = index; i < result.size(); i++) {
                        String currentEntry = result.get(i).getKey();
                        boolean dead = ChatColor.getLastColors(currentEntry).equals(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString());

                        if (!dead) {
                            aliveLeft++;
                        }
                    }

                    if (aliveLeft != 0 && aliveLeft != 1) {
                        // if there are players we weren't able to show and if it's more than one
                        // (if it's only one they'll be shown as the last entry [see 17 lines above]), display the number
                        // of alive players we weren't able to show instead.
                        layout.set(x, y, ChatColor.GREEN + "+" + aliveLeft);
                    }

                    break;
                }

                if (x == 3) {
                    x = 0;
                    y++;
                }
            }
        }
    }

    /**
     * Render the tab entries for a player who is spectating a match.
     * This respects their previous team, still showing "Enemies", for example.
     */
    private void renderSpectatorEntries(TabLayout layout, Match match, MatchTeam oldTeam) {
        List<MatchTeam> teams = match.getTeams();

        // if it's one team versus another
        if (teams.size() == 2) {
            MatchTeam teamOne = teams.get(0);
            MatchTeam teamTwo = teams.get(1);

            boolean duel = teamOne.getAllMembers().size() == 1 && teamTwo.getAllMembers().size() == 1;

            // first, we want to check if they were a part of the match and died, and if so, render the tab differently.
            if (oldTeam != null) {
                // if they were, it means it couldn't have been a duel, so we don't check for that below.
                MatchTeam ourTeam = teamOne == oldTeam ? teamOne : teamTwo;
                MatchTeam otherTeam = teamOne == ourTeam ? teamTwo : teamOne;

                {
                    // Column 1
                    if (!duel) {
                        layout.set(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "Team " + ChatColor.GREEN + "(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
                    } else {
                        layout.set(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "You");
                    }
                    renderTeamMemberOverviewEntries(layout, ourTeam, 0, 4, ChatColor.GREEN);
                }

                {
                    // Column 3
                    if (!duel) {
                        layout.set(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Enemies " + ChatColor.RED + "(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")");
                    } else {
                        layout.set(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Opponent");
                    }
                    renderTeamMemberOverviewEntries(layout, otherTeam, 2, 4, ChatColor.RED);
                }

            } else {

                {
                    // Column 1
                    // we handle duels a bit differently
                    if (!duel) {
                        layout.set(0, 3, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Team One (" + teamOne.getAliveMembers().size() + "/" + teamOne.getAllMembers().size() + ")");
                    } else {
                        layout.set(0, 3, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Player One");
                    }
                    renderTeamMemberOverviewEntries(layout, teamOne, 0, 4, ChatColor.LIGHT_PURPLE);
                }

                {
                    // Column 3
                    // we handle duels a bit differently
                    if (!duel) {
                        layout.set(2, 3, ChatColor.AQUA + ChatColor.BOLD.toString() + "Team Two (" + teamTwo.getAliveMembers().size() + "/" + teamTwo.getAllMembers().size() + ")");
                    } else {
                        layout.set(2, 3, ChatColor.AQUA + ChatColor.BOLD.toString() + "Player Two");
                    }
                    renderTeamMemberOverviewEntries(layout, teamTwo, 2, 4, ChatColor.AQUA);
                }

            }
        } else { // it's an FFA or something else like that
            layout.set(1, 3, ChatColor.BLUE + ChatColor.BOLD.toString() + "Party FFA");

            int x = 0;
            int y = 4;

            Map<String, Integer> entries = new LinkedHashMap<>();

            if (oldTeam != null) {
                // if they were a part of this match, we want to render it like we would for an alive player, showing their team-mates first and in green.
                {
                    // this is where we'll be adding our team members
                    Map<String, Integer> aliveLines = new LinkedHashMap<>();
                    Map<String, Integer> deadLines = new LinkedHashMap<>();

                    // separate lists to sort alive players before dead
                    // + color differently
                    for (UUID teamMember : oldTeam.getAllMembers()) {
                        if (oldTeam.isAlive(teamMember)) {
                            aliveLines.put(ChatColor.GREEN + FrozenUUIDCache.name(teamMember),  getPing(teamMember));
                        } else {
                            deadLines.put("&7&m" + FrozenUUIDCache.name(teamMember), getPing(teamMember));
                        }
                    }

                    entries.putAll(aliveLines);
                    entries.putAll(deadLines);
                }

                {
                    // this is where we'll be adding everyone else
                    Map<String, Integer> deadLines = new LinkedHashMap<>();

                    for (MatchTeam otherTeam : match.getTeams()) {
                        if (otherTeam == oldTeam) {
                            continue;
                        }

                        // separate lists to sort alive players before dead
                        // + color differently
                        for (UUID enemy : otherTeam.getAllMembers()) {
                            if (otherTeam.isAlive(enemy)) {
                                entries.put(ChatColor.RED + FrozenUUIDCache.name(enemy), getPing(enemy));
                            } else {
                                deadLines.put("&7&m" + FrozenUUIDCache.name(enemy), getPing(enemy));
                            }
                        }
                    }

                    entries.putAll(deadLines);
                }
            } else {
                // if they're just a random spectator, we'll pick different colors for each team.
                Map<String, Integer> deadLines = new LinkedHashMap<>();
                List<String> colors = ImmutableList.copyOf(TEAM_COLORS);

                for (int index = 0; index < match.getTeams().size(); index++) {
                    MatchTeam team = match.getTeams().get(index);
                    String color = colors.get(index);

                    for (UUID enemy : team.getAllMembers()) {
                        if (team.isAlive(enemy)) {
                            entries.put(color + FrozenUUIDCache.name(enemy), getPing(enemy));
                        } else {
                            deadLines.put("&7&m" + FrozenUUIDCache.name(enemy), getPing(enemy));
                        }
                    }
                }

                entries.putAll(deadLines);
            }

            List<Map.Entry<String, Integer>> result = new ArrayList<>(entries.entrySet());

            // actually display our entries
            for (int index = 0; index < result.size(); index++) {
                Map.Entry<String, Integer> entry = result.get(index);

                layout.set(x++, y, entry.getKey(), entry.getValue());

                if (x == 3 && y == MAX_Y) {
                    // if we're at the last slot, we want to see if we still have alive players to show
                    int aliveLeft = 0;

                    for (int i = index; i < result.size(); i++) {
                        String currentEntry = result.get(i).getKey();
                        boolean dead = ChatColor.getLastColors(currentEntry).equals(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString());

                        if (!dead) {
                            aliveLeft++;
                        }
                    }

                    if (aliveLeft != 0 && aliveLeft != 1) {
                        // if there are players we weren't able to show and if it's more than one
                        // (if it's only one they'll be shown as the last entry [see 17 lines above]), display the number
                        // of alive players we weren't able to show instead.
                        layout.set(x, y, ChatColor.GREEN + "+" + aliveLeft);
                    }

                    break;
                }

                if (x == 3) {
                    x = 0;
                    y++;
                }
            }
        }
    }

    /**
     * Render the tab entries for a player who is currently in the lobby.
     */
    private void renderLobbyEntries(Player player, TabLayout layout) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

        {
            // Column 1
            layout.set(0, 4, ChatColor.GOLD + "HCTeams - 2000");
            layout.set(0, 5, ChatColor.GOLD + "Classic - 2000");
        }

        {
            // Column 2
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
            layout.set(2, 4, ChatColor.GOLD + "Gapple - 2000");
            layout.set(2, 5, ChatColor.GOLD + "Archer - 2000");
        }

        if (party != null) {
            Map<String, Integer> entries = new LinkedHashMap<>();

            UUID leader =  party.getLeader();

            // we display the leader as second, after the watcher. however, if the watcher is the leader, we don't show them twice.
            if (player.getUniqueId() != leader) {
                entries.put(ChatColor.BLUE + player.getName(), PlayerUtils.getPing(player));
            }

            entries.put(ChatColor.BLUE + UUIDUtils.name(leader) + ChatColor.GRAY + "*", getPing(leader));

            for (UUID member : party.getMembers()) {
                // don't display the leader or the watcher again
                if (member == leader || member == player.getUniqueId()) {
                    continue;
                }

                entries.put(ChatColor.BLUE + UUIDUtils.name(member), getPing(member));
            }

            List<Map.Entry<String, Integer>> result = new ArrayList<>(entries.entrySet());

            int x = 0;
            int y = 8;

            // actually display our entries
            for (int index = 0; index < result.size(); index++) {
                Map.Entry<String, Integer> entry = result.get(index);

                layout.set(x++, y, entry.getKey(), entry.getValue());

                if (x == 3 && y == MAX_Y) {
                    // if we're at the last slot, we want to see if we still have alive players to show
                    int leftToShow = result.size() - index;

                    if (leftToShow != 0 && leftToShow != 1) {
                        // if there are players we weren't able to show and if it's more than one
                        // (if it's only one they'll be shown as the last entry [see 8 lines above]), display the number
                        // of alive players we weren't able to show instead.
                        layout.set(x, y, ChatColor.BLUE + "+" + leftToShow);
                    }

                    break;
                }

                if (x == 3) {
                    x = 0;
                    y++;
                }
            }
        }
    }

    /**
     * Render tab entries which represent players of a certain team.
     *
     * @param column - The column we want to render the players in
     * @param start  - The starting Y position.
     *               This method will fill the column starting from that Y position
     *               all the way to the bottom. If it can't fit all players inside
     *               of the column, a number of alive players that couldn't be shown
     *               will be displayed as the last entry.
     */
    private void renderTeamMemberOverviewEntries(TabLayout layout, MatchTeam team, int column, int start, ChatColor color) {
        Map<String, Integer> aliveLines = new LinkedHashMap<>();
        Map<String, Integer> deadLines = new LinkedHashMap<>();

        // separate lists to sort alive players before dead
        // + color differently
        for (UUID teamMember : team.getAllMembers()) {
            if (team.isAlive(teamMember)) {
                aliveLines.put(color + FrozenUUIDCache.name(teamMember), getPing(teamMember));
            } else {
                deadLines.put("&7&m" + FrozenUUIDCache.name(teamMember), getPing(teamMember));
            }
        }

        Map<String, Integer> entries = new LinkedHashMap<>();

        entries.putAll(aliveLines);
        entries.putAll(deadLines);

        List<Map.Entry<String, Integer>> result = new ArrayList<>(entries.entrySet());

        // how many spots we have left
        int spotsLeft = MAX_Y - start;

        // we could've used the 'start' variable, but we create a new one for readability.
        int y = start;

        for (int index = 0; index < result.size(); index++) {
            Map.Entry<String, Integer> entry = result.get(index);

            // we check if we only have 1 more spot to show
            if (spotsLeft == 1) {
                // if so, count how many alive players we have left to show
                int aliveLeft = 0;

                for (int i = index; i < result.size(); i++) {
                    String currentEntry = result.get(i).getKey();
                    boolean dead = !ChatColor.getLastColors(currentEntry).equals(color.toString());

                    if (!dead) {
                        aliveLeft++;
                    }
                }

                // if we have any
                if (aliveLeft != 0) {
                    if (aliveLeft == 1) {
                        // if it's only one, we display them as the last entry
                        layout.set(column, y, entry.getKey(), entry.getValue());
                    } else {
                        // if it's more than one, display a number of how many we couldn't display.
                        layout.set(column, y, color + "+" + aliveLeft);
                    }
                }

                break;
            }

            // if not, just display the entry.
            layout.set(column, y, entry.getKey(), entry.getValue());
            y++;
            spotsLeft--;
        }
    }

    /**
     * Gets a player's ping by their UUID.
     *
     * If the player is online, we return their ping,
     *  otherwise we return Integer.MAX_VALUE, which displays empty bars.
     */
    private int getPing(UUID  playerUuid) {
        return Bukkit.getPlayer(playerUuid) == null ? Integer.MAX_VALUE : PlayerUtils.getPing(Bukkit.getPlayer(playerUuid));
    }

    static {
        // gather all of the colors we want to use for teams when showing them to spectators in FFAs

        List<ChatColor> colors = new ArrayList<>();
        List<ChatColor> formats = new ArrayList<>();

        for (ChatColor color : ChatColor.values()) {
            if (color.isFormat() && color != ChatColor.MAGIC && color != ChatColor.STRIKETHROUGH) {
                formats.add(color);
            } else if (color.isColor() && color != ChatColor.BLACK) {
                colors.add(color);
            }
        }

        for (ChatColor color : colors) {
            TEAM_COLORS.add(color.toString());
        }

        for (ChatColor color : colors) {
            for (ChatColor format : formats) {
                TEAM_COLORS.add(color + format.toString());
            }
        }
    }

}