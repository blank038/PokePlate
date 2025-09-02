package com.aiyostudio.pokeplate.view;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.api.PlateApi;
import com.aiyostudio.pokeplate.data.DataContainer;
import com.aiyostudio.pokeplate.data.plate.PlateData;
import com.aiyostudio.pokeplate.data.player.PlayerData;
import com.aiyostudio.pokeplate.data.player.PokedexChildData;
import com.aiyostudio.pokeplate.i18n.I18n;
import com.aiyostudio.pokeplate.storage.StorageHandler;
import com.aiyostudio.pokeplate.util.PlayerUtil;
import com.aiyostudio.pokeplate.util.TextUtil;
import com.aystudio.core.bukkit.util.common.CommonUtil;
import com.aystudio.core.bukkit.util.inventory.GuiModel;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Blank038
 */
public class PokedexView {

    public static void openGui(Player player, String pokedex, int page) {
        File file = new File(PokePlate.getInstance().getDataFolder() + "/view", pokedex + ".yml");
        if (Files.notExists(file.toPath())) {
            return;
        }
        PlateData plateData = DataContainer.PLATE_DATA.get(pokedex);
        if (plateData == null) {
            return;
        }
        PlayerData playerData = StorageHandler.getPlayerDataFromMemory(player.getName()).orElse(null);
        if (playerData == null) {
            return;
        }
        PokedexChildData childData = playerData.getChild(pokedex);

        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        GuiModel model = new GuiModel(data.getString("title"), data.getInt("size"));
        model.registerListener(PokePlate.getInstance());

        Integer[] slots = CommonUtil.formatSlots(data.getString("pokemon-slots"));
        int maximum = plateData.getRequire().getPokemon().size();
        int maxPage = Math.max(1, (maximum / slots.length) + (maximum % slots.length > 0 ? 1 : 0));

        if (data.contains("items")) {
            String state = I18n.getOption("state.lock");
            if (childData.isGotten()) {
                state = I18n.getOption("state.received");
            } else if (PlateApi.isCompleted(player, pokedex)) {
                state = I18n.getOption("state.available");
            }
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
                meta.setDisplayName(TextUtil.colorify(section.getString("name")));
                List<String> lore = new ArrayList<>();
                for (String line : section.getStringList("lore")) {
                    if (line.contains("%reward%")) {
                        lore.addAll(plateData.getRewardVariable());
                        continue;
                    }
                    lore.add(TextUtil.colorify(line).replace("%state%", state));
                }
                meta.setLore(lore);
                itemStack.setItemMeta(meta);

                if (section.contains("action")) {
                    NBTItem nbtItem = new NBTItem(itemStack);
                    nbtItem.setString("PokedexAction", section.getString("action"));
                    itemStack = nbtItem.getItem();
                }

                for (int i : CommonUtil.formatSlots(section.getString("slot"))) {
                    model.setItem(i, itemStack);
                }
            }
        }

        initializePokemonItems(data, model, page, childData, plateData);

