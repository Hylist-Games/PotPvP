package net.frozenorb.potpvp.kit.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kit.KitItems;
import net.frozenorb.potpvp.kit.menu.kits.KitsMenu;
import net.frozenorb.potpvp.kittype.menu.select.SelectKitTypeMenu;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.util.ItemListener;

public final class KitItemListener extends ItemListener {

    public KitItemListener() {
        addHandler(KitItems.OPEN_EDITOR_ITEM, player -> {
            MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

            if (matchHandler.isPlayingOrSpectatingMatch(player)) {
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                new KitsMenu(kitType).openMenu(player);
            }, "Select a kit to edit...").openMenu(player);
        });
    }

}