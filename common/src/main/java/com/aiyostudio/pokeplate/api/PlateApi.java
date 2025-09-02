package com.aiyostudio.pokeplate.api;

import com.aiyostudio.pokeplate.data.DataContainer;
import com.aiyostudio.pokeplate.data.plate.PlateData;
import com.aiyostudio.pokeplate.data.player.PlayerData;
import com.aiyostudio.pokeplate.data.player.PokedexChildData;
import com.aiyostudio.pokeplate.storage.StorageHandler;
import org.bukkit.entity.Player;

import java.util.Map;

public class PlateApi {

    public static void addPokedex(Player player, String pokemon) {
        if (player == null || !player.isOnline()) {
            return;
        }
        StorageHandler.getPlayerDataFromMemory(player.getName()).ifPresent((data) -> {
            for (Map.Entry<String, PlateData> entry : DataContainer.PLATE_DATA.entrySet()) {
                if (!checkPokedexRequires(player, entry.getKey())) {
                    continue;
                }
                data.getChild(entry.getKey()).addPokemon(pokemon);
            }
        });
    }

    public static boolean checkPokedexRequires(Player player, String pokedex) {
        if (player == null || !player.isOnline()) {
            return false;
        }
        PlateData plateData = DataContainer.PLATE_DATA.get(pokedex);
        if (plateData == null) {
            return false;
        }
        PlayerData playerData = StorageHandler.getPlayerDataFromMemory(player.getName()).orElse(null);
        if (playerData == null) {
            return false;
        }
        PlateData.Require require = plateData.getRequire();
        PokedexChildData childData = playerData.getChild(pokedex);
        String permission = require.getPermission();

        if (!permission.equalsIgnoreCase("none") && !player.hasPermission(permission)) {
            return false;
        }
        if (childData.getPokemon().size() != require.getPokemon().size()) {
            return false;
        }

        return require.getPlate().stream().allMatch((v) -> playerData.getChild(v).isGotten());
    }
}
