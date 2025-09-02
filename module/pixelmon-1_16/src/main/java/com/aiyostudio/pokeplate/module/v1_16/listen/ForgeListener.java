package com.aiyostudio.pokeplate.module.v1_16.listen;

import com.aiyostudio.pokeplate.api.PlateApi;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * @author Blank038
 */
public class ForgeListener implements Listener {

    public ForgeListener() {
        Pixelmon.EVENT_BUS.addListener(EventPriority.NORMAL, true, CaptureEvent.SuccessfulCapture.class, (e) -> {
            if (e.isCanceled()) {
                return;
            }
            Player player = Bukkit.getPlayer(e.getPlayer().getUUID());
            if (e.getPokemon() != null && player != null) {
                PlateApi.addPokedex(player, e.getPokemon().getSpecies().getName());
            }
        });
        Pixelmon.EVENT_BUS.addListener(EventPriority.NORMAL, true, EvolveEvent.Post.class, (e) -> {
            Player player = Bukkit.getPlayer(e.getPlayer().getUUID());
            if (e.getPokemon() == null || player == null) {
                return;
            }
            PlateApi.addPokedex(player, e.getPokemon().getSpecies().getName());
        });
    }
}
