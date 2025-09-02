package com.aiyostudio.pokeplate.view;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.data.DataContainer;
import com.aiyostudio.pokeplate.data.plate.PlateData;
import com.aiyostudio.pokeplate.data.player.PlayerData;
import com.aiyostudio.pokeplate.data.player.PokedexChildData;
import com.aiyostudio.pokeplate.i18n.I18n;
import com.aiyostudio.pokeplate.storage.StorageHandler;
import com.aystudio.core.bukkit.util.common.CommonUtil;
import com.aystudio.core.bukkit.util.common.TextUtil;
import com.aystudio.core.bukkit.util.inventory.GuiModel;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.utils.MinecraftVersion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Blank038
 */
public class SelectView {

    public static void open(Player player) {
        PlayerData playerData = StorageHandler.getPlayerDataFromMemory(player.getName()).orElse(null);
        if (playerData == null) {
            return;
        }

        String version = MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_16_R1) ? "latest" : "legacy";
        String resPath = "view/" + version + "/select.yml";

        PokePlate.getInstance().saveResource(resPath, "view/select.yml", false, (file) -> {
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);

            GuiModel model = new GuiModel(data.getString("title", ""), data.getInt("size"));
            model.registerListener(PokePlate.getInstance());
            model.setCloseRemove(true);

            if (data.contains("items")) {
                for (String key : data.getConfigurationSection("items").getKeys(false)) {
                    ConfigurationSection section = data.getConfigurationSection("items." + key);
                    ItemStack itemStack = new ItemStack(Material.valueOf(section.getString("type")), section.getInt("amount"));
                    ItemMeta meta = itemStack.getItemMeta();
                    if (section.contains("data")) {
                        itemStack.setDurability((short) section.getInt("data"));
                    }
                    if (section.contains("customModelData")) {
                        meta.setCustomModelData(section.getInt("customModelData"));
                    }
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name")));
                    List<String> lore = section.getStringList("lore");
                    lore.replaceAll((s) -> ChatColor.translateAlternateColorCodes('&', s));
                    meta.setLore(lore);
                    itemStack.setItemMeta(meta);
                    for (int i : CommonUtil.formatSlots(section.getString("slot"))) {
                        model.setItem(i, itemStack);
                    }
                }
            }

            int slotIndex = 0;
            Integer[] plateSlots = CommonUtil.formatSlots(data.getString("plate-slots"));
            for (Map.Entry<String, PlateData> entry : DataContainer.PLATE_DATA.entrySet()) {
                if (slotIndex >= plateSlots.length) {
                    continue;
                }

                String k = entry.getKey();
                PlateData v = entry.getValue();
                PlateData.DisplayItem display = v.getDisplayItem();
                PokedexChildData childData = playerData.getChild(k);
                // Get variable values
                int size = v.getRequire().getPokemon().size(), current = childData.getCount();
                int progress = (int) ((double) current / size * 100);
                LocalDateTime gottenDate = childData.getGottenDate(), unlockDate = childData.getUnlockDate();
                String emptyDate = I18n.getOption("variable.empty-date");
                String gottenFormat = gottenDate == null ? emptyDate : gottenDate.format(DataContainer.dateTimeFormatter);
                String unlockFormat = unlockDate == null ? emptyDate : unlockDate.format(DataContainer.dateTimeFormatter);

                // Create display item
                ItemStack itemStack = new ItemStack(display.getType(), display.getAmount());
                ItemMeta itemMeta = itemStack.getItemMeta();

                if (display.getData() > 0) {
                    itemStack.setDurability((short) display.getData());
                }
                if (display.getCustomModelData() > 0) {
                    itemMeta.setCustomModelData(display.getCustomModelData());
                }
                itemMeta.setDisplayName(TextUtil.formatHexColor(display.getDisplayName()));
                List<String> lore = new ArrayList<>(display.getLore());
                lore.replaceAll((line) -> TextUtil.formatHexColor(line)
                        .replace("%progress%", String.valueOf(progress))
                        .replace("%unlock_date%", unlockFormat)
                        .replace("%gotten_date%", gottenFormat));
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);

                NBTItem nbtItem = new NBTItem(itemStack);
                nbtItem.setString("PlateId", k);
                model.setItem(plateSlots[slotIndex], nbtItem.getItem());

                slotIndex++;
            }

            model.onClick((e) -> {
                e.setCancelled(true);
                if (e.getClickedInventory() != e.getInventory()) {
                    return;
                }
                ItemStack itemStack = e.getCurrentItem();
                if (itemStack == null || itemStack.getType() == Material.AIR || itemStack.getType().name().endsWith("_AIR")) {
                    return;
                }
                NBTItem nbtItem = new NBTItem(itemStack);
                if (nbtItem.hasTag("PlateId")) {
                    Player clicker = (Player) e.getWhoClicked();
                    PokedexView.openGui(clicker, nbtItem.getString("PlateId"), 1);
                }
            });

            model.openInventory(player);
        });
    }
}
