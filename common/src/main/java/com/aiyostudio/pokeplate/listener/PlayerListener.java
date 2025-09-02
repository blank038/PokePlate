package com.aiyostudio.pokeplate.listener;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.api.event.PlateCompleteEvent;
import com.aiyostudio.pokeplate.api.event.PlateUnlockEvent;
import com.aiyostudio.pokeplate.data.plate.PlateData;
import com.aiyostudio.pokeplate.data.DataContainer;
import com.aiyostudio.pokeplate.i18n.I18n;
import com.aiyostudio.pokeplate.storage.StorageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Blank038
 */
public class PlayerListener implements Listener {
    private final PokePlate plugin = PokePlate.getInstance();

    /**
     * Only YAML
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        StorageHandler.loadPlayerData(e.getPlayer().getName());
    }

    /**
     * Only YAML
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        StorageHandler.unloadAndSave(e.getPlayer().getName());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlateComplete(PlateCompleteEvent event) {
        PlateData plateData = DataContainer.PLATE_DATA.get(event.getPlateKey());
        if (plateData == null) {
            return;
        }
        event.getPlayer().sendMessage(I18n.getStrAndHeader("complete")
                .replace("%plate_name%", plateData.getDisplayName()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlateUnlock(PlateUnlockEvent event) {
        PlateData plateData = DataContainer.PLATE_DATA.get(event.getPlateKey());
        if (plateData == null) {
            return;
        }
        event.getPlayer().sendMessage(I18n.getStrAndHeader("unlock")
                .replace("%plate_name%", plateData.getDisplayName())
                .replace("%species%", PokePlate.getModule().getTranslationName(event.getSpecies())));
    }
}
