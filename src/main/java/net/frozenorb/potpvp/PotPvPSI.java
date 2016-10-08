package net.frozenorb.potpvp;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

public final class PotPvPSI extends JavaPlugin {

    @Getter private static PotPvPSI instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

}