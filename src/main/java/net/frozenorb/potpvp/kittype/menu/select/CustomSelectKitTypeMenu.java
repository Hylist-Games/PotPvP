package net.frozenorb.potpvp.kittype.menu.select;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.frozenorb.qlib.util.Callback;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Similar to {@link SelectKitTypeMenu} but allows the user to set custom
 * descriptions/item counts for each KitType. For example, this is used by
 * the queue system to show the number of players in each queue prior to joining.
 */
public final class CustomSelectKitTypeMenu extends Menu {

    private final Callback<KitType> callback;
    private final Function<KitType, CustomKitTypeMeta> metaFunc;

    public CustomSelectKitTypeMenu(Callback<KitType> callback, Function<KitType, CustomKitTypeMeta> metaFunc, String title) {
        super(ChatColor.BLUE.toString() + ChatColor.BOLD + title);

        setAutoUpdate(true);

        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.metaFunc = Preconditions.checkNotNull(metaFunc, "metaFunc");
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryDelayed(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (KitType kitType : KitType.getAllTypes()) {
            if (!player.isOp() && kitType.isHidden()) {
                continue;
            }

            CustomKitTypeMeta meta = metaFunc.apply(kitType);
            buttons.put(index++, new KitTypeButton(kitType, callback, meta.getDescription(), meta.getQuantity()));
        }

        return buttons;
    }

    @AllArgsConstructor
    public static final class CustomKitTypeMeta {

        @Getter private int quantity;
        @Getter private List<String> description;

    }

}