package net.frozenorb.potpvp.postmatchinv.menu;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.postmatchinv.PostMatchInvHandler;
import net.frozenorb.potpvp.postmatchinv.PostMatchPlayer;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.util.ItemUtils;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.frozenorb.qlib.util.UUIDUtils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PostMatchMenu extends Menu {

    private final PostMatchPlayer target;

    public PostMatchMenu(PostMatchPlayer target) {
        super("Inventory of " + UUIDUtils.name(target.getPlayerUuid()));

        this.target = Preconditions.checkNotNull(target, "target");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int x = 0;
        int y = 0;

        for (ItemStack inventoryItem : target.getInventory()) {
            buttons.put(getSlot(x, y), Button.fromItem(inventoryItem));

            if (x++ > 7) {
                x = 0;
                y++;
            }
        }

        x = 3; // start armor backwards, helm first

        for (ItemStack armorItem : target.getArmor()) {
            buttons.put(getSlot(x--, y), Button.fromItem(armorItem));
        }

        y++; // advance line for status buttons

        buttons.put(getSlot(0, y), new PostMatchHealthButton(target.getHealth()));
        buttons.put(getSlot(1, y), new PostMatchFoodLevelButton(target.getHunger()));

        List<PotionEffect> potionEffects = target.getPotionEffects();

        if (!potionEffects.isEmpty()) {
            buttons.put(getSlot(2, y), new PostMatchPotionEffectsButton(potionEffects));
        }

        int healthPotsRemaining = ItemUtils.countAmountMatching(target.getInventory(), ItemUtils.INSTANT_HEAL_POTION_PREDICATE);

        if (healthPotsRemaining > 0) {
            String playerName = UUIDUtils.name(target.getPlayerUuid());
            buttons.put(getSlot(3, y), new PostMatchPotionsLeftButton(playerName, healthPotsRemaining));
        }

        // swap to other player button (for 1v1s)
        PostMatchInvHandler postMatchInvHandler = PotPvPSI.getInstance().getPostMatchInvHandler();
        Collection<PostMatchPlayer> postMatchPlayers = postMatchInvHandler.getPostMatchData(player.getUniqueId()).values();

        if (postMatchPlayers.size() == 2) {
            PostMatchPlayer otherPlayer = null;

            for (PostMatchPlayer postMatchPlayer : postMatchPlayers) {
                if (!postMatchPlayer.getPlayerUuid().equals(target.getPlayerUuid())) {
                    otherPlayer = postMatchPlayer;
                }
            }

            // will never be null (as we checked size earlier)
            buttons.put(getSlot(8, y), new PostMatchSwapTargetButton(otherPlayer));
        }

        return buttons;
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryDelayed(player);
    }

}