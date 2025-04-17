package me.hretsam.ipnotify.commands;

import java.util.List;
import me.hretsam.ipnotify.IPObject;
import me.hretsam.ipnotify.IPNotify;
import me.hretsam.ipnotify.data.DataException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Hretsam
 */
public class CommandIpList implements IPCommand {

    @Override
    public void run(IPNotify parent, CommandSender sender, String command, String[] args) {
        try {
            // Check arguments length
            if (args.length == 1) {
                // Get player
                Player targetPlayer = parent.getServer().getPlayer(args[0]);
                // See if player exits
                if (targetPlayer == null) {
                    // Player doesn't exist

                    // Check for permissions
                    if (!parent.getPermissions().hasPermission(sender, IPNotify.getConfig().othernode, "IPNotify.other")) {
                        sender.sendMessage("You don't have Permission to do that");
                        return;
                    }

                    //Print message
                    sender.sendMessage("*** Player not online ***");

                    // Get UserIplist with the users, where ignoring the case
                    List<IPObject> iplist = parent.getDataHandler().getUserIplist(parent.getDataHandler().checkCaseIndependant(args[0]), IPNotify.getConfig().maxIpListSize);
                    // See if got results
                    if (iplist != null && iplist.size() > 0) {
                        // Got results, print
                        sender.sendMessage("Listing ip's which " + args[0] + " to login:");
                        for (IPObject iip : iplist) {
                            sender.sendMessage(ChatColor.YELLOW + iip.getValue() + " on '" + iip.getDateString() + "'");
                        }
                    } else {
                        // No result
                        sender.sendMessage(ChatColor.YELLOW + "Player not found!");
                    }
                    return;
                }

                // If the sender is a player, check if the name target is himself
                if (sender instanceof Player
                        && (targetPlayer.getName().equalsIgnoreCase(((Player) sender).getName())
                        && !parent.getPermissions().hasPermission(sender, IPNotify.getConfig().othernode, "IPNotify.self"))) {
                    sender.sendMessage("You don't have Permission to do that");
                    return;
                } else {
                    // Check if got permission (checks server to for future server permission adjustment)
                    if (!parent.getPermissions().hasPermission(sender, IPNotify.getConfig().othernode, "IPNotify.other")) {
                        sender.sendMessage("You don't have Permission to do that");
                        return;
                    }
                }

                // Get list
                List<IPObject> iplist = parent.getDataHandler().getUserIplist(targetPlayer.getName(), IPNotify.getConfig().maxIpListSize);

                // Print results, (dont need extra null check, as the player is logged in)
                sender.sendMessage("Listing ip's which " + targetPlayer.getName() + " to login:");
                for (IPObject iip : iplist) {
                    sender.sendMessage(ChatColor.YELLOW + iip.getValue() + " on '" + iip.getDateString() + "'");
                }
            } else {
                //Invalid amount of arguments, print out usage message
                sender.sendMessage(ChatColor.YELLOW + "Usage: /iplist [player]");
            }
            return;
        } catch (DataException de) {
            sender.sendMessage(ChatColor.RED + "Exception in getting ip list " + de.getMessage());

        }
    }
}
