package net.frozenorb.potpvp.scoreboard;

import net.frozenorb.qlib.scoreboard.ScoreboardConfiguration;
import net.frozenorb.qlib.scoreboard.TitleGetter;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PotPvPScoreboardConfiguration {

    private static final String SCOREBOARD_TITLE = "&5&lArcane [&7&lPractice&5&l]";

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration configuration = new ScoreboardConfiguration();

        configuration.setTitleGetter(TitleGetter.forStaticString(SCOREBOARD_TITLE));
        configuration.setScoreGetter(new MultiplexingScoreGetter(
            new LobbyScoreGetter(),
            new MatchScoreGetter()
        ));

        return configuration;
    }

}