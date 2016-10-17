package net.frozenorb.potpvp.tab;

import net.frozenorb.qlib.tab.LayoutProvider;
import net.frozenorb.qlib.tab.TabLayout;

import org.bukkit.entity.Player;

public final class PotPvPLayoutProvider implements LayoutProvider {

    @Override
    public TabLayout provide(Player player) {
        TabLayout layout = TabLayout.create(player);
        // {}s are only used for organizations

        {
            // Column 1
        }

        {
            // Column 2
        }

        {
            // Column 3
        }

        return layout;
    }

}