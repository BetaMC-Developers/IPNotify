package me.hretsam.ipnotify.commands;

import java.util.List;
import me.hretsam.ipnotify.IPNotify;
import me.hretsam.ipnotify.data.DataException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Hretsam
 */
public class CommandIpCheck implements IPCommand {

    @Override
    public void run(IPNotify parent, CommandSender sender, String command, String[] args) {
        try {

            // Check arguments length
            if (args.length == 0) {

                // Check permissions
                if (!parent.getPermissions().hasPermission(sender, IPNotify.getConfig().othernode, "IPNotify.other")) {
                    sender.sendMessage("You don't have Permission to do that");
                    return;
                }

                // Gets the list
                List<String> userlist = parent.getDataHandler().getIndirectlyBannedUserList();
                // Send message
                sender.sendMessage("Printing users that are indirectly banned!");

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
            } else {
                //Invalid amount of arguments, print out usage message
                sender.sendMessage(ChatColor.YELLOW + "Usage: /ipcheck");
            }
            return;
        } catch (DataException de) {
            sender.sendMessage(ChatColor.RED + "Exception in getting checking ip " + de.getMessage());
        }
    }
}
