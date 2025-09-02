package com.aiyostudio.pokeplate.hook;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.data.player.PlayerData;
import com.aiyostudio.pokeplate.data.DataContainer;
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
//        if (DataContainer.PLAYER_DATA_MAP.containsKey(p.getName())) {
//            PlayerData playerData = DataContainer.PLAYER_DATA_MAP.get(p.getName());
//            switch (params) {
//                case "count":
//                    return String.valueOf(playerData.getCount());
//                case "pg":
//                    return String.valueOf(playerData.getProgress() * 100);
//                default:
//                    break;
//            }
//        }
        return null;
    }
}
