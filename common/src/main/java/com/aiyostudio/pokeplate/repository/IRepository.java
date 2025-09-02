package com.aiyostudio.pokeplate.repository;

import com.aiyostudio.pokeplate.data.player.PlayerData;

import java.util.Optional;

public interface IRepository {

    /**
     * Fetch data from memory.
     */
    Optional<PlayerData> getPlayerDataFromMemory(String name);

    /**
     * Fetch data from memory first, if not available, retrieve it from storage.
     */
    Optional<PlayerData> getPlayerData(String name);

    /**
     * Fetch data from storage and cache it in momeory.
     */
    void loadPlayerData(String name);

    /**
     * Unload data and save.
     */
    void unloadPlayerData(String name);

    /**
     * Save data to storage.
     */
    void save(PlayerData playerData, boolean locked);

    /**
     * Save all memory data.
     */
    void saveAll();
}
