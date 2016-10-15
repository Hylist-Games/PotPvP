package net.frozenorb.potpvp.kit.menu;

import net.frozenorb.potpvp.kit.Kit;
import net.frozenorb.potpvp.kit.KitHandler;
import net.frozenorb.potpvp.kit.menu.button.kits.KitDeleteButton;
import net.frozenorb.potpvp.kit.menu.button.kits.KitEditButton;
import net.frozenorb.potpvp.kit.menu.button.kits.KitIconButton;
import net.frozenorb.potpvp.kit.menu.button.kits.KitRenameButton;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class KitsMenu extends Menu {

    private final KitType kitType;

    public KitsMenu(KitType kitType) {
        super("Viewing " + kitType.getName() + " kits");

        setPlaceholder(true);
        setAutoUpdate(true);

        this.kitType = kitType;
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryLater(player, 5);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        // kit slots are 1-indexed
        for (int i = 1; i <= KitHandler.KITS_PER_TYPE; i++) {
            int column = (i * 2) - 1; // -1 to compensate for this being 0-indexed

            // TODO
            Optional<Kit> kitOpt = /*PotPvPSI.getInstance().getKitHandler().getKit(player.getUniqueId(), kitType, i);*/Optional.empty();

            buttons.put(getSlot(column, 0), new KitIconButton(kitOpt.orElse(null), kitType, i));
            buttons.put(getSlot(column, 2), new KitEditButton(kitOpt.orElse(null), kitType, i));

            if (kitOpt.isPresent()) {
                buttons.put(getSlot(column, 3), new KitRenameButton(kitOpt.get()));
                buttons.put(getSlot(column, 4), new KitDeleteButton(kitType, i));
            } else {
                buttons.put(getSlot(column, 3), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.SILVER.getWoolData(), ""));
                buttons.put(getSlot(column, 4), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.SILVER.getWoolData(), ""));
            }
        }

        return buttons;
    }

}