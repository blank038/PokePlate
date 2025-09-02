package com.aiyostudio.pokeplate.util;

import com.aiyostudio.pokeplate.PokePlate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlayerUtil {

    public static void executeCommands(Player player, List<String> commands) {
        List<String> opCommands = commands.stream()
                .filter((command) -> command.startsWith("op:"))
                .map((v) -> v.substring(3).replace("%player%", player.getName()))
                .collect(Collectors.toList());
        List<String> consoleCommands = commands.stream()
                .filter((command) -> command.startsWith("console:"))
                .map((v) -> v.substring(8).replace("%player%", player.getName()))
                .collect(Collectors.toList());
        if (!opCommands.isEmpty()) {
            boolean isOp = player.isOp();
            try {
                player.setOp(true);
                opCommands.forEach((v) -> Bukkit.dispatchCommand(player, v));
            } catch (Exception e) {
                PokePlate.getInstance().getLogger().log(Level.WARNING, e, () -> "Failed to run the commands as operator");
            } finally {
                player.setOp(isOp);
            }
        }
        if (!consoleCommands.isEmpty()) {
            consoleCommands.forEach((c) -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c));
        }
    }
}
