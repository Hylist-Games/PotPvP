package net.frozenorb.potpvp.kittype;

import com.google.common.base.Preconditions;

import com.mongodb.client.MongoCollection;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.MongoUtils;
import net.frozenorb.qlib.qLib;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import lombok.Getter;

/**
 * Denotes a type of Kit, under which players can queue, edit kits,
 * have elo, etc.
 */
public enum KitType {

    HCTEAMS(
        "HCTeams",
        ChatColor.GREEN,
        Material.DIAMOND_SWORD
    ),
    ARCHER(
        "Archer",
        ChatColor.GRAY,
        Material.BOW
    ),
    AXE(
        "Axe",
        ChatColor.RED,
        Material.IRON_AXE
    ),
    NO_ENCHANTS(
        "No Enchants",
        ChatColor.AQUA,
        Material.ENCHANTMENT_TABLE
    ),
    GAPPLE(
        "Gapple",
        ChatColor.GOLD,
        Material.GOLDEN_APPLE
    ),
    SOUP(
        "Soup",
        ChatColor.DARK_AQUA,
        Material.MUSHROOM_SOUP
    ),
    VANILLA(
        "Vanilla",
        ChatColor.WHITE,
        new MaterialData(Material.POTION, (byte) 8225) // 8225 = regen 2 potion
    ),
    CLASSIC(
        "Classic",
        ChatColor.AQUA,
        Material.DIAMOND_CHESTPLATE
    ),
    WIZARD(
        "Wizard",
        ChatColor.DARK_PURPLE,
        Material.BLAZE_POWDER
    );

    private static final String META_MONGO_COLLECTION_NAME = "KitTypeMeta";

    /**
     * Name of this KitType, only used to display to the player
     */
    @Getter private final String name;

    /**
     * Display color for this KitType, only used to display to the name. Display colors are not unique
     * to kit types, they can (and are) repeated.
     */
    @Getter private final ChatColor displayColor;

    /**
     * Icon to be used when rendering buttons for this KitType
     * @see net.frozenorb.potpvp.kittype.menu.KitTypeButton
     */
    @Getter private final MaterialData icon;

    /**
     * Metadata pertaining to this KitType. See javadocs for {@link KitTypeMeta}
     * @see KitType#saveMetaAsync()
     */
    @Getter private final KitTypeMeta meta;

    // convenience constructor to avoid execessive MaterialData constructors
    KitType(String name, ChatColor displayColor, Material iconType) {
        this(name, displayColor, new MaterialData(iconType));
    }

    KitType(String name, ChatColor displayColor, MaterialData icon) {
        this.name = Preconditions.checkNotNull(name, "name");
        this.displayColor = Preconditions.checkNotNull(displayColor, "color");
        this.icon = Preconditions.checkNotNull(icon, "representation");

        MongoCollection<Document> metaCollection = MongoUtils.getCollection(META_MONGO_COLLECTION_NAME);
        Document metaJson = metaCollection.find(buildQuery()).first();

        if (metaJson != null) {
            this.meta = qLib.GSON.fromJson(((Document) metaJson.get("meta")).toJson(), KitTypeMeta.class);
        } else {
            this.meta = new KitTypeMeta();
        }
    }

    /**
     * Returns a colored display name for this KitType, suitable to be displayed to players.
     * @return a colored display name for this KitType
     */
    public String getDisplayName() {
        return displayColor + name;
    }

    /**
     * Saves this KitType's meta to the database asynchronously. Will create document and save
     * if there is no existing document to update.
     */
    public void saveMetaAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            MongoCollection<Document> metaCollection = MongoUtils.getCollection(META_MONGO_COLLECTION_NAME);
            Document metaJson = Document.parse(qLib.PLAIN_GSON.toJson(meta));
            Document update = new Document("$set", new Document("meta", metaJson));

            metaCollection.updateOne(buildQuery(), update, MongoUtils.UPSERT_OPTIONS);
        });
    }

    private Document buildQuery() {
        return new Document("_id", name());
    }

}