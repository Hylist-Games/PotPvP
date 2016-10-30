package net.frozenorb.potpvp.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public final class VoidChunkGenerator extends ChunkGenerator {

    @Override
    public byte[] generate(World world, Random random, int cx, int cz) {
        return new byte[32768];
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return world.getSpawnLocation();
    }

}