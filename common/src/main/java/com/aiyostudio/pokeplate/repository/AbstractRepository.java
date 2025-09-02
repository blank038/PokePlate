package com.aiyostudio.pokeplate.repository;

import com.aiyostudio.pokeplate.data.player.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractRepository implements IRepository {
    protected final Map<String, PlayerData> PLAYER_DATA_MAP = new HashMap<>();

    @Override
    public Optional<PlayerData> getPlayerDataFromMemory(String name) {
        return Optional.ofNullable(PLAYER_DATA_MAP.get(name));
    }

    @Override
    public void saveAll() {
        PLAYER_DATA_MAP.forEach((k, v) -> save(v, true));
    }
}