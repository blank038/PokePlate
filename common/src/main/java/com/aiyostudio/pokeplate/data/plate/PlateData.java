package com.aiyostudio.pokeplate.data.plate;

import com.aiyostudio.pokeplate.util.TextUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PlateData {
    private final String viewId, displayName;
    private final Require require;
    private final List<String> commands = new ArrayList<>();
    private final List<String> rewardVariable = new ArrayList<>();
    private final DisplayItem displayItem, noPermsItem;

    public PlateData(FileConfiguration data) {
        this.viewId = data.getString("view-id");
        this.displayName = data.getString("display-name", "");
        this.require = new Require(data.getConfigurationSection("require"));
        this.commands.addAll(data.getStringList("commands"));
        this.rewardVariable.addAll(data.getStringList("variable.reward"));
        this.rewardVariable.replaceAll(TextUtil::colorify);
        this.displayItem = new DisplayItem(data.getConfigurationSection("display-item"));
        this.noPermsItem = new DisplayItem(data.getConfigurationSection("no-perms-item"));
    }

    @Getter
    public static class Require {
        private final String permission;
        private final List<String> plate = new ArrayList<>(),
                pokemon = new ArrayList<>();
        private final boolean unlock;

        public Require(ConfigurationSection section) {
            this.permission = section.getString("permission");
            this.unlock = section.getBoolean("unlock");
            this.plate.addAll(section.getStringList("plate"));
            this.pokemon.addAll(section.getStringList("pokemon"));
        }
    }

    @Getter
    public static class DisplayItem {
        private final Material type;
        private final String displayName;
        private final int data, customModelData, amount;
        private final List<String> lore = new ArrayList<>();

        public DisplayItem(ConfigurationSection section) {
            this.type = Material.valueOf(section.getString("type"));
            this.displayName = TextUtil.colorify(section.getString("name"));
            this.data = section.getInt("data");
            this.customModelData = section.getInt("customModelData");
            this.amount = section.getInt("amount");
            this.lore.addAll(section.getStringList("lore"));
        }
    }
}
