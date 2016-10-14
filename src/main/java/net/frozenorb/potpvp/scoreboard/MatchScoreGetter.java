package net.frozenorb.potpvp.scoreboard;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.qlib.util.TimeUtils;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

// the list here must be viewed as rendered javadoc to make sense. In IntelliJ, click on
// 'MatchScoreGetter' and press Control+Q
/**
 * Implements the scoreboard as defined in {@link net.frozenorb.potpvp.scoreboard}<br />
 * This class is divided up into multiple prodcedures to reduce overall complexity<br /><br />
 *
 * Although there are many possible outcomes, for a 4v4 match this code would take the
 * following path:<br /><br />
 *
 * <ul>
 *   <li>accept()</li>
 *   <ul>
 *     <li>renderParticipantLines()</li>
 *     <ul>
 *       <li>render4v4MatchLines()</li>
 *       <ul>
 *         <li>renderTeamMemberOverviewLines()</li>
 *         <li>renderTeamMemberOverviewLines()</li>
 *       </ul>
 *     </ul>
 *     <li>renderMetaLines()</li>
 *   </ul>
 * </ul>
 */
final class MatchScoreGetter implements BiConsumer<Player, List<String>> {

    @Override
    public void accept(Player player, List<String> scores) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(player);

        // this method shouldn't have even been called if
        // they're not in a match
        if (match == null) {
            return;
        }

        boolean participant = match.getTeam(player.getUniqueId()) != null;

        if (participant) {
            renderParticipantLines(scores, match, player);
        } else {
            renderSpectatorLines(scores, match);
        }

