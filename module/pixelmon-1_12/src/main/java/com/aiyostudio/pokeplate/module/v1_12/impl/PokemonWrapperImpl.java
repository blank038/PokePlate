package com.aiyostudio.pokeplate.module.v1_12.impl;

import com.aiyostudio.pokeplate.api.impl.IPokemonWrapper;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

public class PokemonWrapperImpl implements IPokemonWrapper {
    private final Pokemon pokemon;

    public PokemonWrapperImpl(Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    @Override
    public String getSpecies() {
        return "";
    }

    @Override
    public String getOriginTrainer() {
        return "";
    }
}
