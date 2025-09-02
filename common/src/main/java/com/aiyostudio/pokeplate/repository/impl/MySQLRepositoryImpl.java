package com.aiyostudio.pokeplate.repository.impl;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.data.player.PlayerData;
import com.aiyostudio.pokeplate.i18n.I18n;
import com.aiyostudio.pokeplate.repository.AbstractRepository;
import com.aystudio.core.bukkit.util.mysql.litesql.LiteSQLApi;
import com.aystudio.core.bukkit.util.mysql.litesql.wrapper.LiteWrapper;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Optional;

public class MySQLRepositoryImpl extends AbstractRepository {
    private final LiteWrapper<PlayerData> liteWrapper;

    public MySQLRepositoryImpl() {
        FileConfiguration config = PokePlate.getInstance().getConfig();
        liteWrapper = LiteSQLApi.create(
                PokePlate.getInstance(),
                PlayerData.class,
                config.getString("data-option.url"),
                config.getString("data-option.user"),
                config.getString("data-option.password"),
                "poke_plate"
        );
        liteWrapper.setPullNotify(config.getBoolean("data-option.pull-notify"));
        liteWrapper.setTimeout(config.getInt("data-option.time-out"));
        liteWrapper.setLoadingMessage(I18n.getStrAndHeader("pull-start"));
        liteWrapper.setSuccessMessage(I18n.getStrAndHeader("pull-end"));
        liteWrapper.getDao().setGetterFunc((name) -> getPlayerDataFromMemory(name).orElse(null));
        liteWrapper.setSaveFunc((v) -> PLAYER_DATA_MAP.remove(v.getName()));
        liteWrapper.setLoadedFunc((player, data) -> PLAYER_DATA_MAP.put(player.getName(), data));
    }

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
        liteWrapper.getDao().save(playerData, locked ? 0 : 1);
        playerData.setNewData(false);
    }
}
