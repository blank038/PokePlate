package com.aiyostudio.pokeplate.storage;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.data.player.PlayerData;
import com.aiyostudio.pokeplate.repository.AbstractRepository;
import com.aiyostudio.pokeplate.repository.impl.MySQLRepositoryImpl;
import com.aiyostudio.pokeplate.repository.impl.YamlRepositoryImpl;

import java.util.Optional;

public class StorageHandler {
    private static AbstractRepository repository;

    public static void initRepository() {
        String type = PokePlate.getInstance().getConfig().getString("data-option.type", "yaml");
        switch (type.toLowerCase()) {
            case "mysql":
                repository = new MySQLRepositoryImpl();
                break;
            case "yaml":
            default:
                repository = new YamlRepositoryImpl();
                break;
        }
    }

    public static void loadPlayerData(String playerName) {
        repository.loadPlayerData(playerName);
    }

    public static void unloadAndSave(String playerName) {
        repository.unloadPlayerData(playerName);
    }

    public static void save(PlayerData playerData, boolean locked) {
        repository.save(playerData, locked);
    }

    public static void saveAll() {
        repository.saveAll();
    }

    public static Optional<PlayerData> getPlayerDataFromMemory(String playerName) {
        return repository.getPlayerDataFromMemory(playerName);
    }

    public static Optional<PlayerData> getPlayerData(String playerName) {
        return repository.getPlayerData(playerName);
    }

    public static boolean hasPlayerData(String playerName) {
        return repository.getPlayerDataFromMemory(playerName).isPresent();
    }
}