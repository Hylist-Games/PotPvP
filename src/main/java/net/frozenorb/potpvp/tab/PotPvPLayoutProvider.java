package net.frozenorb.potpvp.tab;

import net.frozenorb.qlib.tab.LayoutProvider;
import net.frozenorb.qlib.tab.TabLayout;

import org.bukkit.entity.Player;

public final class PotPvPLayoutProvider implements LayoutProvider {

    // TODO: Get specs + implement

    @Override
    public TabLayout provide(Player player) {
        TabLayout layout = TabLayout.create(player);

        if (layout.is18()) {
            layout.setHeader("&6&lPotPvP\n&7&lwww.minehq.com");
        }

        // Column 1
        {
        }

        // Column 2
        {
            if (!layout.is18()) {
                layout.set(1, 0, "&6&lPotPvP");
                layout.set(1, 1, "&7&lwww.minehq.com");
            }
        }

        // Column 3
        {
        }

        return layout;
    }

}