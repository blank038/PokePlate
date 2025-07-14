package com.aiyostudio.pokeplate.gui;

import com.aiyostudio.pokeplate.PokePlate;
import com.aystudio.core.bukkit.util.common.CommonUtil;
import com.aystudio.core.bukkit.util.inventory.GuiModel;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.List;

/**
 * @author Blank038
 */
public class MainGui {

    public MainGui(Player player) {
        // 创建界面
        File file = new File(PokePlate.getInstance().getDataFolder(), "gui.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        GuiModel model = new GuiModel(data.getString("title"), data.getInt("size"));
        model.registerListener(PokePlate.getInstance());
        model.setCloseRemove(true);
        // 开始设置物品
        if (data.contains("items")) {
            for (String key : data.getConfigurationSection("items").getKeys(false)) {
                ConfigurationSection section = data.getConfigurationSection("items." + key);
                ItemStack itemStack = new ItemStack(Material.valueOf(section.getString("type")),
                        section.getInt("amount"), (short) section.getInt("data"));
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name")));
                List<String> lore = section.getStringList("lore");
                lore.replaceAll((s) -> ChatColor.translateAlternateColorCodes('&', s));
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
                // 判断是否有 action
                if (section.contains("star")) {
                    NBTItem nbtItem = new NBTItem(itemStack);
                    nbtItem.setInteger("StarList", section.getInt("star"));
                    itemStack = nbtItem.getItem();
                }
                for (int i : CommonUtil.formatSlots(section.getString("slot"))) {
                    model.setItem(i, itemStack);
                }
            }
        }
        model.onClick((e) -> {
            e.setCancelled(true);
            if (e.getClickedInventory() == e.getInventory()) {
                ItemStack itemStack = e.getCurrentItem();
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    return;
                }
                NBTItem nbtItem = new NBTItem(itemStack);
                if (nbtItem.hasTag("StarList")) {
                    int star = nbtItem.getInteger("StarList");
                    Player clicker = (Player) e.getWhoClicked();
                    ListGui.openGui(clicker, star, 1);
                }
            }
        });
        model.openInventory(player);
    }
}
