package net.frozenorb.potpvp.arena.schematic;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class WorldSchematic {
    @Getter private Vector size;
    private short[] blocks;
    private byte[] blockData;
    private Map<BlockVector, Map<String, Tag>> tileEntitiesMap;

    public Pair<Vector, Vector> place(Vector origin) throws Exception {
        World world = Bukkit.getServer().getWorlds().get(0);
        EditSession editSession = new EditSession(new BukkitWorld(world), Integer.MAX_VALUE);

        int width = size.getBlockX();
        int height = size.getBlockY();
        int length = size.getBlockZ();
        int bx = origin.getBlockX();
        int by = origin.getBlockY();
        int bz = origin.getBlockZ();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    BaseBlock base = new BaseBlock(blocks[index], blockData[index]);
                    BlockVector pt = new BlockVector(x, y, z);

                    if (tileEntitiesMap.containsKey(pt)) {
                        base.setNbtData(new CompoundTag("", tileEntitiesMap.get(pt)));
                    }

                    editSession.setBlock(pt.add(bx, by, bz), base);
                }
            }
        }

        editSession.flushQueue();
        return new Pair<>(origin, origin.add(size));
    }

}
