package com.nebzei.commands;

import com.nebzei.TFA;
import com.nebzei.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCMD implements CommandExecutor {

    private TFA plugin;

    public SetCMD(TFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (!player.hasPermission("permissions.pin-set")) {
            player.sendMessage(Utils.c(plugin.getConfig().getString("messages.no-permission")));
            return false;
        }

        if (args.length < 2) {
            player.sendMessage(Utils.c("&8[&c!&8] &eThe proper use for this command is: &f/setpin <name> <pin>"));
            return false;
        }

        final String NAME = args[0];

        if (Bukkit.getPlayer(args[0]) == null){

            player.sendMessage(Utils.c(plugin.getConfig().getString("messages.player_not_online").replaceAll("%player%", args[0])));
            return false;

        }

        final Player target = Bukkit.getPlayer(args[0]);

        if (plugin.getSQLManager().hasData(NAME)) {

            plugin.getSQLManager().setPin(NAME, Integer.parseInt(args[1]));

            player.sendMessage(Utils.c(plugin.getConfig().getString("messages.changepin_message")
                            .replaceAll("%player%", target.getName())
                            .replaceAll("%pin%", String.valueOf(args[1]))));

        } else {

            plugin.getSQLManager().addData(target, Integer.parseInt(args[1]));

            player.sendMessage(Utils.c(plugin.getConfig().getString("messages.changepin_message")
                            .replaceAll("%player%", target.getName())
                            .replaceAll("%pin%", String.valueOf(args[1]))));

        }

        plugin.getSQLManager().close();

        return false;
    }
}
