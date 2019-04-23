package com.nebzei.managers;

import com.nebzei.TFA;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class SQLManager {

    private TFA plugin;

    private Connection con = null;
    private Statement statement = null;

    private String host, database, username, password;
    private int port;

    public SQLManager(TFA plugin) {

        this.plugin = plugin;

        username = plugin.getConfig().getString("database.username");
        password = plugin.getConfig().getString("database.password");
        host = plugin.getConfig().getString("database.host");
        database = plugin.getConfig().getString("database.database_name");
        port = plugin.getConfig().getInt("database.port");

        try {

            openConnection();

            if (!tableExist("data")) {

                statement = con.createStatement();

                statement.executeUpdate("CREATE TABLE data ( "
                        + "name VARCHAR(50) NOT NULL, "
                        + "uuid VARCHAR(50), "
                        + "ip VARCHAR(50), "
                        + "pin VARCHAR(230) NOT NULL )");

                statement.close();

            }

            if (!tableExist("storage")) {

                statement = con.createStatement();

                statement.executeUpdate("CREATE TABLE storage ( "
                        + "name VARCHAR(50) NOT NULL, "
                        + "ip VARCHAR(50) NOT NULL )");

                statement.close();

            }

            con.close();

        } catch (SQLException | ClassNotFoundException e) {

            e.printStackTrace();

        }

    }

    /**
     * Open connection to the database.
     * @throws SQLException - If there connection to database cannot be established.
     * @throws ClassNotFoundException - If connection to JDBC Driver cannot be established.
     */
    private void openConnection() throws SQLException, ClassNotFoundException {

        synchronized (this) {

            if (con != null && !con.isClosed()) {
                return;
            }

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;

            try {

                Class.forName("com.mysql.jdbc.Driver");

            } catch (ClassNotFoundException e) {

                System.err.println("CNFE: Could not establish connection.");

                return;
            }

            try {

                this.con = DriverManager.getConnection(url, username, password);

            } catch (SQLException e) {

                System.err.println("SQL: Could not establish connection.");

            }
        }
    }

    /** Adds new data with the player information.
     * @param player - The user to add to the 'data' database.
     * @param pin - The pin to login.
     */
    public void addData(Player player, int pin) {

        try {

            openConnection();

            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO data VALUES (?, ?, ?, ?)");

            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.setString(3, player.getAddress().getAddress().toString());
            preparedStatement.setInt(4, pin);

            preparedStatement.executeUpdate();

            preparedStatement.close();

        } catch (SQLException | ClassNotFoundException e) {

            plugin.getLogger().log(Level.WARNING, e.getMessage());

        }

    }

    /**
     * Logs the name with IP if IP is different from the last ip login.
     * @param name - The user to add to the 'storage' database.
     * @param ip - The IP of the current user.
     */
    public void addLog(String name, String ip) {

        try {

            openConnection();

            List<String> currentIPS = getIPS(name);

            if (!currentIPS.contains(ip)) {

                PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO storage VALUES (?, ?)");

                preparedStatement.setString(1, name);
                preparedStatement.setString(2, ip);

                preparedStatement.executeUpdate();

                preparedStatement.close();

                Bukkit.getOnlinePlayers().forEach(pl -> {

                    if (plugin.getAlerts().contains(pl)) return;

                    if (pl.hasPermission(plugin.getConfig().getString("permissions.login-alerts"))) {

                        pl.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.getConfig().getString("messages.alert_message")
                                        .replaceAll("%player%", name)
                                        .replaceAll("%IP%", translate(ip))));

                    }

                });

            }

        } catch (SQLException e) {

            plugin.getLogger().log(Level.WARNING, e.getMessage());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the pin of the user.
     * @param player - The user to change pin.
     * @param pin - The pin to update.
     */
    public void setPin(String player, int pin) {

        try {

            openConnection();

            PreparedStatement preparedStatement = con.prepareStatement("UPDATE data SET pin = ? WHERE name = ? ;");

            preparedStatement.setInt(1, pin);

            preparedStatement.setString(2, player);

            preparedStatement.executeUpdate();

            preparedStatement.close();

        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Changes the ip of the user.
     * @param player - The user to change pin.
     * @param ip - The IP to update.
     */
    public void setIP(String player, String ip) {

        try {

            openConnection();

            PreparedStatement preparedStatement = con.prepareStatement("UPDATE data SET ip = ? WHERE name = ? ;");

            preparedStatement.setString(1, ip);

            preparedStatement.setString(2, player);

            preparedStatement.executeUpdate();

            preparedStatement.close();

        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Close the connection to database if one is open.
     */
    public void close() {
        try {
            if (con != null && !con.isClosed()){
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param player - The name of the player to search pin.
     * @return The current pin of the player.
     */
    public int getPin(String player) {

        try {

            openConnection();

            statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet r = statement.executeQuery("SELECT * FROM data WHERE name = '" + player + "' ;");

            if (r.next()) {

                int pin = r.getInt("pin");

                statement.close();

                return pin;

            }

        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().log(Level.WARNING, e.getMessage());
        }

        return 0;
    }

    /**
     * @param player - The name of the player to search IPs.
     * @return List of IP's player has logged in with.
     */
    public List<String> getIPS(String player) {

        List<String> ipList = new ArrayList<>();

        try {

            openConnection();

            statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet r = statement.executeQuery("SELECT * FROM storage WHERE name = '" + player + "' ;");

            while (r.next()) {

                ipList.add(r.getString("ip"));

            }

            statement.close();

        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return ipList.isEmpty() ? Collections.emptyList() : ipList;
    }

    /**
     * @param player - The name of the player to check if data exists.
     * @return If the player has data in the 'data' database.
     */
    public boolean hasData(String player) {
        try {

            openConnection();

            statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet r = statement.executeQuery("SELECT * FROM data WHERE name = '" + player + "' ;");

            while (r.next()) {

                return true;

            }

        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().log(Level.WARNING, e.getMessage());
        }

        return false;
    }

    /**
     * @param player - The name of the player to get current IP.
     * @return The current IP of the player from database.
     */
    public String getCurrIP(String player) {

        try {

            openConnection();

            statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet r = statement.executeQuery("SELECT * FROM data WHERE name = '" + player + "' ;");

            while (r.next()) {

                String string = r.getString("ip").substring(1);

                statement.close();

                return string;

            }

        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().log(Level.WARNING, e.getMessage());
        }

        return "";

    }

    /**
     * @param name - The name of the table to check if exists.
     * @return Table exists, true else false.
     */
    private boolean tableExist(String name) {

        try {

            statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet r = statement.executeQuery("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '" + plugin.getConfig().getString("database.database_name")
                    + "' AND table_name = '" + name + "'");

            if (r.next()) {
                if (r.getInt("COUNT(*)") == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            return false;

        } catch (SQLException ex) {

            plugin.getServer().getPluginManager().disablePlugin(plugin.getInstance());

        }

        return false;
    }

    /**
     * @param str - The IP substring not including the /.
     * @return Dependent on config boolean, returns the IP full or only first section visible.
     */
    private String translate(String str) {

        final String IP = str.substring(1);

        if (!plugin.getConfig().getBoolean("settings.full-ip-alerts"))
            return IP.substring(0, IP.indexOf(".")) + "" + IP.substring(IP.indexOf("."))
                    .replaceAll("\\d", "#");

        return IP;

    }

}


