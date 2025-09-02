package com.aiyostudio.pokeplate.api;

import com.aiyostudio.pokeplate.api.event.PlateCompleteEvent;
import com.aiyostudio.pokeplate.api.event.PlateUnlockEvent;
import com.aiyostudio.pokeplate.data.DataContainer;
import com.aiyostudio.pokeplate.data.plate.PlateData;
import com.aiyostudio.pokeplate.data.player.PlayerData;
import com.aiyostudio.pokeplate.data.player.PokedexChildData;
import com.aiyostudio.pokeplate.storage.StorageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlateApi {

    public static void addPokedex(Player player, String pokemon) {
        if (player == null || !player.isOnline()) {
            return;
        }
        StorageHandler.getPlayerDataFromMemory(player.getName()).ifPresent((data) -> {
            for (Map.Entry<String, PlateData> entry : DataContainer.PLATE_DATA.entrySet()) {
                if (!canView(player, entry.getKey())) {
                    continue;
                }
                List<String> pokemonList = entry.getValue().getRequire().getPokemon();
                if (!pokemonList.contains(pokemon)) {
                    continue;
                }
                PokedexChildData childData = data.getChild(entry.getKey());
                if (childData.hasPokemon(pokemon)) {
                    continue;
                }
                PlateUnlockEvent unlockEvent = new PlateUnlockEvent(player, entry.getKey(), pokemon);
                Bukkit.getPluginManager().callEvent(unlockEvent);
                if (unlockEvent.isCancelled()) {
                    continue;
                }
                childData.addPokemon(pokemon);

                // Check unlock date
                if (childData.getUnlockDate() != null) {
                    continue;
                }
                List<String> childPokemonList = childData.getPokemon().stream()
                        .filter(pokemonList::contains)
                        .collect(Collectors.toList());
                if (childPokemonList.size() == pokemonList.size()) {
                    PlateCompleteEvent event = new PlateCompleteEvent(player, entry.getKey());
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        continue;
                    }
                    childData.setUnlockDate(LocalDateTime.now());
                }
            }
        });
    }

    public static boolean canView(Player player, String pokedex) {
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
        String permission = require.getPermission();

        if (!permission.equalsIgnoreCase("none") && !player.hasPermission(permission)) {
            return false;
        }
        return require.getPlate().stream().noneMatch((v) -> playerData.getChild(v).getUnlockDate() == null);
    }

    public static boolean checkPokedexRequires(Player player, String pokedex) {
        return canView(player, pokedex) && isCompleted(player, pokedex);
    }

    public static boolean isCompleted(Player player, String pokedex) {
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
        PokedexChildData childData = playerData.getChild(pokedex);
        List<String> childPokemonList = childData.getPokemon().stream()
                .filter((v) -> plateData.getRequire().getPokemon().contains(v))
                .collect(Collectors.toList());
        return plateData.getRequire().getPokemon().size() == childPokemonList.size();
    }
}
