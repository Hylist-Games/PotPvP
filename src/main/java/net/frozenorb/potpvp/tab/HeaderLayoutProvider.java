package net.frozenorb.potpvp.tab;

import java.util.function.BiConsumer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.qlib.tab.TabLayout;

final class HeaderLayoutProvider implements BiConsumer<Player, TabLayout> {

    @Override
    public void accept(Player player, TabLayout tabLayout) {
        header: {
            tabLayout.set(1, 0, PotPvPSI.getInstance().getDominantColor().toString() + ChatColor.BOLD + (PotPvPSI.getInstance().getDominantColor() == ChatColor.DARK_PURPLE ? "Arcane" : "VeltPvP") +" Practice");
        }

        /*
        status: {
            tabLayout.set(1, 1, ChatColor.GRAY + "Your Connection", Math.max(((PlayerUtils.getPing(player) + 5) / 10) * 10, 1));
        }
        */
    }

}
