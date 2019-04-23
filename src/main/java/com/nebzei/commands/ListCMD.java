package com.nebzei.commands;

import com.nebzei.TFA;
import com.nebzei.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

public class ListCMD implements CommandExecutor {

    private TFA plugin;

    public ListCMD(TFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (!player.hasPermission("permissions.list-ip")) {
            player.sendMessage(Utils.c(plugin.getConfig().getString("messages.no-permission")));
            return false;
        }

        if (args.length < 1) {
            player.sendMessage(Utils.c("&8[&c!&8] &eThe proper use for this command is: &f/listip <name>"));
            return false;
        }

        final String NAME = args[0];

        List<String> ips = plugin.getSQLManager().getIPS(NAME);

        if (!ips.isEmpty()) {

            player.sendMessage(Utils.c(plugin.getConfig().getString("messages.getting-ips").replaceAll("%player%", NAME)));

            ips.forEach(str -> player.sendMessage(Utils.c("&c-&e " + toIP(str))));

            plugin.getServer().getLogger().log(Level.INFO, player.getName() + " has just requested IP list for: " + NAME);

        } else {

            player.sendMessage(Utils.c("&8[&c!&8] &eCould not find any data for " + NAME + "!"));

        }

        plugin.getSQLManager().close();

        return false;

    }

    private String toIP(String str) {

        String sub = str.substring(1);

        if (!plugin.getConfig().getBoolean("settings.full-ip-alerts"))

            return sub.substring(0, sub.indexOf(".")) + "" + sub.substring(sub.indexOf(".")).replaceAll("\\d", "#");

        return sub;

    }
}
