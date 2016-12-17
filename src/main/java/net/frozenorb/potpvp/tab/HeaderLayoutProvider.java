package net.frozenorb.potpvp.tab;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.qlib.tab.TabLayout;
import net.frozenorb.qlib.util.PlayerUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

final class HeaderLayoutProvider implements BiConsumer<Player, TabLayout> {

    @Override
    public void accept(Player player, TabLayout tabLayout) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        column1: {
            tabLayout.set(0, 1, ChatColor.GRAY + "Online: " + Bukkit.getOnlinePlayers().size());
        }

        column2: {
            tabLayout.set(1, 0, ChatColor.GOLD.toString() + ChatColor.BOLD + "MineHQ PotPvP");
            tabLayout.set(1, 1, ChatColor.GRAY + "Your Connection", PlayerUtils.getPing(player));
        }

        column3: {
            tabLayout.set(2, 1, ChatColor.GRAY + "In Fights: " + matchHandler.countPlayersPlayingMatches());
        }
    }

}