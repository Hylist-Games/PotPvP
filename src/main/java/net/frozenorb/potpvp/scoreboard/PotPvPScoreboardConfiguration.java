package net.frozenorb.potpvp.scoreboard;

import net.frozenorb.qlib.scoreboard.ScoreboardConfiguration;
import net.frozenorb.qlib.scoreboard.TitleGetter;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PotPvPScoreboardConfiguration {

    // ⏐ (not |, which is the standard pipe) is similar to a pipe but
    // doesn't have a split in the middle when rendered in Minecraft
    private static final String SCOREBOARD_TITLE = "&6&lMineHQ &7&l⏐ &fPotPvP";

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