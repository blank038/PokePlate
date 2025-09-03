package com.aiyostudio.pokeplate.module.v1_16;

import com.aiyostudio.pokeplate.api.PlateApi;
import com.aiyostudio.pokeplate.api.impl.IPokemonWrapper;
import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.api.IPokemonModule;
import com.aiyostudio.pokeplate.data.DataContainer;
import com.aiyostudio.pokeplate.i18n.I18n;
import com.aiyostudio.pokeplate.manager.StateManager;
import com.aiyostudio.pokeplate.module.v1_16.impl.PokemonWrapperImpl;
import com.aiyostudio.pokeplate.module.v1_16.listen.ForgeListener;
import com.aystudio.core.pixelmon.PokemonAPI;
import com.aystudio.core.pixelmon.api.pokemon.PokemonUtil;
import com.google.common.collect.Lists;
import com.mc9y.pokestar.Main;
import com.pixelmonmod.api.registry.RegistryValue;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

/**
 * @author Blank038
 */
public class PokemonModuleImpl implements IPokemonModule {

    public PokemonModuleImpl() {
        for (Species species : PixelmonSpecies.getAll()) {
            Pokemon pokemon = PokemonFactory.create(species);
            String key = String.valueOf(com.mc9y.pokestar.Main.getPokeStarAPI().getPokemonStar(species.getName()));
            DataContainer.PHOTO_ITEMS.put(species.getName(), PokemonAPI.getInstance().getSpriteHelper().getSpriteItem(pokemon));
            DataContainer.SPECIES_MAP.putIfAbsent(key, Lists.newArrayList(species.getName()));
            DataContainer.SPECIES_MAP.get(key).add(species.getName());
        }
        Bukkit.getPluginManager().registerEvents(new ForgeListener(), PokePlate.getInstance());
    }

    @Override
    public IPokemonWrapper getPokemon(Player player, int slot) {
        Pokemon pokemon = StorageProxy.getParty(player.getUniqueId()).get(slot);
        if (pokemon == null || pokemon.isEgg()) {
            return null;
        }
        return new PokemonWrapperImpl(pokemon);
    }

    @Override
    public boolean hasSpecies(String pokemonName) {
        return PixelmonSpecies.fromName(pokemonName).getValue().isPresent();
    }

    @Override
    public boolean give(CommandSender sender, Player player, String[] params) {
        Species specie;
        try {
            specie = PixelmonSpecies.get(params[2]).orElse(null).getValueUnsafe();
        } catch (Exception e) {
            sender.sendMessage(I18n.getStrAndHeader("error"));
            return false;
        }
        if (specie == null) {
            return false;
        }
        if (params.length > 3 && "true".equalsIgnoreCase(params[3])) {
            Pokemon pokemon = PokemonFactory.create(specie);
            StorageProxy.getParty(player.getUniqueId()).add(pokemon);
        } else {
            PlateApi.addPokedex(player, specie.getName());
        }
        return true;
    }

    @Override
    public String getTranslationName(String speciesValue) {
        Optional<RegistryValue<Species>> species = PixelmonSpecies.get(speciesValue);
        return species.map(speciesRegistryValue -> PokemonUtil.getPokemonName(speciesRegistryValue.getValueUnsafe())).orElse(null);
    }

    @Override
    public List<String> getPokemonListByStar(int star) {
        if (StateManager.pokeStar) {
            return Main.getPokeStarAPI().getStarPokemon(star);
        }
        return Lists.newArrayList();
    }
}
