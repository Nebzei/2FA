package com.nebzei.events;

import com.nebzei.TFA;
import com.nebzei.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class PJEvent implements Listener {

    private TFA plugin;

    public PJEvent(TFA core) {
        this.plugin = core;
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {

            plugin.getSQLManager().addLog(event.getPlayer().getName(), event.getPlayer().getAddress().getAddress().toString());

            if (event.getPlayer().hasPermission(plugin.getConfig().getString("permissions.pin-staff"))) {

                String lastIP = plugin.getSQLManager().getCurrIP(event.getPlayer().getName());

                if (Objects.equals(lastIP, "")) return;

                final String LAST = lastIP.replaceAll(", ", ".");
                final String CURRENT = event.getPlayer().getAddress().getAddress().toString().substring(1);

                if (!Objects.equals(LAST, CURRENT)) {

                    plugin.getSQLManager().setIP(event.getPlayer().getName(), event.getPlayer().getAddress().getAddress().toString());

                    Bukkit.getOnlinePlayers().forEach(pl -> {

                        if (pl.hasPermission(plugin.getConfig().getString("permissions.login-alerts"))) {

                            if (plugin.getAlerts().contains(pl)) return;

                            pl.sendMessage(Utils.c(plugin.getConfig().getString("messages.alert_staff_new_ip")
                                            .replaceAll("%player%", event.getPlayer().getName())));

                        }
                    });
                }
            }

            if (event.getPlayer().hasPermission(plugin.getConfig().getString("permissions.pin-require"))) {

                if (plugin.getSQLManager().getPin(event.getPlayer().getName()) == 0) return;

                event.getPlayer().sendMessage(Utils.c(plugin.getConfig().getString("messages.login_message")));

                plugin.getPinManager().getAttempts().putIfAbsent(event.getPlayer().getUniqueId(), 0);

            }

            plugin.getSQLManager().close();

        }, 20 * 2);
    }
}
