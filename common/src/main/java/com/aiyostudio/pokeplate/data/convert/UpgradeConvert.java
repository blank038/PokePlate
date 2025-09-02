package com.aiyostudio.pokeplate.data.convert;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.data.DataContainer;
import com.aiyostudio.pokeplate.data.plate.PlateData;
import com.aystudio.core.common.util.FileUtils;
import de.tr7zw.nbtapi.utils.MinecraftVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class UpgradeConvert {

    public void check() throws IOException {
        File folder = PokePlate.getInstance().getDataFolder();
        File file = new File(folder, "config.yml");
        if (!file.exists()) return;

        // Get resources path
        String version = MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_16_R1) ? "latest" : "legacy";
        String resPath = "view/" + version;

        FileConfiguration config = PokePlate.getInstance().getConfig();
        if (config.contains("Stars") && config.contains("Info")) {
            // Backup old data
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
            Files.createDirectories(Paths.get("./ayDataStorage", "PokePlate"));
            FileUtils.copyFile(folder, new File("./ayDataStorage/PokePlate", date));
            // Convert plate data
            ConfigurationSection starSection = config.getConfigurationSection("Stars"),
                    infoSection = config.getConfigurationSection("Info");
            for (String key : starSection.getKeys(false)) {
                if (infoSection.contains(key)) {
                    int star = Integer.parseInt(key.substring(0, key.length() - 1));
                    PokePlate.getInstance().saveResource("plate/example.yml", "plate/" + key + ".yml", false, (i) -> {
                        FileConfiguration data = YamlConfiguration.loadConfiguration(i);
                        data.set("view-id", key);
                        data.set("require.permission", "none");
                        data.set("require.plate", new ArrayList<>());
                        data.set("require.unlock", false);
                        data.set("require.pokemon", PokePlate.getModule().getPokemonListByStar(star));
                        data.set("commands", starSection.getStringList(key));
                        data.set("variable.reward", infoSection.getStringList(key));
                        try {
                            data.save(i);
                        } catch (IOException e) {
                            PokePlate.getInstance().getLogger().log(Level.WARNING, e, () -> "Failed to convert " + key + " data.");
                        }
                    });
                    PokePlate.getInstance().saveResource(resPath + "/example.yml", "view/" + key + ".yml");
                }
            }
            // Reload plate data
            DataContainer.init();
            // Convert player data
            File playerFolder = new File(folder, "Data");
            for (File i : playerFolder.listFiles()) {
                File target = new File(folder + "/storage/", i.getName());
                FileConfiguration oldData = YamlConfiguration.loadConfiguration(i);
                FileConfiguration data = new YamlConfiguration();

                List<String> pokedex = oldData.getStringList("Pokedex"),
                        rewards = oldData.getStringList("Rewards");

                starSection.getKeys(false).forEach((plateKey) -> {
                    ConfigurationSection plateSection = new YamlConfiguration();
                    PlateData plateData = DataContainer.PLATE_DATA.get(plateKey);
                    List<String> starPokedex = pokedex.stream()
                            .filter((v) -> plateData.getRequire().getPokemon().contains(v))
                            .collect(Collectors.toList());
                    plateSection.set("pokedex", starPokedex);
                    plateSection.set("gotten", rewards.contains(plateKey));
                    data.set("plate." + plateKey, plateSection);
                });
                data.save(target);
            }
            // Remove old data
            file.delete();
            deleteDirectory(playerFolder);
            for (String i : new String[]{"gui.yml", "list.yml"}) {
                Files.deleteIfExists(Paths.get(folder.getPath(), i));
            }
            // Save default resource
            PokePlate.getInstance().saveResource("plate/example.yml", "plate/example.yml");
            PokePlate.getInstance().saveResource(resPath + "/select.yml", "view/select.yml");
            PokePlate.getInstance().saveResource(resPath + "/example.yml", "view/example.yml");

            PokePlate.getInstance().getLogger().info("Convert successed.");
        }
    }

    private void deleteDirectory(File directory) throws IOException {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
    }
}
