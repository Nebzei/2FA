package com.nebzei.events;

import com.nebzei.TFA;
import com.nebzei.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PMEvent implements Listener {

    private TFA plugin;
    public PMEvent(TFA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (plugin.getPinManager().getAttempts().containsKey(event.getPlayer().getUniqueId())) {
            if (!plugin.getConfig().getBoolean("pre-login.can-move")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        if (plugin.getPinManager().getAttempts().containsKey(event.getPlayer().getUniqueId())){
            if (!plugin.getConfig().getBoolean("pre-login.can-interact")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        if (plugin.getAlerts().contains(event.getPlayer())){
            plugin.getAlerts().remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onChat (AsyncPlayerChatEvent event) {

        if (plugin.getPinManager().getAttempts().isEmpty()) return;

        if (plugin.getPinManager().getAttempts().containsKey(event.getPlayer().getUniqueId())){
            if (!plugin.getConfig().getBoolean("pre-login.can-talk")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Utils.c(plugin.getConfig().getString("messages.login_message")));
            }
        }
    }
}
