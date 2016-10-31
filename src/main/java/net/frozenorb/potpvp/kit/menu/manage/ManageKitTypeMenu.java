package net.frozenorb.potpvp.kit.menu.manage;

import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mazen Kotb
 */
public class ManageKitTypeMenu extends Menu {
    private KitType type;

    public ManageKitTypeMenu(KitType type) {
        super("Editing: " + type.getDisplayName());

        setNoncancellingInventory(true);
        setUpdateAfterClick(false);

        this.type = type;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        // The vertical row
        for (int i = 0; i <= 5; i++) {
            buttons.put(getSlot(1, i), Button.placeholder(Material.OBSIDIAN));
        }

        // The horizontal row
        for (int i = 0; i <= 8; i++) {
            buttons.put(getSlot(i, 1), Button.placeholder(Material.OBSIDIAN));
        }

        // top row slots
        for (int i = 3; i < 8; i++) {
            buttons.put(getSlot(i, 0), Button.placeholder(Material.OBSIDIAN));
        }

        buttons.put(getSlot(0, 0), new KitTypeInfoButton(type));
        buttons.put(getSlot(1, 0), Button.placeholder(Material.OBSIDIAN));
        buttons.put(getSlot(2, 0), new SaveKitTypeButton(type));
        buttons.put(getSlot(7, 0), new ManageExitButton());

        for (ItemStack armorItem : type.getMeta().getDefaultArmor()) {
            int armorYOffset = 2;
            int armorSlot = -1;

            if (armorItem.getType().name().contains("HELMET")) {
                armorSlot = 0;
            } else if (armorItem.getType().name().contains("CHESTPLATE")) {
                armorSlot = 1;
            } else if (armorItem.getType().name().contains("LEGGINGS")) {
                armorSlot = 2;
            } else if (armorItem.getType().name().contains("BOOTS")) {
                armorSlot = 3;
            }

            buttons.put(getSlot(0, armorSlot + armorYOffset), Button.fromItem(armorItem));
        }

        ItemStack[] kit = type.getMeta().getEditorItems();
        int index = -1;

        o:
        for (int x = 2; x <= 5; x++) {
            for (int z = 2; z < 9; z++) {
                if (index >= kit.length) {
                    break o;
                }

                ItemStack stack = kit[++index];

                if (stack != null && stack.getType() != Material.AIR) {
                    buttons.put(getSlot(x, z), Button.fromItem(stack));
                }
            }
        }

        return buttons;
    }
}
