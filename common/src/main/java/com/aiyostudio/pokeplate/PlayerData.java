package com.aiyostudio.pokeplate;

import com.aiyostudio.pokeplate.data.DataContainer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class PlayerData {
    private final String owner;
    private final List<String> rewards, pokedex;

    public PlayerData(Player p) {
        this.owner = p.getName();

        File file = new File(PokePlate.getInstance().getDataFolder() + "/Data/", this.owner + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        this.rewards = data.getStringList("Rewards");
        this.pokedex = data.getStringList("Pokedex");
    }

    public boolean hasPokedex(String pokemon) {
        return pokedex.contains(pokemon);
    }

    public void addPokedex(String name) {
        pokedex.add(name);
    }

    public void addReward(String reward) {
        rewards.add(reward);
    }

    public boolean hasReward(String reward) {
        return rewards.contains(reward);
    }

    public int getCount() {
        return pokedex.size();
    }

    public int getProgress() {
        return (int) (1d * pokedex.size() / DataContainer.pokedexCount);
    }

    public void save() {
        FileConfiguration data = new YamlConfiguration();
        data.set("Rewards", rewards);
        data.set("Pokedex", pokedex);
        try {
            data.save(new File(PokePlate.getInstance().getDataFolder() + "/Data/", this.owner + ".yml"));
        } catch (IOException e) {
            PokePlate.getInstance().getLogger().log(Level.WARNING, e, () -> "无法保存玩家数据: " + this.owner);
        }
    }
}