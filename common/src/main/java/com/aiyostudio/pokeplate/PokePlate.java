package com.aiyostudio.pokeplate;

import com.aiyostudio.pokeplate.api.IPokemonModule;
import com.aiyostudio.pokeplate.command.PokePlateCommand;
import com.aiyostudio.pokeplate.data.DataContainer;
import com.aiyostudio.pokeplate.data.convert.UpgradeConvert;
import com.aiyostudio.pokeplate.hook.PlaceholderHook;
import com.aiyostudio.pokeplate.i18n.I18n;
import com.aiyostudio.pokeplate.listener.PlayerListener;
import com.aiyostudio.pokeplate.manager.StateManager;
import com.aiyostudio.pokeplate.storage.StorageHandler;
import com.aystudio.core.bukkit.platform.IPlatformApi;
import com.aystudio.core.bukkit.plugin.AyPlugin;
import de.tr7zw.nbtapi.utils.MinecraftVersion;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * @author Blank038
 */
public class PokePlate extends AyPlugin {
    @Getter
    private static PokePlate instance;
    @Getter
    private static IPokemonModule api;

    @Override
    public void onEnable() {
        instance = this;

        this.initializeApi();
        this.initializeHooks();

        try {
            new UpgradeConvert().check();
        } catch (IOException e) {
            this.getLogger().log(Level.WARNING, e, () -> "Failed to backup data, plugin disabled.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.loadConfig();

        StorageHandler.initRepository();
        this.getCommand("tj").setExecutor(new PokePlateCommand());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        StorageHandler.saveAll();
    }

    private void initializeHooks() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderHook().register();
        }
        if (Bukkit.getPluginManager().getPlugin("PokeStar") != null) {
            StateManager.pokeStar = true;
        }
    }

    private void initializeApi() {
        String apiClass = null;
        switch (MinecraftVersion.getVersion()) {
            case MC1_12_R1:
                apiClass = "com.aiyostudio.pokeplate.module.v1_12.PokemonModuleImpl";
                break;
            case MC1_16_R3:
                apiClass = "com.aiyostudio.pokeplate.module.v1_16.PokemonModuleImpl";
                break;
            default:
                break;
        }
        if (apiClass == null) {
            this.getLogger().info("Unsupported version: " + MinecraftVersion.getVersion().name());
            return;
        }
        this.getLogger().info("Loaded module: " + apiClass);
        try {
            Class<? extends IPlatformApi> classes = (Class<? extends IPlatformApi>) Class.forName(apiClass);
            api = (IPokemonModule) classes.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            this.getLogger().log(Level.WARNING, e, () -> "Failed to initialize api class.");
        }
    }

    public void loadConfig() {
        this.saveDefaultConfig();
        this.reloadConfig();

        new File(getDataFolder(), "storage").mkdir();
        new I18n(this.getConfig().getString("language", "zh_CN"));

        DataContainer.init();
    }
}