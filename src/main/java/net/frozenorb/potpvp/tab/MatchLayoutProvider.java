package net.frozenorb.potpvp.tab;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.qlib.tab.TabLayout;
import net.frozenorb.qlib.util.UUIDUtils;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

final class MatchLayoutProvider implements BiConsumer<Player, TabLayout> {

    private static final List<String> TEAM_COLOR_PREFIXES;

    static {
        List<String> teamColors = new ArrayList<>();

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
            teamColors.add(color.toString());
        }

        for (ChatColor color : colors) {
            for (ChatColor format : formats) {
                teamColors.add(color + format.toString());
            }
        }

        TEAM_COLOR_PREFIXES = ImmutableList.copyOf(teamColors);
    }

    @Override
    public void accept(Player player, TabLayout tabLayout) {
        Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlayingOrSpectating(player);

        boolean isParticipant = match.getTeam(player.getUniqueId()) != null;

        if (isParticipant) {
            renderParticipantEntries(tabLayout, match, player);
        } else {
            MatchTeam previousTeam = match.getPreviousTeam(player.getUniqueId());
            renderSpectatorEntries(tabLayout, match, previousTeam);
        }
    }

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
                        aliveLines.put(ChatColor.GREEN + FrozenUUIDCache.name(teamMember),  PotPvPLayoutProvider.getPingOrDefault(teamMember));
                    } else {
                        deadLines.put("&7&m" + FrozenUUIDCache.name(teamMember), PotPvPLayoutProvider.getPingOrDefault(teamMember));
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
                            entries.put(ChatColor.RED + FrozenUUIDCache.name(enemy), PotPvPLayoutProvider.getPingOrDefault(enemy));
                        } else {
                            deadLines.put("&7&m" + FrozenUUIDCache.name(enemy), PotPvPLayoutProvider.getPingOrDefault(enemy));
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

                if (x == 3 && y == PotPvPLayoutProvider.MAX_TAB_Y) {
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
                entries = renderTeamMemberOverviewLines(oldTeam, ChatColor.GREEN);

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
                                entries.put(ChatColor.RED + FrozenUUIDCache.name(enemy), PotPvPLayoutProvider.getPingOrDefault(enemy));
                            } else {
                                deadLines.put("&7&m" + FrozenUUIDCache.name(enemy), PotPvPLayoutProvider.getPingOrDefault(enemy));
                            }
                        }
                    }

                    entries.putAll(deadLines);
                }
            } else {
                // if they're just a random spectator, we'll pick different colors for each team.
                Map<String, Integer> deadLines = new LinkedHashMap<>();

                for (int index = 0; index < match.getTeams().size(); index++) {
                    MatchTeam team = match.getTeams().get(index);
                    String color = TEAM_COLOR_PREFIXES.get(index);

                    for (UUID enemy : team.getAllMembers()) {
                        if (team.isAlive(enemy)) {
                            entries.put(color + FrozenUUIDCache.name(enemy), PotPvPLayoutProvider.getPingOrDefault(enemy));
                        } else {
                            deadLines.put("&7&m" + FrozenUUIDCache.name(enemy), PotPvPLayoutProvider.getPingOrDefault(enemy));
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

                if (x == 3 && y == PotPvPLayoutProvider.MAX_TAB_Y) {
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

    private void renderTeamMemberOverviewEntries(TabLayout layout, MatchTeam team, int column, int start, ChatColor color) {
        List<Map.Entry<String, Integer>> result = new ArrayList<>(renderTeamMemberOverviewLines(team, color).entrySet());

        // how many spots we have left
        int spotsLeft = PotPvPLayoutProvider.MAX_TAB_Y - start;

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

    private Map<String, Integer> renderTeamMemberOverviewLines(MatchTeam team, ChatColor aliveColor) {
        Map<String, Integer> aliveLines = new LinkedHashMap<>();
        Map<String, Integer> deadLines = new LinkedHashMap<>();

        for (UUID member : team.getAllMembers()) {
            int ping = PotPvPLayoutProvider.getPingOrDefault(member);

            if (team.isAlive(member)) {
                aliveLines.put(aliveColor + UUIDUtils.name(member), ping);
            } else {
                deadLines.put("&7&m" + UUIDUtils.name(member), ping);
            }
        }

        Map<String, Integer> result = new LinkedHashMap<>();

        result.putAll(aliveLines);
        result.putAll(deadLines);

        return result;
    }

}