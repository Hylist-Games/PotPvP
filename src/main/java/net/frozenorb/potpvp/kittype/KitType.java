package net.frozenorb.potpvp.kittype;

import com.mongodb.client.MongoCollection;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.MongoUtils;
import net.frozenorb.qlib.qLib;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.JsonAdapter;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.SerializedName;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Denotes a type of Kit, under which players can queue, edit kits,
 * have elo, etc.
 */
@JsonAdapter(KitTypeJsonAdapter.class)
public final class KitType {

    private static final String MONGO_COLLECTION_NAME = "kitTypeMeta";
    @Getter private static final List<KitType> allTypes = new ArrayList<>();

    static {
        MongoCollection<Document> collection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);

        collection.find().iterator().forEachRemaining(doc -> {
            allTypes.add(qLib.PLAIN_GSON.fromJson(doc.toJson(), KitType.class));
        });

        /*new KitType("HCTEAMS", "HCTeams", ChatColor.GREEN, Material.DIAMOND_SWORD);
        new KitType("ARCHER", "Archer", ChatColor.BLUE, Material.BOW);
        new KitType("SOUP", "Soup", ChatColor.DARK_AQUA, Material.MUSHROOM_SOUP);
        new KitType("AXE", "Axe", ChatColor.RED, Material.IRON_AXE);
        new KitType("NO_ENCHANTS", "No Ench", ChatColor.AQUA, Material.ENCHANTMENT_TABLE);
        new KitType("GAPPLE", "Gapple", ChatColor.GOLD, Material.GOLDEN_APPLE, 1);
        new KitType("VANILLA", "Vanilla", ChatColor.WHITE, Material.POTION, 8225); // 8225 = regen 2 potion
        new KitType("CLASSIC", "Classic", ChatColor.AQUA, Material.DIAMOND_CHESTPLATE);
        new KitType("WIZARD", "Wizard", ChatColor.DARK_PURPLE, Material.BLAZE_POWDER);*/
    }

    /**
     * Id of this KitType, will be used when serializing the KitType for
     * database storage. Ex: "WIZARD", "NO_ENCHANTS", "SOUP"
     */
    @Getter @SerializedName("_id") private String id;

    /**
     * Display name of this KitType, will be used when communicating a KitType
     * to playerrs. Ex: "Wizard", "No Enchants", "Soup"
     */
    @Getter @Setter private String displayName;

    /**
     * Display color for this KitType, will be used in messages
     * or scoreboards sent to players.
     */
    @Getter @Setter private ChatColor displayColor;

    /**
     * Material info which will be used when rendering this
     * kit in selection menus and such.
     */
    @Getter @Setter private MaterialData icon;

    /**
     * Items which will be available for players to grab in the kit
     * editor, when making kits for this kit type.
     */
    @Getter @Setter private ItemStack[] editorItems = new ItemStack[0];

    /**
     * The armor that will be applied to players for this kit type.
     * Currently players are not allowed to edit their armor, they are
     * always given this armor.
     */
    @Getter @Setter private ItemStack[] defaultArmor = new ItemStack[0];

    /**
     * The default inventory that will be applied to players for this kit type.
     * Players are always allowed to rearange this inventory, so this only serves
     * as a default (in contrast to defaultArmor)
     */
    @Getter @Setter private ItemStack[] defaultInventory = new ItemStack[0];

    /**
     * Determines if players are allowed to spawn in items while editing their kits.
     * For some kit types (ex archer and axe) players can only rearange items in kits,
     * whereas some kit types (ex HCTeams and soup) allow spawning in items as well.
     */
    @Getter @Setter private boolean editorSpawnAllowed = true;

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

    public void saveAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            MongoCollection<Document> collection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
            Document kitTypeDoc = Document.parse(qLib.PLAIN_GSON.toJson(this));
            kitTypeDoc.remove("_id"); // upserts with an _id field is weird.

            Document query = new Document("_id", id);
            Document kitUpdate = new Document("$set", kitTypeDoc);

            collection.updateOne(query, kitUpdate, MongoUtils.UPSERT_OPTIONS);
        });
    }

}