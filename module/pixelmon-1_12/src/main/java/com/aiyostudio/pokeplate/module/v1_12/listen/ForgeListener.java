package com.aiyostudio.pokeplate.module.v1_12.listen;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.data.DataContainer;
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
            String name = e.player.getDisplayNameString();
            Player player = Bukkit.getPlayer(name);
            if (!PokePlate.getApi().hasPokemon(player, e.getPokemon().getSpecies())) {
                DataContainer.PLAYER_DATA_MAP.get(name).addPokedex(e.getPokemon().getSpecies().name());
            }
        } else if (event.getForgeEvent() instanceof EvolveEvent.PostEvolve) {
            EvolveEvent.PostEvolve e = (EvolveEvent.PostEvolve) event.getForgeEvent();
            String name = e.player.getDisplayNameString();
            Player player = Bukkit.getPlayer(name);
            if (!PokePlate.getApi().hasPokemon(player, e.pokemon.getSpecies())) {
                DataContainer.PLAYER_DATA_MAP.get(name).addPokedex(e.pokemon.getSpecies().name());
            }
        }
    }
}
