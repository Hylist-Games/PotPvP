package net.frozenorb.potpvp.tab;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.qlib.tab.LayoutProvider;
import net.frozenorb.qlib.tab.TabLayout;
import net.frozenorb.qlib.util.PingUtils;
import net.frozenorb.qlib.util.PlayerUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.BiConsumer;

public final class PotPvPLayoutProvider implements LayoutProvider {

    static final int MAX_TAB_Y = 20;

    private final BiConsumer<Player, TabLayout> headerLayoutProvider = new HeaderLayoutProvider();
    private final BiConsumer<Player, TabLayout> lobbyLayoutProvider = new LobbyLayoutProvider();
    private final BiConsumer<Player, TabLayout> matchLayoutProvider = new MatchLayoutProvider();

    @Override
    public TabLayout provide(Player player) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        TabLayout tabLayout = TabLayout.create(player);

        headerLayoutProvider.accept(player, tabLayout);

        if (matchHandler.isPlayingOrSpectatingMatch(player)) {
            matchLayoutProvider.accept(player, tabLayout);
        } else {
            lobbyLayoutProvider.accept(player, tabLayout);
        }

        return tabLayout;
    }

    static int getPingOrDefault(UUID check) {
        Player player = Bukkit.getPlayer(check);
        return player != null ? PlayerUtils.getPing(player) : 0;
    }

}