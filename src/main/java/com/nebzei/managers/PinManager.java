package com.nebzei.managers;

import com.nebzei.TFA;
import com.nebzei.utilities.Utils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PinManager {

    private HashMap<UUID, Integer> attempts;

    private TFA plugin;

    public PinManager(TFA core) {
        this.plugin = core;

        this.attempts = new HashMap<>();
    }

    private boolean canAttempt(Player player) {
        if (!attempts.containsKey(player.getUniqueId())) return true;

        return (attempts.get(player.getUniqueId()) < plugin.getConfig().getInt("settings.login-attempts"));
    }

    public boolean addAttempt(Player player) {
        if (!canAttempt(player)) {

            if (plugin.getConfig().getBoolean("settings.ban-after-failed")) {

                Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(),
                        Utils.c(plugin.getConfig().getString("settings.ban-reason")), null, null);

                return false;
            }

            player.kickPlayer(Utils.c(plugin.getConfig().getString("messages.reached-max")));

            return false;

        } else {

            attempts.replace(player.getUniqueId(), attempts.get(player.getUniqueId()) + 1);

            return true;
        }

    }

    public HashMap<UUID, Integer> getAttempts() {
        return attempts;
    }
}
