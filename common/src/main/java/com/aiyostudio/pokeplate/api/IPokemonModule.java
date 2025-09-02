package com.aiyostudio.pokeplate.api;

import com.aiyostudio.pokeplate.api.impl.IPokemonWrapper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Blank038
 */
public interface IPokemonModule {

    IPokemonWrapper getPokemon(int slot);

    boolean hasSpecies(String pokemonName);

    boolean give(CommandSender sender, Player player, String[] params);

    String getPokemonNameBySpeciesValue(String speciesValue);
    
    List<String> getPokemonListByStar(int star);
}
