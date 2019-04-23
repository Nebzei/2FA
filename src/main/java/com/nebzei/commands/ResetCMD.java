package com.nebzei.commands;

import com.nebzei.TFA;
import com.nebzei.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCMD implements CommandExecutor {

    private TFA plugin;

    public ResetCMD(TFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (!player.hasPermission("permissions.pin-admin-reset")) {
            player.sendMessage(Utils.c(plugin.getConfig().getString("messages.no-permission")));
            return false;
        }

        if (args.length < 1) {
            player.sendMessage(Utils.c( "&8[&c!&8] &eThe proper use for this command is: &f/resetpin <name>"));
            return false;
        }

        final String NAME = args[0];

        if (plugin.getSQLManager().hasData(NAME)) {

            plugin.getSQLManager().setPin(NAME, 0);

            player.sendMessage(Utils.c(plugin.getConfig().getString("messages.resetpin_message")
                            .replaceAll("%player%", NAME)));

        } else {

            player.sendMessage(Utils.c("&8[&c!&8] &eCould not find any data for " + NAME + "!"));

        }

        plugin.getSQLManager().close();

        return false;
    }
}
