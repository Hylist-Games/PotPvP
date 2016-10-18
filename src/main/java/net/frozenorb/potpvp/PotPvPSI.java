package net.frozenorb.potpvp;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.kit.KitHandler;
import net.frozenorb.potpvp.listener.BasicPreventionListener;
import net.frozenorb.potpvp.listener.ChatListener;
import net.frozenorb.potpvp.listener.PearlCooldownListener;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.nametag.PotPvPNametagProvider;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.scoreboard.PotPvPScoreboardConfiguration;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.tab.PotPvPLayoutProvider;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.scoreboard.FrozenScoreboardHandler;
import net.frozenorb.qlib.tab.FrozenTabHandler;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class PotPvPSI extends JavaPlugin {

    @Getter private static PotPvPSI instance;

    private MongoClient mongoClient;
    @Getter private MongoDatabase mongoDatabase;

    @Getter private JedisPool redisConnection;

    @Getter private SettingHandler settingHandler;
    @Getter private KitHandler kitHandler;
    @Getter private LobbyHandler lobbyHandler;
    @Getter private ArenaHandler arenaHandler;
    @Getter private MatchHandler matchHandler;
    @Getter private PartyHandler partyHandler;
    @Getter private QueueHandler queueHandler;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        setupMongo();
        setupRedis();

        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setTime(12_000L);
        }

        settingHandler = new SettingHandler();
        kitHandler = new KitHandler();
        lobbyHandler = new LobbyHandler();
        arenaHandler = new ArenaHandler();
        matchHandler = new MatchHandler();
        partyHandler = new PartyHandler();
        queueHandler = new QueueHandler();

        getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new PearlCooldownListener(), this);

        FrozenCommandHandler.registerAll(this);
        FrozenTabHandler.setLayoutProvider(new PotPvPLayoutProvider());
        FrozenNametagHandler.registerProvider(new PotPvPNametagProvider());
        FrozenScoreboardHandler.setConfiguration(PotPvPScoreboardConfiguration.create());
    }

    @Override
    public void onDisable() {
        instance = null;

        mongoClient.close();
        redisConnection.close();
    }

    private void setupMongo() {
        mongoClient = new MongoClient(
            getConfig().getString("Mongo.Host"),
            getConfig().getInt("Mongo.Port")
        );

        String databaseId = getConfig().getString("Mongo.Database");
        mongoDatabase = mongoClient.getDatabase(databaseId);
    }

    private void setupRedis() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        // Don't block any thread when pool is exhausted
        jedisPoolConfig.setBlockWhenExhausted(false);

        redisConnection = new JedisPool(
            jedisPoolConfig,
            getConfig().getString("Redis.Host"),
            getConfig().getInt("Redis.Port")
        );
    }

}