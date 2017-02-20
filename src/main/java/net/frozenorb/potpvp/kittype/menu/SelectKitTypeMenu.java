package net.frozenorb.potpvp.kittype.menu;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.frozenorb.qlib.util.Callback;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class SelectKitTypeMenu extends Menu {

    private final boolean reset;
    private final Callback<KitType> callback;

    public SelectKitTypeMenu(Callback<KitType> callback) {
        this(callback, true);
    }

    public SelectKitTypeMenu(Callback<KitType> callback, boolean reset) {
        super("Select a kit type...");

        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.reset = reset;
    }

    @Override
    public void onClose(Player player) {
        if (reset) {
            InventoryUtils.resetInventoryDelayed(player);
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (KitType kitType : KitType.getAllTypes()) {
            if (!player.isOp() && kitType.isHidden()) {
                continue;
            }

            buttons.put(index++, new KitTypeButton(kitType, callback));
        }

        return buttons;
    }

}