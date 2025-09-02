package com.aiyostudio.pokeplate.view;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.data.DataContainer;
import com.aiyostudio.pokeplate.data.plate.PlateData;
import com.aiyostudio.pokeplate.data.player.PlayerData;
import com.aiyostudio.pokeplate.storage.StorageHandler;
import com.aystudio.core.bukkit.util.inventory.GuiModel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Files;

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

        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        GuiModel model = new GuiModel(data.getString("title"), data.getInt("size"));
        model.registerListener(PokePlate.getInstance());
        model.onClick((e) -> {
            e.setCancelled(true);
        });
        model.openInventory(player);

//        PokePlate.getInstance().saveResource("view/select.yml", "view/select.yml", false, (file) -> {
//            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
//
//            GuiModel model = new GuiModel(data.getString("title", "")
//                    .replace("%display%", PokePlate.getApi().getStarShowName(star)), data.getInt("size"));
//            model.registerListener(PokePlate.getInstance());
//            model.setCloseRemove(true);
//        });
//        // 创建界面
//        File file = new File(PokePlate.getInstance().getDataFolder() + "/view", ".yml");
//        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
//        GuiModel model = new GuiModel(data.getString("title", "")
//                .replace("%display%", PokePlate.getApi().getStarShowName(star)), data.getInt("size"));
//        model.registerListener(PokePlate.getInstance());
//        model.setCloseRemove(true);
//        // 设置物品
//        if (data.contains("items")) {
//            for (String key : data.getConfigurationSection("items").getKeys(false)) {
//                ConfigurationSection section = data.getConfigurationSection("items." + key);
//                ItemStack itemStack = new ItemStack(Material.valueOf(section.getString("type")),
//                        section.getInt("amount"), (short) section.getInt("data"));
//                ItemMeta meta = itemStack.getItemMeta();
//                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name")));
//                List<String> lore = new ArrayList<>();
//                String stats = DataContainer.PLAYER_DATA_MAP.get(player.getName()).hasReward(star + "s")
//                        ? "§c已领取" : (PokePlate.getApi().allowGet(player, star) ? "§a可领取" : "§e条件未达");
//                // 增加奖励 Lore
//                for (String line : section.getStringList("lore")) {
//                    String rel = ChatColor.translateAlternateColorCodes('&', line)
//                            .replace("%stats%", stats);
//                    if (rel.contains("%reward%")) {
//                        for (String l : PokePlate.getInstance().getConfig().getStringList("Info." + star + "s")) {
//                            lore.add(ChatColor.translateAlternateColorCodes('&', l));
//                        }
//                        continue;
//                    }
//                    lore.add(rel);
//                }
//                meta.setLore(lore);
//                itemStack.setItemMeta(meta);
//                // 判断是否有 action
//                if (section.contains("action")) {
//                    NBTItem nbtItem = new NBTItem(itemStack);
//                    nbtItem.setString("ListAction", section.getString("action"));
//                    itemStack = nbtItem.getItem();
//                }
//                for (int i : CommonUtil.formatSlots(section.getString("slot"))) {
//                    model.setItem(i, itemStack);
//                }
//            }
//        }
//        // 设置精灵物品列表
//        List<String> pokemons = PokePlate.getApi().getPokemonListByStar(star);
//        Integer[] slot = CommonUtil.formatSlots(data.getString("pokemon_slot"));
//        // 计算界面数据
//        final int maxPage = (pokemons.isEmpty() ? 1 : (pokemons.size() / slot.length))
//                + (pokemons.isEmpty() ? 0 : (pokemons.size() % slot.length > 0 ? 1 : 0));
//        final int start = page > maxPage ? 1 : (page - 1) * slot.length,
//                end = page * slot.length;
//        for (int index = 0, s = start; s < end && s < pokemons.size()
//                && index < slot.length; s++, index++) {
//            String pokemon = pokemons.get(s);
//            try {
//                ItemStack itemStack = DataContainer.ITEM_MAP.get(pokemon).clone();
//                if (PokePlate.getApi().hasPokemon(player, pokemon)) {
//                    itemStack.addUnsafeEnchantment(Enchantment.LUCK, 10);
//                }
//                ItemMeta itemMeta = itemStack.getItemMeta();
//                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', data.getString("pokemon_info.name")
//                        .replace("%pokemon_name%", PokePlate.getApi().getPokemonNameBySpeciesValue(pokemon))));
//                List<String> list = new ArrayList<>();
//                for (String line : data.getStringList("pokemon_info.lore")) {
//                    list.add(ChatColor.translateAlternateColorCodes('&', line));
//                }
//                itemMeta.setLore(list);
//                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
//                itemStack.setItemMeta(itemMeta);
//                model.setItem(slot[index], itemStack);
//            } catch (Exception e) {
//                PokePlate.getInstance().getLogger().log(Level.WARNING, e, () -> String.format("Failed to create photo from %s", pokemon));
//            }
//        }
//        model.onClick((e) -> {
//            e.setCancelled(true);
//            if (e.getClickedInventory() == e.getInventory()) {
//                ItemStack itemStack = e.getCurrentItem();
//                if (itemStack == null || itemStack.getType() == Material.AIR) {
//                    return;
//                }
//                NBTItem nbtItem = new NBTItem(itemStack);
//                if (nbtItem.hasTag("ListAction")) {
//                    String action = nbtItem.getString("ListAction");
//                    Player clicker = (Player) e.getWhoClicked();
//                    switch (action) {
//                        case "up":
//                            if (page == 1) {
//                                clicker.sendMessage(I18n.getStrAndHeader("no-previous-page"));
//                                break;
//                            }
//                            ListGui.openGui(player, star, page - 1);
//                            break;
//                        case "down":
//                            if (page >= maxPage) {
//                                clicker.sendMessage(I18n.getStrAndHeader("no-next-page"));
//                                break;
//                            }
//                            ListGui.openGui(player, star, page + 1);
//                            break;
//                        case "reward":
//                            if (!PokePlate.getApi().allowGet(clicker, star)) {
//                                clicker.sendMessage(I18n.getStrAndHeader("claimed"));
//                                break;
//                            }
//                            PokePlate.getInstance().giveReward(clicker, star);
//                            clicker.sendMessage(I18n.getStrAndHeader("gotten").replace("%star_name%", PokePlate.getApi().getStarShowName(star)));
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            }
//        });
//        model.openInventory(player);
    }
}
