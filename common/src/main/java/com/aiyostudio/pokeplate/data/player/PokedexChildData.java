package com.aiyostudio.pokeplate.data.player;

import com.aiyostudio.pokeplate.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Getter
public class PokedexChildData {
    private final Set<String> pokemon = new HashSet<>();
    @Setter
    private LocalDateTime unlockDate, gottenDate;
    @Setter
    private boolean gotten = false;

    public PokedexChildData() {
    }

    public PokedexChildData(ConfigurationSection section) {
        this.pokemon.addAll(section.getStringList("pokemon"));
        this.gotten = section.getBoolean("gotten");
        if (section.contains("unlock-date")) {
            this.unlockDate = DateUtil.toLocalDateTime(section.getLong("unlock-date"));
        }
        if (section.contains("gotten-date")) {
            this.gottenDate = DateUtil.toLocalDateTime(section.getLong("gotten-date"));
        }
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
        FileConfiguration data = new YamlConfiguration();
        data.set("pokemon", new ArrayList<>(pokemon));
        data.set("unlock-date", unlockDate == null ? null : DateUtil.toEpochMilli(unlockDate));
        data.set("gotten-date", gottenDate == null ? null : DateUtil.toEpochMilli(gottenDate));
        data.set("gotten", gotten);
        return data;
    }
}
