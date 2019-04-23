package com.nebzei;

import com.nebzei.commands.*;
import com.nebzei.events.PJEvent;
import com.nebzei.events.PMEvent;
import com.nebzei.managers.PinManager;
import com.nebzei.managers.SQLManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class TFA extends JavaPlugin {

    private SQLManager sqlManager;
    private PinManager pinManager;
    private TFA instance;

    private ArrayList<Player> alerts;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        sqlManager = new SQLManager(this);
        pinManager = new PinManager(this);

        getServer().getPluginManager().registerEvents(new PMEvent(this), this);
        getServer().getPluginManager().registerEvents(new PJEvent(this), this);

        getCommand("setpin").setExecutor(new SetCMD(this));
        getCommand("pin").setExecutor(new PinCMD(this));
        getCommand("resetpin").setExecutor(new ResetCMD(this));
        getCommand("pinalerts").setExecutor(new AlertsCMD(this));
        getCommand("listip").setExecutor(new ListCMD(this));

        alerts = new ArrayList<>();

    }

    @Override
    public void onDisable() {

    }

    public TFA getInstance() {
        return instance;
    }

    public SQLManager getSQLManager() {
        return sqlManager;
    }

    public PinManager getPinManager() {
        return pinManager;
    }

    public ArrayList<Player> getAlerts() {
        return alerts;
    }
}
