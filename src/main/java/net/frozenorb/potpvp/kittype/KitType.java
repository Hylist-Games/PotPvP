package net.frozenorb.potpvp.kittype;

import com.mongodb.client.MongoCollection;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.MongoUtils;
import net.frozenorb.qlib.qLib;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.JsonAdapter;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Denotes a type of Kit, under which players can queue, edit kits,
 * have elo, etc.
 */
@JsonAdapter(KitTypeJsonAdapter.class)
public final class KitType {

    private static final String MONGO_COLLECTION_NAME = "kitTypeMeta";
    @Getter private static final List<KitType> allTypes = new ArrayList<>();

    static {
        new KitType("HCTEAMS", "HCTeams", ChatColor.GREEN, Material.DIAMOND_SWORD);
        new KitType("ARCHER", "Archer", ChatColor.BLUE, Material.BOW);
        new KitType("SOUP", "Soup", ChatColor.DARK_AQUA, Material.MUSHROOM_SOUP);
        new KitType("AXE", "Axe", ChatColor.RED, Material.IRON_AXE);
        new KitType("NO_ENCHANTS", "No Ench", ChatColor.AQUA, Material.ENCHANTMENT_TABLE);
        new KitType("GAPPLE", "Gapple", ChatColor.GOLD, Material.GOLDEN_APPLE, 1);
        new KitType("VANILLA", "Vanilla", ChatColor.WHITE, Material.POTION, 8225); // 8225 = regen 2 potion
        new KitType("CLASSIC", "Classic", ChatColor.AQUA, Material.DIAMOND_CHESTPLATE);
        new KitType("WIZARD", "Wizard", ChatColor.DARK_PURPLE, Material.BLAZE_POWDER);
    }

    @Getter private final String id;
    @Getter private final String displayName;
    @Getter private final ChatColor displayColor;
    @Getter private final MaterialData icon;
    @Getter private final KitTypeMeta meta;

    private KitType(String id, String displayName, ChatColor displayColor, Material iconType) {
        this(id, displayName, displayColor, iconType, (byte) 0);
    }

    private KitType(String id, String displayName, ChatColor displayColor, Material iconType, int iconData) {
        this.id = id;
        this.displayName = displayName;
        this.displayColor = displayColor;
        this.icon = new MaterialData(iconType, (byte) iconData);

        MongoCollection<Document> metaCollection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
        Document metaJson = metaCollection.find(new Document("_id", id)).first();

        if (metaJson != null) {
            this.meta = qLib.GSON.fromJson(((Document) metaJson.get("meta")).toJson(), KitTypeMeta.class);
        } else {
            this.meta = new KitTypeMeta();
        }

        allTypes.add(this);
    }

    public static KitType byId(String id) {
        for (KitType kitType : allTypes) {
            if (kitType.getId().equalsIgnoreCase(id)) {
                return kitType;
            }
        }

        return null;
    }

    public String getColoredDisplayName() {
        return displayColor + displayName;
    }

    public void saveMetaAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            MongoCollection<Document> metaCollection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
            Document metaJson = Document.parse(qLib.PLAIN_GSON.toJson(meta));
            Document update = new Document("$set", new Document("meta", metaJson));

            metaCollection.updateOne(new Document("_id", id), update, MongoUtils.UPSERT_OPTIONS);
        });
    }

}