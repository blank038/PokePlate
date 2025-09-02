package com.aiyostudio.pokeplate.repository.impl;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.data.player.PlayerData;
import com.aiyostudio.pokeplate.repository.AbstractRepository;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

public class YamlRepositoryImpl extends AbstractRepository {
    private final File storageFolder;

    public YamlRepositoryImpl() {
        this.storageFolder = new File(PokePlate.getInstance().getDataFolder(), "storage");
    }

    @Override
    public Optional<PlayerData> getPlayerData(String name) {
        PlayerData memoryData = this.getPlayerDataFromMemory(name).orElseGet(() -> {
            File file = new File(this.storageFolder, name + ".yml");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            return new PlayerData(name, data);
        });
        return Optional.of(memoryData);
    }

    @Override
    public void loadPlayerData(String name) {
        File file = new File(this.storageFolder, name + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        this.PLAYER_DATA_MAP.put(name, new PlayerData(name, data));
    }

    @Override
    public void unloadPlayerData(String name) {
        PlayerData playerData = PLAYER_DATA_MAP.remove(name);
        if (playerData != null) {
            this.save(playerData, false);
        }
    }

    @Override
    public void save(PlayerData playerData, boolean locked) {
        File file = new File(this.storageFolder, playerData.getOwner() + ".yml");
        try {
            playerData.toYaml().save(file);
        } catch (IOException e) {
            PokePlate.getInstance().getLogger().log(Level.WARNING, e, () -> "Failed to save player data: " + playerData.getOwner());
        }
    }
}
