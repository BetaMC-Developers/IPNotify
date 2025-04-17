package me.hretsam.ipnotify;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.hretsam.ipnotify.data.DataException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class IPPlayerListener extends PlayerListener {

    public static IPNotify plugin;

    public IPPlayerListener(IPNotify instance) {
        plugin = instance;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            // Get player
            Player player = event.getPlayer();

            // Check if should do warning, and if player is new
            if (IPNotify.getConfig().joinWarning == IPConfig.WarnMode.ALWAYS
                    || (IPNotify.getConfig().joinWarning == IPConfig.WarnMode.FIRSTJOIN && !plugin.getDataHandler().isUserAlreadyLogged(player.getName()))) {
                // Get all connected players to the ip of the new user
                List<String> users = plugin.getDataHandler().getIpUserList(player.getAddress().toString());
                // If none (player itself not added yet) no warning
                if (users != null && users.size() > 0) {
                    int size = (IPNotify.getConfig().joinWarning == IPConfig.WarnMode.ALWAYS ? users.size() - 1 : users.size());
                    plugin.sendWarningMessage(ChatColor.RED + "[IPNotify] The IP of player " + player.getName() + " is used by " + size + " other user" + (size > 1 ? "s" : "") + "!");
                }
            }


            // Add player to log file
            plugin.getDataHandler().addIp(player.getName(), player.getAddress().toString(), new Date().getTime());
        } catch (DataException ex) {
            //@TODO error logging
            Logger.getLogger(IPPlayerListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
