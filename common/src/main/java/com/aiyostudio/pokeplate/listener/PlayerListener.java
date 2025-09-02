package com.aiyostudio.pokeplate.listener;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.data.player.PlayerData;
import com.aiyostudio.pokeplate.data.DataContainer;
import com.aiyostudio.pokeplate.storage.StorageHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Blank038
 */
public class PlayerListener implements Listener {
    private final PokePlate plugin = PokePlate.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        StorageHandler.loadPlayerData(e.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        StorageHandler.unloadAndSave(e.getPlayer().getName());
    }
}
