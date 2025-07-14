package com.aiyostudio.pokeplate.module.v1_16.listen;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.data.DataContainer;
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
            String name = e.getPlayer().getName().getString();
            Player player = Bukkit.getPlayer(name);
            if (e.getPokemon() != null && !PokePlate.getApi().hasPokemon(player, e.getPokemon().getSpecies())) {
                DataContainer.PLAYER_DATA_MAP.get(name).addPokedex(e.getPokemon().getSpecies().getName());
            }
        });
        Pixelmon.EVENT_BUS.addListener(EventPriority.NORMAL, true, EvolveEvent.Post.class, (e) -> {
            String name = e.getPlayer().getName().getString();
            Player player = Bukkit.getPlayer(name);
            if (e.getPokemon() != null && !PokePlate.getApi().hasPokemon(player, e.getPokemon().getSpecies())) {
                DataContainer.PLAYER_DATA_MAP.get(name).addPokedex(e.getPokemon().getSpecies().getName());
            }
        });
    }
}
