package com.aiyostudio.pokeplate.api.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Getter
public class PlateUnlockEvent  extends PlayerEvent
        implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final String plateKey, species;
    private boolean cancelled;

    public PlateUnlockEvent(Player who, String plateKey, String species) {
        super(who);
        this.plateKey = plateKey;
        this.species = species;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
