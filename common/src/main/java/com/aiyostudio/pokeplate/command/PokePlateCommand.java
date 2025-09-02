package com.aiyostudio.pokeplate.command;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.api.PlateApi;
import com.aiyostudio.pokeplate.api.impl.IPokemonWrapper;
import com.aiyostudio.pokeplate.data.player.PlayerData;
import com.aiyostudio.pokeplate.view.SelectView;
import com.aiyostudio.pokeplate.i18n.I18n;
import com.aiyostudio.pokeplate.storage.StorageHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Blank038
 */
public class PokePlateCommand implements CommandExecutor {
    private final PokePlate plugin = PokePlate.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                SelectView.open((Player) sender);
            }
        } else if (sender.hasPermission("pokeplate.admin")) {
            switch (args[0]) {
                case "reload":
                    this.reloadConfig(sender);
                    break;
                case "add":
                    this.add(sender, args);
                    break;
                case "check":
                    this.check(sender, args);
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    private void reloadConfig(CommandSender sender) {
        plugin.loadConfig();
        sender.sendMessage(I18n.getStrAndHeader("reload"));
    }

    private void add(CommandSender sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(I18n.getStrAndHeader("pls-enter-player"));
            return;
        }
        Player player = Bukkit.getPlayerExact(args[1]);
        if (player == null || !player.isOnline()) {
            sender.sendMessage(I18n.getStrAndHeader("player-offline"));
            return;
        }
        if (args.length == 2) {
            sender.sendMessage(I18n.getStrAndHeader("pls-enter-pokemon"));
            return;
        }
        if (PokePlate.getModule().hasSpecies(args[2])) {
            if (PokePlate.getModule().give(sender, player, args)) {
                sender.sendMessage(I18n.getStrAndHeader("success"));
            }
        } else {
            sender.sendMessage(I18n.getStrAndHeader("wrong-pokemon"));
        }
    }

    private void check(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }
        PlayerData playerData = StorageHandler.getPlayerDataFromMemory(sender.getName()).orElse(null);
        if (playerData == null) {
            return;
        }

        int slot = 1;
        if (args.length > 1 && args[1].matches("\\d+")) {
            try {
                int temp = Integer.parseInt(args[1]);
                slot = temp >= 1 && temp <= 6 ? temp : 1;
            } catch (Exception ignored) {
            }
        }
        slot--;

        // Get party storage from target player.
        IPokemonWrapper pokemonWrapper = PokePlate.getModule().getPokemon(slot);
        if (pokemonWrapper == null) {
            sender.sendMessage(I18n.getStrAndHeader("not-found-pokemon"));
            return;
        }
        if (!sender.getName().equals(pokemonWrapper.getOriginTrainer())) {
            sender.sendMessage(I18n.getStrAndHeader("not-origin-trainer"));
            return;
        }
        PlateApi.addPokedex((Player) sender, pokemonWrapper.getSpecies());
        sender.sendMessage(I18n.getStrAndHeader("check"));
    }
}
