package com.aiyostudio.pokeplate.data;

import com.aiyostudio.pokeplate.PlayerData;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Blank038
 */
public class DataContainer {
    public static final Map<String, ItemStack> ITEM_MAP = new HashMap<>();
    public static final Map<String, List<String>> SPECIES_MAP = new HashMap<>();
    public static final Map<String, PlayerData> PLAYER_DATA_MAP = new HashMap<>();
    public static int pokedexCount = 0;
}
