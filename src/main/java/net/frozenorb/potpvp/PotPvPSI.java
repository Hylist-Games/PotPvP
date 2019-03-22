package net.frozenorb.potpvp;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.qrakn.morpheus.Morpheus;
import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameListeners;
import com.qrakn.morpheus.game.GameQueue;
import com.qrakn.morpheus.game.event.GameEvent;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.hydrogen.Settings;
import net.frozenorb.potpvp.morpheus.EventListeners;
import net.frozenorb.potpvp.morpheus.EventTask;
import net.frozenorb.potpvp.pvpclasses.PvPClassHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.TypeAdapter;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.spigotmc.SpigotConfig;

import com.google.common.collect.ImmutableMap;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import lombok.Getter;
import net.frozenorb.chunksnapshot.ChunkSnapshot;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.punishment.Punishment;
import net.frozenorb.hydrogen.punishment.meta.PunishmentMeta;
import net.frozenorb.hydrogen.punishment.meta.PunishmentMetaFetcher;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.duel.DuelHandler;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.kit.KitHandler;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kittype.KitTypeJsonAdapter;
import net.frozenorb.potpvp.kittype.KitTypeParameterType;
import net.frozenorb.potpvp.listener.BasicPreventionListener;
import net.frozenorb.potpvp.listener.BowHealthListener;
import net.frozenorb.potpvp.listener.ChatFormatListener;
import net.frozenorb.potpvp.listener.ChatToggleListener;
import net.frozenorb.potpvp.listener.NightModeListener;
import net.frozenorb.potpvp.listener.PearlCooldownListener;
import net.frozenorb.potpvp.listener.RankedMatchQualificationListener;
import net.frozenorb.potpvp.listener.TabCompleteListener;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.nametag.PotPvPNametagProvider;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.postmatchinv.PostMatchInvHandler;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.rematch.RematchHandler;
import net.frozenorb.potpvp.scoreboard.PotPvPScoreboardConfiguration;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.statistics.StatisticsHandler;
import net.frozenorb.potpvp.tab.PotPvPLayoutProvider;
import net.frozenorb.potpvp.tournament.TournamentHandler;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.scoreboard.FrozenScoreboardHandler;
import net.frozenorb.qlib.serialization.BlockVectorAdapter;
import net.frozenorb.qlib.serialization.ItemStackAdapter;
import net.frozenorb.qlib.serialization.LocationAdapter;
import net.frozenorb.qlib.serialization.PotionEffectAdapter;
import net.frozenorb.qlib.serialization.VectorAdapter;
import net.frozenorb.qlib.tab.FrozenTabHandler;

public final class PotPvPSI extends JavaPlugin {

    private static PotPvPSI instance;
    @Getter private static Gson gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
        .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
        .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
        .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
        .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
        .registerTypeHierarchyAdapter(KitType.class, new KitTypeJsonAdapter()) // custom KitType serializer
        .registerTypeAdapter(ChunkSnapshot.class, new ChunkSnapshotAdapter())
        .serializeNulls()
        .create();

    private MongoClient mongoClient;
    @Getter private MongoDatabase mongoDatabase;

    @Getter private SettingHandler settingHandler;
    @Getter private DuelHandler duelHandler;
    @Getter private KitHandler kitHandler;
    @Getter private LobbyHandler lobbyHandler;
    private ArenaHandler arenaHandler;
    @Getter private MatchHandler matchHandler;
    @Getter private PartyHandler partyHandler;
    @Getter private QueueHandler queueHandler;
    @Getter private RematchHandler rematchHandler;
    @Getter private PostMatchInvHandler postMatchInvHandler;
    @Getter private FollowHandler followHandler;
    @Getter private EloHandler eloHandler;
    @Getter private TournamentHandler tournamentHandler;
    @Getter private PvPClassHandler pvpClassHandler;
    
    @Getter private ChatColor dominantColor = ChatColor.RED;

    @Override
    public void onEnable() {
        //SpigotConfig.onlyCustomTab = true; // because we'll definitely forget
        //this.dominantColor = ChatColor.DARK_PURPLE;
        instance = this;
        saveDefaultConfig();

        Settings.setClean(true);

        setupMongo();

        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setTime(6_000L);
        }

        AtomicInteger index = new AtomicInteger(0);
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            FancyMessage message = new FancyMessage("TIP: ").color(ChatColor.GOLD);

