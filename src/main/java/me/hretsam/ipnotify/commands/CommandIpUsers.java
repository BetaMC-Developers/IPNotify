package me.hretsam.ipnotify.commands;

import java.util.List;
import me.hretsam.ipnotify.data.FlatFileHandler;
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
public class CommandIpUsers implements IPCommand {

    @Override
    public void run(IPNotify parent, CommandSender sender, String command, String[] args) {
        try {
            // Check arguments length
            if (args.length == 1) {

                String ip = null;

                // Check if argument contains an IP
                if (args[0].contains(".")) {
                    // Check permissions
                    if (!parent.getPermissions().hasPermission(sender, IPNotify.getConfig().othernode, "IPNotify.other")) {
                        sender.sendMessage("You don't have Permission to do that");
                        return;
                    }
                    // Print out message
                    sender.sendMessage("*** Raw IP address found ***");

                    // Set ip variable
                    ip = args[0];
                }
                if (ip == null) {
                    // Get target player
                    Player targetPlayer = parent.getServer().getPlayer(args[0]);
                    // See if player exists
                    if (targetPlayer == null) {
                        // Check permissions
                        if (!parent.getPermissions().hasPermission(sender, IPNotify.getConfig().othernode, "IPNotify.other")) {
                            sender.sendMessage("You don't have Permission to do that");
                            return;
                        }
                        // This one needs to be seperate! Due to the list op IPs of the user

                        // Print out message
                        sender.sendMessage("*** Player not online ***");

                        // Get list with ip's , where ignoring the case
                        List<IPObject> iplist = parent.getDataHandler().getUserIplist(parent.getDataHandler().checkCaseIndependant(args[0]), IPNotify.getConfig().maxIpListSize);
                        // Check if list is not empty
                        if (iplist != null && iplist.size() > 0) {
                            // Loop trough all ip's
                            for (IPObject ipp : iplist) {
                                // gets a list of users with the given IP
                                List<String> userlist = parent.getDataHandler().getIpUserList(ipp.getValue());
                                // There is atleast one, so no need for null check, print results
                                sender.sendMessage("Listing users with the ip '" + ipp.getValue() + "':");
                                // String builder 
                                StringBuilder sb = new StringBuilder();
                                // Loop trough all names in the list
                                for (String name : userlist) {

                                    // Checks if there should be a , added
                                    if (sb.length() > 0) {
                                        sb.append(", ");
                                    }
                                    // Checks the length of the string, to make sure no data is lost
                                    if (sb.length() + 2 + name.length() > 280) {
                                        // List to long, print results and reset list
                                        sender.sendMessage(sb.toString());
                                        sb = new StringBuilder().append(ChatColor.YELLOW).append(name);
                                    } else {
                                        // String length is ok, append name
                                        sb.append(name);
                                    }

                                }
                                // Print the rest of the string
                                sender.sendMessage(sb.toString());
                            }
                        } else {
                            // Player not found
                            sender.sendMessage(ChatColor.YELLOW + "Player not found!");
                        }
                        // Makes sure this command stops here
                        return;
                    } else {

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

                        // Set the variable
                        ip = FlatFileHandler.formatIP(targetPlayer.getAddress().toString());
                    }
                }

                // Get list with users on the target's ip
                List<String> userlist = parent.getDataHandler().getIpUserList(ip);
                // Will always have at least one (target itself) so no need for null check, Print results
                sender.sendMessage("Listing users with the ip '" + ip + "':");

                // String builder 
                StringBuilder sb = new StringBuilder();
                // Loop trough all names in the list
                for (String name : userlist) {

                    // Checks if there should be a , added
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    // Checks the length of the string, to make sure no data is lost
                    if (sb.length() + 2 + name.length() > 280) {
                        // List to long, print results and reset list
                        sender.sendMessage(sb.toString());
                        sb = new StringBuilder().append(ChatColor.YELLOW).append(name);
                    } else {
                        // String length is ok, append name
                        sb.append(name);
                    }

                }

                // Make it so it says "No players found" when no players found
                if (sb.length() == 0) {
                    sb.append(ChatColor.YELLOW).append("No players found!");
                }

                // Print the rest of the string
                sender.sendMessage(sb.toString());
                return;
            } else {
                // Invalid amount of arguments, print out usage message
                sender.sendMessage(ChatColor.YELLOW + "Usage: /ipusers [player]");
            }
        } catch (DataException de) {
            sender.sendMessage(ChatColor.RED + "Exception in getting ip users " + de.getMessage());

        }
    }
}
