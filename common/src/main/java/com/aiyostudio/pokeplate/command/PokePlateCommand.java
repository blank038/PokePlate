package com.aiyostudio.pokeplate.command;

import com.aiyostudio.pokeplate.PokePlate;
import com.aiyostudio.pokeplate.gui.MainGui;
import com.aiyostudio.pokeplate.i18n.I18n;
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
                new MainGui((Player) sender);
            }
        } else if (sender.hasPermission("pokeplate.admin")) {
            switch (args[0]) {
                case "reload":
                    this.reloadConfig(sender);
                    break;
                case "add":
                    this.add(sender, args);
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
        if (PokePlate.getApi().hasPokemon(args[2])) {
            if (PokePlate.getApi().give(sender, player, args)) {
                sender.sendMessage(I18n.getStrAndHeader("success"));
            }
        } else {
            sender.sendMessage(I18n.getStrAndHeader("wrong-pokemon"));
        }
    }
}
