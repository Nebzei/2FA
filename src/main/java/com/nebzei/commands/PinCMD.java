package com.nebzei.commands;

import com.nebzei.TFA;
import com.nebzei.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PinCMD implements CommandExecutor {

    private TFA plugin;

    public PinCMD(TFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (plugin.getPinManager().getAttempts().containsKey(player.getUniqueId())) {

            if (plugin.getSQLManager().getPin(player.getName()) != Integer.parseInt(args[0])) {

                if (plugin.getPinManager().addAttempt(player)) {

                    String str = plugin.getConfig().getString("messages.wrongpin_message");

                    final UUID uuid = player.getUniqueId();
                    final String currAttempts = String.valueOf(plugin.getPinManager().getAttempts().get(uuid));
                    final String totalAttempts = String.valueOf(plugin.getConfig().getInt("settings.login-attempts"));

                    player.sendMessage(Utils.c(str
                        .replaceAll("%x%", currAttempts)
                        .replace("%z%", totalAttempts)));

                } else {

                    player.kickPlayer(plugin.getConfig().getString("settings.ban-reason"));

                }

                return false;

            } else {

                player.sendMessage(Utils.c(plugin.getConfig().getString("messages.loggedin_message")));

                plugin.getPinManager().getAttempts().remove(player.getUniqueId());

            }
        }

        plugin.getSQLManager().close();

        return false;
    }
}
