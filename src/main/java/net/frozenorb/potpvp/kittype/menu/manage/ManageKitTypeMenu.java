package net.frozenorb.potpvp.kittype.menu.manage;

import net.frozenorb.potpvp.command.ManageCommand;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.util.MenuBackButton;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mazen Kotb
 */
public class ManageKitTypeMenu extends Menu {

    private final KitType type;

    public ManageKitTypeMenu(KitType type) {
        super("Editing " + type.getDisplayName());

        setNoncancellingInventory(true);
        setUpdateAfterClick(false);

        this.type = type;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        // Vertical row
        for (int i = 1; i <= 5; i++) {
            buttons.put(getSlot(1, i), Button.placeholder(Material.OBSIDIAN));
        }

        // Horizontal row
        for (int i = 1; i <= 8; i++) {
            buttons.put(getSlot(i, 1), Button.placeholder(Material.OBSIDIAN));
        }

        buttons.put(getSlot(1, 0), new SaveKitTypeButton(type));
        buttons.put(getSlot(2, 0), new CancelKitTypeEditButton());

        buttons.put(getSlot(8, 0), new MenuBackButton(p -> new ManageCommand.ManageMenu().openMenu(p)));

        ItemStack[] kit = type.getEditorItems();
        int x = 0;
        int y = 0;

        for (ItemStack editorItem : kit) {
            if (editorItem != null) {
                if (editorItem.getType() != Material.AIR) {
                    buttons.put(getSlot(x + 2, y + 2), nonCancellingItem(editorItem));
                }
            }

            x++;

            if (x > 6) {
                x = 0;
                y++;

                if (y >= 4) {
                    break;
                }
            }
        }

        return buttons;
    }

    private Button nonCancellingItem(ItemStack stack) {
        return new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return stack;
            }

            @Override
            public String getName(Player player) {
                return stack.getItemMeta().getDisplayName();
            }

            @Override
            public List<String> getDescription(Player player) {
                return stack.getItemMeta().getLore();
            }

            @Override
            public Material getMaterial(Player player) {
                return stack.getType();
            }

            @Override
            public boolean shouldCancel(Player player, int slot, ClickType clickType) {
                return false;
            }
        };
    }
}
