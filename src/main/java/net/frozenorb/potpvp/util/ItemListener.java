package net.frozenorb.potpvp.util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ItemListener implements Listener {

    private final Map<ItemStack, Consumer<Player>> handlers = new HashMap<>();
    private Predicate<Player> preProcessPredicate = null;

    protected final void addHandler(ItemStack stack, Consumer<Player> handler) {
        this.handlers.put(stack, handler);
    }

    protected final void setPreProcessPredicate(Predicate<Player> preProcessPredicate) {
        this.preProcessPredicate = preProcessPredicate;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (preProcessPredicate != null && !preProcessPredicate.test(player)) {
            return;
        }

        for (Map.Entry<ItemStack, Consumer<Player>> entry : handlers.entrySet()) {
            if (item.isSimilar(entry.getKey())) {
                event.setCancelled(true);
                entry.getValue().accept(player);
                return;
            }
        }
    }

}