        renderMetaLines(scores, match, participant);
    }

    private void renderParticipantLines(List<String> scores, Match match, Player player) {
        List<MatchTeam> teams = match.getTeams();

        // only render scoreboard if we have two teams
        if (teams.size() != 2) {
            return;
        }

        // this method won't be called if the player isn't a participant
        MatchTeam ourTeam = match.getTeam(player.getUniqueId());
        MatchTeam otherTeam = teams.get(0) == ourTeam ? teams.get(1) : teams.get(0);

        // we use getAllMembers instead of getAliveMembers to avoid
        // mid-match scoreboard changes as players die / disconnect
        int ourTeamSize = ourTeam.getAllMembers().size();
        int otherTeamSize = otherTeam.getAllMembers().size();

        if (ourTeamSize == 1 && otherTeamSize == 1) {
            render1v1MatchLines(scores, otherTeam);
        } else if (ourTeamSize <= 2 && otherTeamSize <= 2) {
            render2v2MatchLines(scores, match, ourTeam, otherTeam, player);
        } else if (ourTeamSize <= 4 && otherTeamSize <= 4) {
            render4v4MatchLines(scores, ourTeam, otherTeam);
        } else if (ourTeam.getAllMembers().size() <= 9) {
            renderLargeMatchLines(scores, ourTeam, otherTeam);
        } else {
            renderJumboMatchLines(scores, ourTeam, otherTeam);
        }
    }

    private void render1v1MatchLines(List<String> scores, MatchTeam otherTeam) {
        UUID opponent = otherTeam.getAllMembers().iterator().next();
        scores.add("&c&lOpponent: &f" + FrozenUUIDCache.name(opponent));
    }

    private void render2v2MatchLines(List<String> scores, Match match, MatchTeam ourTeam, MatchTeam otherTeam, Player player) {
        // 2v2, but potentially 1v2 / 1v1 if players have died
        UUID partnerUuid = null;

        for (UUID teamMember : ourTeam.getAllMembers()) {
            if (teamMember != player.getUniqueId()) {
                partnerUuid = teamMember;
                break;
            }
        }

        if (partnerUuid != null) {
            String namePrefix;
            String healthStr;

            if (ourTeam.isAlive(partnerUuid)) {
                Player partnerPlayer = Bukkit.getPlayer(partnerUuid); // will never be null (or isAlive would've returned false)
                double health = Math.round(partnerPlayer.getHealth()) / 2D;
                ChatColor healthColor;

                if (health > 8) {
                    healthColor = ChatColor.GREEN;
                } else if (health > 6) {
                    healthColor = ChatColor.YELLOW;
                } else if (health > 4) {
                    healthColor = ChatColor.GOLD;
                } else if (health > 1) {
                    healthColor = ChatColor.RED;
                } else {
                    healthColor = ChatColor.DARK_RED;
                }

                namePrefix = "&a";
                healthStr = healthColor.toString() + health + " ❤";
            } else {
                namePrefix = "&7&m";
                healthStr = "&4RIP";
            }

            scores.add(namePrefix + FrozenUUIDCache.name(partnerUuid));
            scores.add(healthStr);
            scores.add("&b");
        }

        scores.add("&c&lOpponents");
        scores.addAll(renderTeamMemberOverviewLines(otherTeam));
        scores.add("&c");
    }

    private void render4v4MatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam) {
        // Above a 2v2, but up to a 4v4.
        scores.add("&a&lTeam &a(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
        scores.addAll(renderTeamMemberOverviewLines(ourTeam));
        scores.add("&b");
        scores.add("&c&lOpponents &c(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")");
        scores.addAll(renderTeamMemberOverviewLines(otherTeam));
        scores.add("&c");
    }

    private void renderLargeMatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam) {
        // We just display THEIR team's names, and the other team is a number.
        scores.add("&a&lTeam &a(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
        scores.addAll(renderTeamMemberOverviewLines(ourTeam));
        scores.add("&b");
        scores.add("&c&lOpponents: &f" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size());
    }

    private void renderJumboMatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam) {
        // We just display numbers.
        scores.add("&a&lTeam: &f" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size());
        scores.add("&c&lOpponents: &f" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size());
    }

    private void renderSpectatorLines(List<String> scores, Match match) {
        scores.add("&eKit: &f" + match.getKitType().getDisplayName());

        List<MatchTeam> teams = match.getTeams();

        // only render team overview if we have two teams
        if (teams.size() == 2) {
            MatchTeam teamOne = teams.get(0);
            MatchTeam teamTwo = teams.get(1);

            scores.add("&dTeam One: &f" + teamOne.getAliveMembers().size() + "/" + teamOne.getAllMembers().size()); // team 1 alive
            scores.add("&bTeam Two: &f" + teamTwo.getAliveMembers().size() + "/" + teamOne.getAllMembers().size()); // team 2 alive
        }
    }

    private void renderMetaLines(List<String> scores, Match match, boolean participant) {
        Instant startedAt = match.getStartedAt();
        Instant endedAt = match.getEndedAt();

        // if the match hasn't yet started we pretend it started now
        // we do this so the duration is frozen at 0 until the countdown is done
        if (startedAt == null) {
            startedAt = Instant.now();
        }

        // if the match is in progress we pretend it ended at our current time.
        // otherwise we use its actual end time to avoid incrementing duration
        // for players while a match is ending
        if (endedAt == null) {
            endedAt = Instant.now();
        }

        int duration = (int) ChronoUnit.SECONDS.between(startedAt, endedAt);
        String formattedDuration = TimeUtils.formatIntoMMSS(duration);

        // spectators don't have any bold entries on their scoreboard
        scores.add("&6" + (participant ? "&l" : "") + "Duration: &f" + formattedDuration);
    }

    /* Returns the names of all alive players, colored + indented, followed
       by the names of all dead players, colored + indented. */
    private List<String> renderTeamMemberOverviewLines(MatchTeam team) {
        List<String> aliveLines = new ArrayList<>();
        List<String> deadLines = new ArrayList<>();

        // seperate lists to sort alive players before dead
        // + color differently
        for (UUID teamMember : team.getAllMembers()) {
            if (team.isAlive(teamMember)) {
                aliveLines.add(" &f" + FrozenUUIDCache.name(teamMember));
            } else {
                deadLines.add(" &7&m" + FrozenUUIDCache.name(teamMember));
            }
        }

        List<String> result = new ArrayList<>();

        result.addAll(aliveLines);
        result.addAll(deadLines);

        return result;
    }

}