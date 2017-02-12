package net.frozenorb.potpvp.kit.listener;

import net.frozenorb.potpvp.kit.KitItems;
import net.frozenorb.potpvp.kit.menu.kits.KitsMenu;
import net.frozenorb.potpvp.kittype.menu.SelectKitTypeMenu;
import net.frozenorb.potpvp.util.ItemListener;

public final class KitItemListener extends ItemListener {

    public KitItemListener() {
        addHandler(KitItems.OPEN_EDITOR_ITEM, player -> {
            new SelectKitTypeMenu(kitType -> {
                new KitsMenu(kitType).openMenu(player);
            }).openMenu(player);
        });
    }

}