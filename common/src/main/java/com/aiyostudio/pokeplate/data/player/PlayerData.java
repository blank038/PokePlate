package com.aiyostudio.pokeplate.data.player;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PlayerData {
    private final String owner;
    private final Map<String, PokedexChildData> childs = new HashMap<>();

    public PlayerData(String owner, FileConfiguration data) {
        this.owner = owner;
        ConfigurationSection section = data.getConfigurationSection("plate");
        if (section != null) {
            section.getKeys(false).forEach((key) -> {
                ConfigurationSection sec = section.getConfigurationSection(key);
                childs.put(key, new PokedexChildData(sec));
            });
        }
    }

    public PokedexChildData getChild(String pokedex) {
        childs.putIfAbsent(pokedex, new PokedexChildData());
        return childs.get(pokedex);
    }

    public boolean isGotten(String pokedex) {
        return childs.containsKey(pokedex) && childs.get(pokedex).isGotten();
    }

    public boolean hasPokedex(String pokdex, String pokemon) {
        return childs.containsKey(pokdex) && childs.get(pokdex).hasPokemon(pokemon);
    }

    public FileConfiguration toConfiguration() {
        FileConfiguration data = new YamlConfiguration();
        ConfigurationSection plateSection = new YamlConfiguration();
        childs.forEach((k, v) -> plateSection.set(k, v.toSection()));
        data.set("plate", plateSection);
        return data;
    }
}