package net.frozenorb.potpvp.morpheus;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import com.qrakn.morpheus.game.GameState;
import lombok.experimental.UtilityClass;
import net.frozenorb.qlib.util.ItemBuilder;
import net.frozenorb.qlib.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static net.frozenorb.potpvp.PotPvPLang.LEFT_ARROW;
import static net.frozenorb.potpvp.PotPvPLang.RIGHT_ARROW;
import static net.md_5.bungee.api.ChatColor.BOLD;
import static net.md_5.bungee.api.ChatColor.LIGHT_PURPLE;

@UtilityClass
public final class EventItems {

    public static ItemStack getEventItem() {
        List<Game> game = GameQueue.INSTANCE.getCurrentGames();

        if (game.size() > 0) {
            return ItemBuilder.of(Material.EMERALD).name(LIGHT_PURPLE + "Join An Event").build();
        }

        return null;
    }

}