        model.onClick((e) -> {
            e.setCancelled(true);
            if (e.getClickedInventory() != e.getInventory()) {
                return;
            }
            ItemStack itemStack = e.getCurrentItem();
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                return;
            }
            NBTItem nbtItem = new NBTItem(itemStack);
            if (nbtItem.hasTag("PokedexAction")) {
                String action = nbtItem.getString("PokedexAction");
                Player clicker = (Player) e.getWhoClicked();
                switch (action) {
                    case "up":
                        if (e.isRightClick()) {
                            SelectView.open(clicker);
                            break;
                        }
                        if (page == 1) {
                            clicker.sendMessage(I18n.getStrAndHeader("no-previous-page"));
                            break;
                        }
                        PokedexView.openGui(player, pokedex, page - 1);
                        break;
                    case "down":
                        if (page >= maxPage) {
                            clicker.sendMessage(I18n.getStrAndHeader("no-next-page"));
                            break;
                        }
                        PokedexView.openGui(clicker, pokedex, page + 1);
                        break;
                    case "reward":
                        if (PlateApi.isCompleted(clicker, pokedex)) {
                            tryReceive(clicker, pokedex);
                            PokedexView.openGui(clicker, pokedex, page);
                        } else {
                            clicker.sendMessage(I18n.getStrAndHeader("not-met-condition"));
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        model.openInventory(player);
    }

    private static void tryReceive(Player player, String pokedex) {
        PlayerData playerData = StorageHandler.getPlayerDataFromMemory(player.getName()).orElse(null);
        if (playerData == null) {
            player.sendMessage(I18n.getStrAndHeader("error"));
            return;
        }
        PlateData plateData = DataContainer.PLATE_DATA.get(pokedex);
        if (plateData == null) {
            player.sendMessage(I18n.getStrAndHeader("error"));
            return;
        }
        PokedexChildData childData = playerData.getChild(pokedex);
        if (childData.isGotten()) {
            player.sendMessage(I18n.getStrAndHeader("claimed"));
            return;
        }
        childData.setGotten(true);
        childData.setGottenDate(LocalDateTime.now());
        PlayerUtil.executeCommands(player, plateData.getCommands());
        player.sendMessage(I18n.getStrAndHeader("gotten").replace("%plate_name%", plateData.getDisplayName()));
    }

    private static void initializePokemonItems(FileConfiguration data, GuiModel model, int page, PokedexChildData childData, PlateData plateData) {
        ConfigurationSection section = data.getConfigurationSection("display-item");
        Integer[] slots = CommonUtil.formatSlots(data.getString("pokemon-slots"));
        List<String> requirePokemon = plateData.getRequire().getPokemon();

        int start = slots.length * (page - 1), end = Math.min(requirePokemon.size(), slots.length * page);

        List<String> pokemonList = requirePokemon.subList(start, end);
        String type = data.getString("display-type").toLowerCase();

        for (int i = 0, size = pokemonList.size(); i < slots.length && i < size; i++) {
            String species = pokemonList.get(i);
            ItemStack itemStack = getDisplayItem(section, type, species, childData.hasPokemon(species));
            model.setItem(slots[i], itemStack);
        }
    }

    private static ItemStack getDisplayItem(ConfigurationSection section, String type, String species, boolean contains) {
        ItemStack itemStack = null;
        switch (type) {
            case "normal":
                ConfigurationSection normalSection = section.getConfigurationSection(contains ? "unlock" : "lock");
                itemStack = buildItem(normalSection, species);
                break;
            case "enchantment":
                ConfigurationSection enchantSection = section.getConfigurationSection("photo");
                itemStack = buildPhoto(enchantSection, species, contains);
                break;
            case "mixture":
                ConfigurationSection mixtureSection = section.getConfigurationSection(contains ? "photo" : "lock");
                itemStack = contains ? buildPhoto(mixtureSection, species, false) : buildItem(mixtureSection, species);
                break;
            default:
                break;
        }
        return itemStack;
    }

    private static ItemStack buildItem(ConfigurationSection section, String species) {
        String speciesName = PokePlate.getModule().getTranslationName(species);
        Material material = Material.valueOf(section.getString("type"));
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if (section.contains("data")) {
            itemStack.setDurability((short) section.getInt("data"));
        }
        if (section.contains("customModelData")) {
            meta.setCustomModelData(section.getInt("customModelData"));
        }
        meta.setDisplayName(TextUtil.colorify(section.getString("name").replace("%pokemon_name%", speciesName)));
        List<String> lore = section.getStringList("lore");
        lore.replaceAll(TextUtil::colorify);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }


    private static ItemStack buildPhoto(ConfigurationSection section, String species, boolean enchantment) {
        String speciesName = PokePlate.getModule().getTranslationName(species);
        ItemStack itemStack = DataContainer.PHOTO_ITEMS.getOrDefault(species, new ItemStack(Material.STONE));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(TextUtil.colorify(section.getString("name").replace("%pokemon_name%", speciesName)));
        List<String> lore = section.getStringList("lore");
        lore.replaceAll(TextUtil::colorify);
        itemMeta.setLore(lore);
        if (enchantment) {
            itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
