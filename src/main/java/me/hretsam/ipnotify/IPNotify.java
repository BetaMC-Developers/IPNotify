// Package
package me.hretsam.ipnotify;

// Imports
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.hretsam.ipnotify.commands.CommandAIpBan;
import me.hretsam.ipnotify.commands.CommandIP;
import me.hretsam.ipnotify.commands.CommandIpCheck;
import me.hretsam.ipnotify.commands.CommandIpList;
import me.hretsam.ipnotify.commands.CommandIpUsers;
import me.hretsam.ipnotify.commands.IPCommand;
import me.hretsam.ipnotify.data.DataException;
import me.hretsam.ipnotify.data.DataHandler;
import me.hretsam.ipnotify.data.FlatFileHandler;

import me.hretsam.ipnotify.data.SqlHandler;
import me.hretsam.ipnotify.data.sql.MysqlSource;
import me.hretsam.ipnotify.data.sql.SqlLiteSource;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * IPNotify for Bukkit - Advanced IP logger
 * 
 * @author yoharnu & Hretsam
 */
public class IPNotify extends JavaPlugin {

    private final IPPlayerListener playerListener = new IPPlayerListener(this);
    private IPPermissionHandler permissions;
    private DataHandler dataHandler;
    private IPConfig config;
    private static IPNotify plugin;
    private ColouredConsoleSender console;

    @Override
    public void onDisable() {
        try {
            dataHandler.shutdown();
        } catch (DataException ex) {
            writelog("Datahandler shutdown caused an exception! " + ex.getMessage(), true);
        }
        writelog("IPNotify Disabled!", false);
    }

    @Override
    public void onEnable() {
        // Set plugin reference
        plugin = this;
        // Setup coloured console
        console = new ColouredConsoleSender((CraftServer) getServer());

        // Get the information from the yml file.
        PluginDescriptionFile pdfFile = this.getDescription();

        // Create the pluginmanager
        PluginManager pm = getServer().getPluginManager();

        // Register listeners
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);

        // Start the permissions Handlers
        permissions = new IPPermissionHandler(this);

        // Starts the configuration handler
        try {
            config = new IPConfig(this, this.getDataFolder());
        } catch (IOException ex) {
            writelog("Cannot open config! " + ex.getMessage(), true);
        }

        // Starts the data handler
        try {
            if (config.source.equalsIgnoreCase("flatfile")) {
                writelog("Using flatfile to store players.", false);
                dataHandler = new FlatFileHandler(this, this.getDataFolder());

            } else if (config.source.equalsIgnoreCase("mysql")) {
                writelog("Using mysql to store players.", false);
                dataHandler = new SqlHandler(this, new MysqlSource());

            } else if (config.source.equalsIgnoreCase("sqllite")) {
                writelog("Using sqllite to store players.", false);
                dataHandler = new SqlHandler(this, new SqlLiteSource());

            } else {
                dataHandler = new FlatFileHandler(this, this.getDataFolder());
            }
        } catch (IOException ex) {
            writelog("Cannot start the datahandler! " + ex.getMessage(), true);
        }


        // Print that the plugin has been enabled!
        writelog(pdfFile.getName() + " version "
                + pdfFile.getVersion() + " is enabled!", false);
    }

    /**
     * Returns the filehandler
     * @return 
     */
    public DataHandler getDataHandler() {
        return dataHandler;
    }

    /**
     * Returns the config
     * @return 
     */
    public static IPConfig getConfig() {
        return plugin.config;
    }

    /**
     * Returns permissions
     */
    public IPPermissionHandler getPermissions() {
        return permissions;
    }

    /**
     * Writes a log message
     * @param message
     * @param error 
     */
    public static void writelog(String message, boolean error) {
        if (error) {
            plugin.console.sendMessage(new StringBuilder().append(ChatColor.RED).append("[").append("IPNotify").append("] [ERROR] - ").append(message).toString());
        } else {
            plugin.console.sendMessage(new StringBuilder("[").append("IPNotify").append("] - ").append(message).toString());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // Get command
        // Make a IPCommand object
        IPCommand command = null;
        try {
            System.out.println(dataHandler.getLastUsedUsername(args[0]));
        } catch (DataException ex) {
            Logger.getLogger(IPNotify.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Check for commands
        if (commandLabel.equalsIgnoreCase("ip")) {
            command = new CommandIP();
        } else if (commandLabel.equalsIgnoreCase("ipusers")) {
            command = new CommandIpUsers();
        } else if (commandLabel.equalsIgnoreCase("iplist")) {
            command = new CommandIpList();
        } else if (commandLabel.equalsIgnoreCase("ipcheck")) {
            command = new CommandIpCheck();
        } else if (commandLabel.equalsIgnoreCase("aipban")) {
            command = new CommandAIpBan();
        }

        // Check if the command is found
        if (command != null) {
            // If found run it
            command.run(this, sender, commandLabel, args);
            // Return true;
            return true;
        }
        // none found return false;
        return false;
    }

    /**
     * Prints message to server and all allowed players
     * @param message 
     */
    public void sendWarningMessage(String message) {
        // Check if message is not server log only
        if (!getConfig().warningnode.equalsIgnoreCase("server")) {
            // Get all connected players
            for (Player player : getServer().getOnlinePlayers()) {
                // Check if they have permissions to get the warning
                if (permissions.hasPermission(player, getConfig().warningnode, "IPNotify.warning")) {
                    // Send warning
                    player.sendMessage(message);
                }
            }
        }
        // Send warning to server log/console
        writelog(message, false);
    }
}
