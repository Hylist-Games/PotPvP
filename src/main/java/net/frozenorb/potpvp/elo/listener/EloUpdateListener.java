package net.frozenorb.potpvp.elo.listener;

import com.google.common.base.Joiner;

import net.frozenorb.potpvp.elo.EloCalculator;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.match.event.MatchTerminateEvent;
import net.frozenorb.qlib.util.UUIDUtils;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class EloUpdateListener implements Listener {

    private static final String ELO_CHANGE_MESSAGE = ChatColor.translateAlternateColorCodes('&', "&eElo: &a%s &e(+%d) &c%s &e(-%d)");

    private final EloHandler eloHandler;
    private final EloCalculator eloCalculator;

    public EloUpdateListener(EloHandler eloHandler, EloCalculator eloCalculator) {
        this.eloHandler = eloHandler;
        this.eloCalculator = eloCalculator;
    }

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        Match match = event.getMatch();
        KitType kitType = match.getKitType();
        List<MatchTeam> teams = match.getTeams();

        if (!match.isRanked() || teams.size() != 2 || match.getWinner() == null) {
            return;
        }

        MatchTeam winnerTeam = match.getWinner();
        MatchTeam loserTeam = teams.get(0) == winnerTeam ? teams.get(1) : teams.get(0);

        EloCalculator.Result result = eloCalculator.calculate(
            eloHandler.getElo(winnerTeam.getAllMembers(), kitType),
            eloHandler.getElo(loserTeam.getAllMembers(), kitType)
        );

        eloHandler.setElo(winnerTeam.getAllMembers(), kitType, result.getWinnerNew());
        eloHandler.setElo(loserTeam.getAllMembers(), kitType, result.getLoserNew());

        messageResults(match, winnerTeam, loserTeam, result);
    }

    private void messageResults(Match match, MatchTeam winnerTeam, MatchTeam loserTeam, EloCalculator.Result result) {
        String winnerStr;
        String loserStr;

        if (winnerTeam.getAllMembers().size() == 1 && loserTeam.getAllMembers().size() == 1) {
            winnerStr = UUIDUtils.name(winnerTeam.getAllMembers().iterator().next());
            loserStr = UUIDUtils.name(loserTeam.getAllMembers().iterator().next());
        } else {
            Set<String> winnerNames = winnerTeam.getAllMembers().stream().map(UUIDUtils::name).collect(Collectors.toSet());
            Set<String> loserNames = loserTeam.getAllMembers().stream().map(UUIDUtils::name).collect(Collectors.toSet());

            winnerStr = Joiner.on(", ").join(winnerNames);
            loserStr = Joiner.on(", ").join(loserNames);
        }

        // we negate loser gain to convert negative gain to positive (which we prefix with - in the string)
        match.messageAll(String.format(ELO_CHANGE_MESSAGE, winnerStr, result.getWinnerGain(), loserStr, -result.getLoserGain()));
    }

}