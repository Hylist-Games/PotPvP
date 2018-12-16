package net.frozenorb.potpvp.scoreboard;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.qlib.scoreboard.ScoreboardConfiguration;
import net.frozenorb.qlib.scoreboard.TitleGetter;

import org.bukkit.ChatColor;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PotPvPScoreboardConfiguration {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration configuration = new ScoreboardConfiguration();

        configuration.setTitleGetter(TitleGetter.forStaticString(PotPvPSI.getInstance().getDominantColor() == ChatColor.LIGHT_PURPLE ? "&d&lVeltPvP" : "&5&lArcane"));
        configuration.setScoreGetter(new MultiplexingScoreGetter(
            new MatchScoreGetter(),
            new LobbyScoreGetter()
        ));

        return configuration;
    }

}
