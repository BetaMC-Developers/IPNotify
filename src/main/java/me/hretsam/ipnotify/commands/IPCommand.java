
package me.hretsam.ipnotify.commands;

import me.hretsam.ipnotify.IPNotify;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Hretsam
 */
public interface IPCommand {
    
    public void run(IPNotify parent, CommandSender sender, String command, String args[]);
    
}
