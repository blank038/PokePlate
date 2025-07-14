package com.aiyostudio.pokeplate;

import com.aiyostudio.pokeplate.api.IPokePlateApi;
import com.aiyostudio.pokeplate.command.PokePlateCommand;
import com.aiyostudio.pokeplate.data.DataContainer;
import com.aiyostudio.pokeplate.hook.PlaceholderHook;
import com.aiyostudio.pokeplate.i18n.I18n;
import com.aiyostudio.pokeplate.listener.PlayerListener;
import com.aystudio.core.bukkit.platform.IPlatformApi;
import com.aystudio.core.bukkit.plugin.AyPlugin;
import de.tr7zw.nbtapi.utils.MinecraftVersion;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * @author Blank038
 */
public class PokePlate extends AyPlugin {
    @Getter
    private static PokePlate instance;
    @Getter
    private static IPokePlateApi api;

    @Override
    public void onEnable() {
        instance = this;
        this.loadConfig();
        for (Player p : Bukkit.getOnlinePlayers()) {
            DataContainer.PLAYER_DATA_MAP.put(p.getName(), new PlayerData(p));
        }
        this.getCommand("tj").setExecutor(new PokePlateCommand());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        // 挂钩 PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderHook().register();
        }
        this.initializeApi();
    }

    private void initializeApi() {
        String apiClass;
        if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_16_R3)) {
            apiClass = "com.aiyostudio.pokeplate.module.v1_16.PokePlateApiImpl";
        } else {
            apiClass = "com.aiyostudio.pokeplate.module.v1_12.PokePlateApiImpl";
        }
        this.getLogger().info("加载接口: " + apiClass);
        try {
            Class<? extends IPlatformApi> classes = (Class<? extends IPlatformApi>) Class.forName(apiClass);
            api = (IPokePlateApi) classes.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            this.getLogger().warning("Failed to initialize api class.");
        }
    }

    public void loadConfig() {
        this.saveDefaultConfig();
        this.reloadConfig();

        new File(getDataFolder(), "Data").mkdir();
        for (String name : new String[]{"gui.yml", "list.yml"}) {
            this.saveResource(name, name);
        }

        new I18n(this.getConfig().getString("language", "zh_CN"));
    }

    @Override
    public void onDisable() {
        for (PlayerData pd : DataContainer.PLAYER_DATA_MAP.values()) {
            pd.save();
        }
    }

    public void giveReward(Player p, int star) {
        PlayerData playerData = DataContainer.PLAYER_DATA_MAP.get(p.getName());
        if (playerData != null) {
            playerData.addReward(star + "s");
            boolean isOp = p.isOp();
            try {
                p.setOp(true);
                for (String command : getConfig().getStringList("Stars." + star + "s")) {
                    p.performCommand(command.replace("%player%", p.getName()));
                }
            } finally {
                p.setOp(isOp);
            }
        }
    }
}