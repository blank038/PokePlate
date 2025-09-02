package com.aiyostudio.pokeplate.module.v1_12.listen;

import com.aiyostudio.pokeplate.api.PlateApi;
import com.aystudio.core.forge.event.ForgeEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Blank038
 */
public class ForgeListener implements Listener {

    @EventHandler
    public void onPlayerCaptureEvent(ForgeEvent event) {
        if (event.getForgeEvent() instanceof CaptureEvent.SuccessfulCapture) {
            CaptureEvent.SuccessfulCapture e = (CaptureEvent.SuccessfulCapture) event.getForgeEvent();
            if (e.isCanceled()) {
                return;
            }
            Player player = Bukkit.getPlayer(e.player.getUniqueID());
            PlateApi.addPokedex(player, e.getPokemon().getSpecies().name());
        } else if (event.getForgeEvent() instanceof EvolveEvent.PostEvolve) {
            EvolveEvent.PostEvolve e = (EvolveEvent.PostEvolve) event.getForgeEvent();
            Player player = Bukkit.getPlayer(e.player.getUniqueID());
            PlateApi.addPokedex(player, e.pokemon.getSpecies().name());
        }
    }
}
