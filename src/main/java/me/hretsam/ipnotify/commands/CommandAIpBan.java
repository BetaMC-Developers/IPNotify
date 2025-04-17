package me.hretsam.ipnotify.commands;

import java.util.List;
import me.hretsam.ipnotify.data.FlatFileHandler;
import me.hretsam.ipnotify.IPNotify;
import me.hretsam.ipnotify.data.DataException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;

/**
 *
 * @author Hretsam
 */
public class CommandAIpBan implements IPCommand {

    @Override
    public void run(IPNotify parent, CommandSender sender, String command, String[] args) {
        try {
            if (!parent.getPermissions().hasPermission(sender, parent.getConfig().aipbannode, "IPNotify.aipban")) {
                sender.sendMessage("You don't have Permission to do that");
                return;
            }

            // Check arguments length
            if (args.length == 1 || args.length == 2) {
                boolean banRelated = false;
                if (args.length == 2) {
                    banRelated = true;
                }


                String ip = "";

                // Check if its an IP or username
                if (args[0].contains(".")) {
                    ip = args[0];
                } else {
                    Player targetPlayer = parent.getServer().getPlayer(args[0]);
                    if (targetPlayer != null) {
                        ip = FlatFileHandler.formatIP(targetPlayer.getAddress().toString());
                    } else {
                        sender.sendMessage("You cannot ipban offline players!");
                        return;
                    }
                }

                // Add ip to banlist
                ((CraftServer) parent.getServer()).getHandle().c(ip);
                // gets a list of users with the given IP
                List<String> userlist = parent.getDataHandler().getIpUserList(ip);
                // There is atleast one, so no need for null check, print results
                sender.sendMessage("IP '" + ip + "' added to banList.");
                // Extra message
                sender.sendMessage((banRelated ? "Banned related usernames!" : "Printing related usernames!"));
                // String builder 
                StringBuilder sb = new StringBuilder();
                // Loop trough all names in the list
                for (String name : userlist) {
                    // Check if usernames should be banned as well
                    if (banRelated) {
                        // Add username to banlist
                        ((CraftServer) parent.getServer()).getHandle().a(name);
                    }

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
                // Invalid amount of arguments, print out usage message
                sender.sendMessage(ChatColor.YELLOW + "Usage: /aipban <playername|ip> [banRelated]");
            }

        } catch (DataException de) {
            sender.sendMessage(ChatColor.RED + "Exception in aipban " + de.getMessage());

        }
    }
}
