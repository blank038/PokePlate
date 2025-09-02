package com.aiyostudio.pokeplate.data.player;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
public class PokedexChildData {
    private final Set<String> pokemon = new HashSet<>();
    private LocalDateTime unlockDate, gottenDate;
    private boolean gotten = false;

    public PokedexChildData() {
    }

    public PokedexChildData(ConfigurationSection section) {

    }

    public int getCount() {
        return this.pokemon.size();
    }

    public void addPokemon(String pokemon) {
        this.pokemon.add(pokemon);
    }

    public boolean hasPokemon(String pokemon) {
        return this.pokemon.contains(pokemon);
    }

    public ConfigurationSection toSection() {
        return new YamlConfiguration();
    }
}
