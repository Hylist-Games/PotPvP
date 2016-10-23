package net.frozenorb.potpvp.arena.listener;

import net.frozenorb.potpvp.arena.event.ArenaReleasedEvent;
import net.frozenorb.qlib.cuboid.Cuboid;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public final class ArenaClearListener implements Listener {

    @EventHandler
    public void onArenaReleased(ArenaReleasedEvent event) {
        Set<Chunk> coveredChunks = new HashSet<>();
        Cuboid bounds = event.getArena().getBounds();
        Location minPoint = bounds.getLowerNE();
        Location maxPoint = bounds.getUpperSW();
        World world = minPoint.getWorld();

        for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
            for (int z = minPoint.getBlockZ(); z <= maxPoint.getBlockZ(); z++) {
                coveredChunks.add(world.getChunkAt(x >> 4, z >> 4));
            }
        }

        coveredChunks.forEach(Chunk::load);
        coveredChunks.forEach(chunk -> {
            for (Entity entity : chunk.getEntities()) {
                if (entity instanceof Item && bounds.contains(entity)) {
                    entity.remove();
                }
            }
        });
    }

}