            if (index.get() == 0) {
                message.then("Don't like the server? Knockback sucks? ").color(ChatColor.GRAY)
                        .then("[Click Here]").color(ChatColor.GREEN).command("/showmethedoor").tooltip(ChatColor.GREEN + ":)");

                index.set(0);
            } else {
                message.then("Pots too slow? Learn to pot or disconnect!").color(ChatColor.GRAY);

                index.incrementAndGet();
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                message.send(player);
            }
        }, 5 * 60 * 20L, 5 * 60 * 20L);

        settingHandler = new SettingHandler();
        duelHandler = new DuelHandler();
        kitHandler = new KitHandler();
        lobbyHandler = new LobbyHandler();
        arenaHandler = new ArenaHandler();
        matchHandler = new MatchHandler();
        partyHandler = new PartyHandler();
        queueHandler = new QueueHandler();
        rematchHandler = new RematchHandler();
        postMatchInvHandler = new PostMatchInvHandler();
        followHandler = new FollowHandler();
        eloHandler = new EloHandler();
        pvpClassHandler = new PvPClassHandler();
        //tournamentHandler = new TournamentHandler();

        new Morpheus(this); // qrakn game events
        new EventTask().runTaskTimerAsynchronously(this, 1L, 1L);

        for (GameEvent event : GameEvent.getEvents()) {
            for (Listener listener : event.getListeners()) {
                getServer().getPluginManager().registerEvents(listener, this);
            }
        }

        getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
        getServer().getPluginManager().registerEvents(new BowHealthListener(), this);
        getServer().getPluginManager().registerEvents(new ChatFormatListener(), this);
        getServer().getPluginManager().registerEvents(new ChatToggleListener(), this);
        getServer().getPluginManager().registerEvents(new NightModeListener(), this);
        getServer().getPluginManager().registerEvents(new PearlCooldownListener(), this);
        getServer().getPluginManager().registerEvents(new RankedMatchQualificationListener(), this);
        getServer().getPluginManager().registerEvents(new TabCompleteListener(), this);
        getServer().getPluginManager().registerEvents(new StatisticsHandler(), this);
        getServer().getPluginManager().registerEvents(new GameListeners(), this);
        getServer().getPluginManager().registerEvents(new EventListeners(), this);

        FrozenCommandHandler.registerAll(this);
        FrozenCommandHandler.registerParameterType(KitType.class, new KitTypeParameterType());
        FrozenTabHandler.setLayoutProvider(new PotPvPLayoutProvider());
        FrozenNametagHandler.registerProvider(new PotPvPNametagProvider());
        FrozenScoreboardHandler.setConfiguration(PotPvPScoreboardConfiguration.create());

        Hydrogen.getInstance().getPunishmentHandler().registerMetaFetcher(new PunishmentMetaFetcher(PotPvPSI.getInstance()) {
            
            @Override
            public PunishmentMeta fetch(UUID target) {
                return PunishmentMeta.of(ImmutableMap.of());
            }

            @Override
            public PunishmentMeta fetch(UUID target, Punishment.PunishmentType type) {
                // now we reset elo :^)
                if (type == Punishment.PunishmentType.BLACKLIST || type == Punishment.PunishmentType.BAN) eloHandler.resetElo(target);
                return fetch(target);
            }
            
        });
    }

    @Override
    public void onDisable() {
        for (Match match : this.matchHandler.getHostedMatches()) {
            if (match.getKitType().isBuildingAllowed()) match.getArena().restore();
        }

        GameQueue.INSTANCE.getCurrentGames().forEach(Game::end);

        try {
            arenaHandler.saveSchematics();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String playerName : PvPClassHandler.getEquippedKits().keySet()) {
            PvPClassHandler.getEquippedKits().get(playerName).remove(getServer().getPlayerExact(playerName));
        }

        instance = null;
    }

    private void setupMongo() {
        mongoClient = new MongoClient(
            getConfig().getString("Mongo.Host"),
            getConfig().getInt("Mongo.Port")
        );

        String databaseId = getConfig().getString("Mongo.Database");
        mongoDatabase = mongoClient.getDatabase(databaseId);
    }

    // This is here because chunk snapshots are (still) being deserialized, and serialized sometimes.
    private static class ChunkSnapshotAdapter extends TypeAdapter<ChunkSnapshot> {

        @Override
        public ChunkSnapshot read(JsonReader arg0) throws IOException {
            return null;
        }

        @Override
        public void write(JsonWriter arg0, ChunkSnapshot arg1) throws IOException {
            
        }
        
    }

    public ArenaHandler getArenaHandler() {
        return arenaHandler;
    }

    public static PotPvPSI getInstance() {
        return instance;
    }
}