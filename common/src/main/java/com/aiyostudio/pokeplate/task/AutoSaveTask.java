package com.aiyostudio.pokeplate.task;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.storage.StorageHandler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class AutoSaveTask implements Runnable {
    private static BukkitTask instance;

    @Override
    public void run() {

    }

    public static void register() {
        if (instance != null) {
            instance.cancel();
        }
        instance = Bukkit.getScheduler().runTaskTimerAsynchronously(PokePlate.getInstance(), StorageHandler::saveAll, 1200L, 1200L);
    }
}
