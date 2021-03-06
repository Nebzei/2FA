package com.nebzei.commands;

import com.nebzei.TFA;
import com.nebzei.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertsCMD implements CommandExecutor {

    private TFA plugin;

    public AlertsCMD(TFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (!player.hasPermission("permissions.toggle-alerts")) {
            player.sendMessage(Utils.c(plugin.getConfig().getString("messages.no-permission")));
            return false;
        }

        if (args.length > 0){
            player.sendMessage(Utils.c("&8[&c!&8] &eThe proper use for this command is: &f/pinalerts"));
            return false;
        }

        if (plugin.getAlerts().contains(player)){
            
            plugin.getAlerts().remove(player);
            
            player.sendMessage(Utils.c(plugin.getConfig().getString("messages.alertsoff_message")));

        } else{
            
            plugin.getAlerts().add(player);
            
            player.sendMessage(Utils.c(plugin.getConfig().getString("messages.alertson_message")));

        }

        return false;
    }
}
