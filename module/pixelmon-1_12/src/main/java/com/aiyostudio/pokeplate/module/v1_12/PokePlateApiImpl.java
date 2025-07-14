package com.aiyostudio.pokeplate.module.v1_12;

import com.aiyostudio.pokeplate.PlayerData;
import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.api.IPokePlateApi;
import com.aiyostudio.pokeplate.data.DataContainer;
import com.aiyostudio.pokeplate.i18n.I18n;
import com.aiyostudio.pokeplate.module.v1_12.listen.ForgeListener;
import com.aystudio.core.pixelmon.PokemonAPI;
import com.aystudio.core.pixelmon.api.pokemon.PokemonUtil;
import com.google.common.collect.Lists;
import com.mc9y.pokestar.Main;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Blank038
 */
public class PokePlateApiImpl implements IPokePlateApi<EnumSpecies> {

    public PokePlateApiImpl() {
        for (EnumSpecies species : EnumSpecies.values()) {
            Pokemon pokemon = Pixelmon.pokemonFactory.create(species);
            String key = String.valueOf(com.mc9y.pokestar.Main.getPokeStarAPI().getPokemonStar(species.name()));
            DataContainer.ITEM_MAP.put(species.name(), PokemonAPI.getInstance().getSpriteHelper().getSpriteItem(pokemon));
            DataContainer.SPECIES_MAP.putIfAbsent(key, Lists.newArrayList(species.name));
            DataContainer.SPECIES_MAP.get(key).add(species.name());
        }
        Bukkit.getPluginManager().registerEvents(new ForgeListener(), PokePlate.getInstance());
    }

    @Override
    public boolean hasPokemon(String pokemonName) {
        try {
            EnumSpecies.valueOf(pokemonName);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean hasPokemon(Player p, EnumSpecies es) {
        PlayerData playerData = DataContainer.PLAYER_DATA_MAP.get(p.getName());
        return playerData != null && playerData.hasPokedex(es.name());
    }

    @Override
    public boolean hasPokemon(Player player, String pokemon) {
        PlayerData playerData = DataContainer.PLAYER_DATA_MAP.get(player.getName());
        return playerData != null && playerData.hasPokedex(pokemon);
    }

    @Override
    public boolean give(CommandSender sender, Player player, String[] params) {
        EnumSpecies specie;
        try {
            specie = EnumSpecies.valueOf(params[2]);
        } catch (Exception e) {
            sender.sendMessage(I18n.getStrAndHeader("error"));
            return false;
        }
        if (params.length > 3  && "true".equalsIgnoreCase(params[3])) {
            Pokemon pokemon = Pixelmon.pokemonFactory.create(specie);
            Pixelmon.storageManager.getParty(player.getUniqueId()).add(pokemon);
        } else if(!this.hasPokemon(player,specie)){
            DataContainer.PLAYER_DATA_MAP.get(player.getName()).addPokedex(specie.getPokemonName());
        }
        return true;
    }

    @Override
    public String getStarShowName(int star) {
        return Main.getPokeStarAPI().getPokeShowName(star);
    }

    @Override
    public String getPokemonNameBySpeciesValue(String speciesValue) {
        return PokemonUtil.getPokemonName(EnumSpecies.valueOf(speciesValue));
    }

    @Override
    public List<String> getPokemonListByStar(int star) {
        return Main.getPokeStarAPI().getStarPokemon(star);
    }
}
