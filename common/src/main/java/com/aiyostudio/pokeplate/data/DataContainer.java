package com.aiyostudio.pokeplate.data;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.data.plate.PlateData;
import de.tr7zw.nbtapi.utils.MinecraftVersion;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Blank038
 */
public class DataContainer {
    public static final Map<String, ItemStack> ITEM_MAP = new HashMap<>();
    public static final Map<String, List<String>> SPECIES_MAP = new HashMap<>();
    public static final Map<String, PlateData> PLATE_DATA = new HashMap<>();
    public static DateTimeFormatter dateTimeFormatter;

    public static void init() {
        // Get resources path
        String version = MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_16_R1) ? "latest" : "legacy";
        String resPath = "view/" + version;

        // Load plate data
        PLATE_DATA.clear();
        File plateFolder = new File(PokePlate.getInstance().getDataFolder(), "plate");
        if (Files.notExists(plateFolder.toPath())) {
            PokePlate.getInstance().saveResource("plate/example.yml", "plate/example.yml");
        }
        for (File file : plateFolder.listFiles()) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            PlateData plateData = new PlateData(data);
            PLATE_DATA.put(file.getName().replace(".yml", ""), plateData);
        }

        // Check view folder
        File viewFolder = new File(PokePlate.getInstance().getDataFolder(), "view");
        if (Files.notExists(viewFolder.toPath())) {
            PokePlate.getInstance().saveResource(resPath + "/select.yml", "view/select.yml");
            PokePlate.getInstance().saveResource(resPath + "/example.yml", "view/example.yml");
        }

        // Initialize variable fields
        dateTimeFormatter = DateTimeFormatter.ofPattern(PokePlate.getInstance().getConfig().getString("date-format"));
    }
}
