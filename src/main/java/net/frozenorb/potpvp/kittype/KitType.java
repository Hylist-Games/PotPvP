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
 * A KitType is the highest level type of kit possible, not including
 * data about if debuffs are allowed. See {@link net.frozenorb.potpvp.kittype} for
 * an explanation of KitType and DetailedKitType.
 */
public enum KitType {

    HCTEAMS(
        "HCTeams",
        ChatColor.GREEN,
        Material.DIAMOND_SWORD,
        true
    ),
    ARCHER(
        "Archer",
        ChatColor.GRAY,
        Material.BOW,
        false
    ),
    AXE(
        "Axe",
        ChatColor.RED,
        Material.IRON_AXE,
        false
    ),
    NO_ENCHANTS(
        "No Enchants",
        ChatColor.AQUA,
        Material.ENCHANTMENT_TABLE,
        true
    ),
    GAPPLE(
        "Gapple",
        ChatColor.GOLD,
        Material.GOLDEN_APPLE,
        false
    ),
    SOUP(
        "Soup",
        ChatColor.DARK_AQUA,
        Material.MUSHROOM_SOUP,
        false
    ),
    VANILLA(
        "Vanilla",
        ChatColor.WHITE,
        new MaterialData(Material.POTION, (byte) 8225), // 8225 = regen 2 potion
        true
    ),
    CLASSIC(
        "Classic",
        ChatColor.AQUA,
        Material.DIAMOND_CHESTPLATE,
        false
    ),
    WIZARD(
        "Wizard",
        ChatColor.DARK_PURPLE,
        Material.BLAZE_POWDER,
        false
    );

    private static final String META_MONGO_COLLECTION_NAME = "KitTypeMeta";

    /**
     * Name of this KitType, only used to display to the player (internal uses use enum's .name() method)
     */
    @Getter private final String name;

    /**
     * Display color for this KitType, only used to display to the name. Display colors are not unique
     * to kit types, they can (and are) repeated.
     */
    @Getter private final ChatColor displayColor;

    /**
     * Icon to be used when rendering buttons for this KitType.
     * @see net.frozenorb.potpvp.kit.menu.button.selectdetailedkittype.DetailedKitTypeButton
     * @see net.frozenorb.potpvp.kit.menu.button.selectkittype.KitTypeButton
     */
    @Getter private final MaterialData icon;

    /**
     * If this KitType allows debuffs to be used
     * @see DetailedKitType#debuffSetting
     * @see DebuffSetting
     */
    @Getter private final boolean supportsDebuffs;

    /**
     * Metadata pertaining to this KitType. See javadocs for {@link KitTypeMeta}
     * @see KitType#saveMetaAsync()
     */
    @Getter private final KitTypeMeta meta;

    // convenience constructor to avoid execessive MaterialData constructors
    KitType(String name, ChatColor displayColor, Material iconType, boolean supportsDebuffs) {
        this(name, displayColor, new MaterialData(iconType), supportsDebuffs);
    }

    KitType(String name, ChatColor displayColor, MaterialData icon, boolean supportsDebuffs) {
        this.name = Preconditions.checkNotNull(name, "name");
        this.displayColor = Preconditions.checkNotNull(displayColor, "color");
        this.icon = Preconditions.checkNotNull(icon, "representation");
        this.supportsDebuffs = supportsDebuffs;

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
     * Convenience method to constructor a DetailedKitType, from this KitType, with debuffs.
     * If this KitType does not support debuffs this method and {@link KitType#withoutDebuffs}
     * with behave identically.
     * @return DetailedKitType representing this KitType with debuffs allowed
     */
    public DetailedKitType withDebuffs() {
        return DetailedKitType.of(this, DebuffSetting.ALLOWED);
    }

    /**
     * Convenience method to constructor a DetailedKitType, from this KitType, without debuffs.
     * If this KitType does not support debuffs this method and {@link KitType#withDebuffs}
     * with behave identically.
     * @return DetailedKitType representing this KitType without debuffs allowed
     */
    public DetailedKitType withoutDebuffs() {
        return DetailedKitType.of(this, DebuffSetting.DISALLOWED);
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