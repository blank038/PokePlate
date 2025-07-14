package com.aiyostudio.pokeplate.api;

import com.aiyostudio.pokeplate.PlayerData;
import com.aiyostudio.pokeplate.data.DataContainer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Blank038
 */
public interface IPokePlateApi<T> {

    boolean hasPokemon(String pokemonName);

    boolean hasPokemon(Player p, T es);

    boolean hasPokemon(Player player, String pokemon);

    boolean give(CommandSender sender, Player player, String[] params);

    String getStarShowName(int star);

    String getPokemonNameBySpeciesValue(String speciesValue);

    List<String> getPokemonListByStar(int star);

    default boolean allowGet(Player p, int star) {
        if (DataContainer.PLAYER_DATA_MAP.get(p.getName()).hasReward(star + "s")) {
            return false;
        }
        String key = String.valueOf(star);
        if (!DataContainer.SPECIES_MAP.containsKey(key)) {
            return false;
        }
        PlayerData playerData = DataContainer.PLAYER_DATA_MAP.get(p.getName());
        return DataContainer.SPECIES_MAP.get(key).stream()
                .allMatch(playerData::hasPokedex);
    }
}
