package net.frozenorb.potpvp.arena.schematic;

import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.world.DataException;

import net.frozenorb.potpvp.arena.ArenaSchematic;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class SchematicUtil {
    private static final SchematicUtil INSTANCE = new SchematicUtil(WorldEditPlugin.getPlugin(WorldEditPlugin.class));
    private final EditSession editSession;

    private SchematicUtil(WorldEditPlugin wep) {
        WorldEdit we = wep.getWorldEdit();
        editSession = new EditSession(new BukkitWorld(Bukkit.getWorlds().get(0)), we.getConfiguration().maxChangeLimit);
    }

    public static SchematicUtil instance() {
        return INSTANCE;
    }

    public void saveSchematic(File saveFile, Location one, Location two) throws Exception {
        if (!saveFile.exists()) {
            saveFile.createNewFile();
        }

        Vector min = getMin(one, two);
        Vector max = getMax(one, two);

        editSession.enableQueue();
        CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
        clipboard.copy(editSession);
        SchematicFormat.MCEDIT.save(clipboard, saveFile);
        editSession.flushQueue();
    }

    public WorldSchematic load(ArenaSchematic schematic) throws Exception {
        return load(schematic.getSchematicFile());
    }

    public WorldSchematic load(File file) throws Exception {
        InputStream stream = new FileInputStream(file);
        NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(stream));

        // Schematic tag
        CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
        nbtStream.close();
        if (!schematicTag.getName().equals("Schematic")) {
            throw new com.sk89q.worldedit.data.DataException("Tag \"Schematic\" does not exist or is not first");
        }

        // Check
        Map<String, Tag> schematic = schematicTag.getValue();
        if (!schematic.containsKey("Blocks")) {
            throw new com.sk89q.worldedit.data.DataException("Schematic file is missing a \"Blocks\" tag");
        }

        // Get information
        short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
        short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
        short height = getChildTag(schematic, "Height", ShortTag.class).getValue();

        // Check type of Schematic
        String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
        if (!materials.equals("Alpha")) {
            throw new com.sk89q.worldedit.data.DataException("Schematic file is not an Alpha schematic");
        }

        // Get blocks
        byte[] blockId = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
        byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
        byte[] addId = new byte[0];
        short[] blocks = new short[blockId.length]; // Have to later combine IDs

        // We support 4096 block IDs using the same method as vanilla Minecraft, where
        // the highest 4 bits are stored in a separate byte array.
        if (schematic.containsKey("AddBlocks")) {
            addId = getChildTag(schematic, "AddBlocks", ByteArrayTag.class).getValue();
        }

        // Combine the AddBlocks data with the first 8-bit block ID
        for (int index = 0; index < blockId.length; index++) {
            if ((index >> 1) >= addId.length) { // No corresponding AddBlocks index
                blocks[index] = (short) (blockId[index] & 0xFF);
            } else {
                if ((index & 1) == 0) {
                    blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
                } else {
                    blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
                }
            }
        }

        // Need to pull out tile entities
        List<Tag> tileEntities = getChildTag(schematic, "TileEntities", ListTag.class).getValue();
        Map<BlockVector, Map<String, Tag>> tileEntitiesMap = new HashMap<>();

        for (Tag tag : tileEntities) {
            if (!(tag instanceof CompoundTag)) continue;
            CompoundTag t = (CompoundTag) tag;

            int x = 0;
            int y = 0;
            int z = 0;

            Map<String, Tag> values = new HashMap<>();

            for (Map.Entry<String, Tag> entry : t.getValue().entrySet()) {
                if (entry.getKey().equals("x")) {
                    if (entry.getValue() instanceof IntTag) {
                        x = ((IntTag) entry.getValue()).getValue();
                    }
                } else if (entry.getKey().equals("y")) {
                    if (entry.getValue() instanceof IntTag) {
                        y = ((IntTag) entry.getValue()).getValue();
                    }
                } else if (entry.getKey().equals("z")) {
                    if (entry.getValue() instanceof IntTag) {
                        z = ((IntTag) entry.getValue()).getValue();
                    }
                }

                values.put(entry.getKey(), entry.getValue());
            }

            BlockVector vec = new BlockVector(x, y, z);
            tileEntitiesMap.put(vec, values);
        }

        Vector size = new Vector(width, height, length);
        return new WorldSchematic(size, blocks, blockData, tileEntitiesMap);
    }

    private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws DataException {
        if (!items.containsKey(key)) {
            throw new DataException("Schematic file is missing a \"" + key + "\" tag");
        }

        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new DataException(key + " tag is not of tag type " + expected.getName());
        }

        return expected.cast(tag);
    }

    private Vector getMin(Location one, Location two) {
        return new Vector(
                Math.min(one.getBlockX(), two.getBlockX()),
                Math.min(one.getBlockY(), two.getBlockY()),
                Math.min(one.getBlockZ(), two.getBlockZ())
        );
    }

    private Vector getMax(Location one, Location two) {
        return new Vector(
                Math.max(one.getBlockX(), two.getBlockX()),
                Math.max(one.getBlockY(), two.getBlockY()),
                Math.max(one.getBlockZ(), two.getBlockZ())
        );
    }
}