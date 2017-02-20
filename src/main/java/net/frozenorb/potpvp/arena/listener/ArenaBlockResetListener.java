package net.frozenorb.potpvp.arena.listener;

import net.frozenorb.potpvp.arena.event.ArenaReleasedEvent;
import net.frozenorb.qlib.cuboid.Cuboid;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Remove changed blocks when {@link net.frozenorb.potpvp.arena.Arena}s are released.
 */
public final class ArenaBlockResetListener implements Listener {

    private final Map<Location, BlockState> originalStates = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        recordOriginalState(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        recordOriginalState(event.getBlock());
    }

    private void recordOriginalState(Block block) {
        // we only putIfAbsent because there could be an earlier
        // state we'd override by inserting its current state
        // (think a player placing and breaking a block over and over)
        originalStates.putIfAbsent(block.getLocation(), block.getState());
    }

    @EventHandler
    public void onArenaReleased(ArenaReleasedEvent event) {
        Cuboid bounds = event.getArena().getBounds();

        originalStates.entrySet().removeIf(entry -> {
           if (bounds.contains(entry.getKey())) {
               entry.getValue().update(true);
               return true;
           }

           return false;
        });
    }

}