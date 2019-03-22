package net.frozenorb.potpvp.scoreboard;

import java.util.function.BiConsumer;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import com.qrakn.morpheus.game.GameState;
import org.bukkit.entity.Player;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.qlib.scoreboard.ScoreGetter;
import net.frozenorb.qlib.util.LinkedList;

final class MultiplexingScoreGetter implements ScoreGetter {

    private final BiConsumer<Player, LinkedList<String>> matchScoreGetter;
    private final BiConsumer<Player, LinkedList<String>> lobbyScoreGetter;
    private final BiConsumer<Player, LinkedList<String>> gameScoreGetter;

    MultiplexingScoreGetter(
        BiConsumer<Player, LinkedList<String>> matchScoreGetter,
        BiConsumer<Player, LinkedList<String>> lobbyScoreGetter,
        BiConsumer<Player, LinkedList<String>> gameScoreGetter
    ) {
        this.matchScoreGetter = matchScoreGetter;
        this.lobbyScoreGetter = lobbyScoreGetter;
        this.gameScoreGetter = gameScoreGetter;
    }

    @Override
    public void getScores(LinkedList<String> scores, Player player) {
        if (PotPvPSI.getInstance() == null) return;
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();

        if (settingHandler.getSetting(player, Setting.SHOW_SCOREBOARD)) {
            if (matchHandler.isPlayingOrSpectatingMatch(player)) {
                matchScoreGetter.accept(player, scores);
            } else {
                Game game = GameQueue.INSTANCE.getCurrentGame(player);

                if (game != null && game.getPlayers().contains(player) && game.getState() != GameState.ENDED) {
                    gameScoreGetter.accept(player, scores);
                } else {
                    lobbyScoreGetter.accept(player, scores);
                }
            }
        }

        if (!scores.isEmpty()) {
            scores.addFirst("&a&7&m--------------------");
            scores.add("&f&7&m--------------------");
        }
    }

}