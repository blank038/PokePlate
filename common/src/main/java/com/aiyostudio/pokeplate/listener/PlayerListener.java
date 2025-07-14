package com.aiyostudio.pokeplate.listener;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.PlayerData;
import com.aiyostudio.pokeplate.data.DataContainer;
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            DataContainer.PLAYER_DATA_MAP.put(e.getPlayer().getName(), new PlayerData(e.getPlayer()));
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        PlayerData playerData = DataContainer.PLAYER_DATA_MAP.remove(e.getPlayer().getName());
        if (playerData != null) {
            playerData.save();
        }
    }
}
