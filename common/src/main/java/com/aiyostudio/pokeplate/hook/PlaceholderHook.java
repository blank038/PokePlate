package com.aiyostudio.pokeplate.hook;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.data.plate.PlateData;
import com.aiyostudio.pokeplate.data.player.PlayerData;
import com.aiyostudio.pokeplate.data.DataContainer;
import com.aiyostudio.pokeplate.data.player.PokedexChildData;
import com.aiyostudio.pokeplate.storage.StorageHandler;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * @author Blank038
 */
public class PlaceholderHook extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "pokeplate";
    }

    @Override
    public String getAuthor() {
        return "Blank038";
    }

    @Override
    public String getVersion() {
        return PokePlate.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (p == null || !p.isOnline()) {
            return "";
        }
        PlayerData playerData = StorageHandler.getPlayerDataFromMemory(p.getName()).orElse(null);
        if (playerData == null) {
            return "";
        }
        String[] split = params.split("_");
        if (split.length == 1) {
            return "ERROR";
        }
        PlateData plateData = DataContainer.PLATE_DATA.get(split[1]);
        if (plateData == null) {
            return "NOT_FOUND_PLATE";
        }
        PokedexChildData childData = playerData.getChild(split[1]);
        switch (split[0]) {
            case "count":
                return String.valueOf(childData.getCount());
            case "pg":
                int progress = (int) ((double) childData.getCount() / plateData.getRequire().getPokemon().size() * 100);
                return String.valueOf(progress);
            default:
                break;
        }
        return "";
    }
}
