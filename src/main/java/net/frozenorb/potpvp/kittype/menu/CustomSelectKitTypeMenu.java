package net.frozenorb.potpvp.kittype.menu;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.frozenorb.qlib.util.Callback;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class CustomSelectKitTypeMenu extends Menu {

    private final Callback<KitType> callback;
    private final Function<KitType, List<String>> descriptionFunc;
    private final Function<KitType, Integer> quantityFunc;

    public CustomSelectKitTypeMenu(Callback<KitType> callback,
                                   Function<KitType, List<String>> descriptionFunc,
                                   Function<KitType, Integer> quantityFunc) {
        super("Select a kit type...");

        setAutoUpdate(true);

        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.descriptionFunc = Preconditions.checkNotNull(descriptionFunc, "descriptionFunc");
        this.quantityFunc = Preconditions.checkNotNull(quantityFunc, "quantityFunc");
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryDelayed(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (KitType kitType : KitType.values()) {
            List<String> description = descriptionFunc.apply(kitType);
            int quantity = quantityFunc.apply(kitType);

            buttons.put(index++, new KitTypeButton(kitType, callback, description, quantity));
        }

        return buttons;
    }

}