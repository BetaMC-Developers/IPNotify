package me.hretsam.ipnotify.commands;

import me.hretsam.ipnotify.data.FlatFileHandler;
import me.hretsam.ipnotify.IPNotify;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 *
 * @author Hretsam
 */
public class CommandIP implements IPCommand {

    @Override
    public void run(IPNotify parent, CommandSender sender, String command, String[] args) {
        // Checks if the sender is a player, so it will filter out colours for the server console
        boolean useColour = (sender instanceof Player);

        // Check array length if there is an argument
        if (args.length == 0) {
            // Check permissions
            if (parent.getPermissions().hasPermission(sender, IPNotify.getConfig().selfnode, "IPNotify.self")) {
                // Check if the sender is a player or the server
                if (sender instanceof Player) {
                    // Print a formatted ip address
                    sender.sendMessage("Your IP address is: " + FlatFileHandler.formatIP(((Player) sender).getAddress().toString()));
                } else {
                    sender.sendMessage("You are the server! ");
                }
            } else {
                sender.sendMessage("You don't have Permission to do that");
            }
        } else if (args.length == 1) {
            // Get target player
            Player targetPlayer = parent.getServer().getPlayer(args[0]);
            // See if player exists
            if (targetPlayer == null) {
                sender.sendMessage((useColour ? ChatColor.YELLOW : "") + "No such player.");
                return;
            }
            // Check for permissions
            if (parent.getPermissions().hasPermission(sender, IPNotify.getConfig().othernode, "IPNotify.other")) {
                // Prints out ip
                sender.sendMessage(targetPlayer.getName() + " IP address is: " + FlatFileHandler.formatIP(targetPlayer.getAddress().toString()));
            } else {
                sender.sendMessage("You don't have Permission to do that");
            }
        } else {
            //Invalid amount of arguments, print out usage message
            sender.sendMessage((useColour ? ChatColor.YELLOW : "") + "Usage: /ip [player]");
        }
    }
}
