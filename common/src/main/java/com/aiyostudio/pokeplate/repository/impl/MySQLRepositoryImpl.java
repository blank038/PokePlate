package com.aiyostudio.pokeplate.repository.impl;

import com.aiyostudio.pokeplate.data.player.PlayerData;
import com.aiyostudio.pokeplate.repository.AbstractRepository;

import java.util.Optional;

public class MySQLRepositoryImpl extends AbstractRepository {

    @Override
    public Optional<PlayerData> getPlayerData(String name) {
        return Optional.empty();
    }

    @Override
    public void loadPlayerData(String name) {

    }

    @Override
    public void unloadPlayerData(String name) {

    }

    @Override
    public void save(PlayerData playerData, boolean locked) {

    }
}
