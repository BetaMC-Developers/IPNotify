
package me.hretsam.ipnotify.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import me.hretsam.ipnotify.IPNotify;
import me.hretsam.ipnotify.IPObject;
import org.bukkit.craftbukkit.CraftServer;

/**
 *
 * @author Hretsam
 */
public abstract class DataHandler {
    
    private IPNotify plugin;

    public DataHandler(IPNotify plugin) {
        this.plugin = plugin;
    }    

    /**
     * Formats the ip address to remove the slash and port
     * @param ip
     * @return ip itself
     */
    public static String formatIP(String ip) {
        // Remove the slash at the start
        if (ip.contains("/")) {
            // Remove the slash
            ip = ip.substring(1);
        }
        // Check if the ip still contains the port
        if (ip.contains(":")) {
            // Remove the port
            ip = ip.split(":")[0];
        }
        return ip;
    }

    /**
     * Adds the ip to the user's IP list
     * @param username
     * @param ip
     * @param datelong
     */
    public abstract void addIp(String username, String ip, long datelong) throws DataException;

    /**
     * Returns the username that is equal to the given username
     * but where the casing is not the same
     * @param username
     * @return
     */
    public abstract String checkCaseIndependant(String username) throws DataException;

    /**
     * Gets a list of players who's used an IP that is banned directly or indirectly (usernames)
     * @return
     */
    public abstract List<String> getIndirectlyBannedUserList() throws DataException;

    /**
     * Returns a list with all of the usernames used by the given ip
     * @param username
     * @return
     */
    public abstract List<String> getIpUserList(String ip) throws DataException;

    /**
     * This returns a list with all ips that are in the banned-ips.txt
     * It uses the reader as its in the ServerConfigurationManager
     * @return
     */
    protected List<String> getMCBannedIpsList() {
        // Make a list
        List<String> bannedIps = new ArrayList<String>();
        try {
            //Open reader and get the file (from a mc method)
            BufferedReader bufferedreader = new BufferedReader(new FileReader(((CraftServer) plugin.getServer()).getHandle().server.a("banned-ips.txt")));
            // Init input string
            String s = "";
            // Reader
            while ((s = bufferedreader.readLine()) != null) {
                // Add line to list
                bannedIps.add(s.trim().toLowerCase());
            }
            // Close reader
            bufferedreader.close();
        } catch (Exception exception) {
            IPNotify.writelog("Failed to load ip ban list: " + exception, true);
        }
        return bannedIps;
    }

    /**
     * This returns a list with all names that are in the banned-player.txt
     * It uses the reader as its in the ServerConfigurationManager
     * @return
     */
    protected List<String> getMCBannedNamesList() {
        // Make a list
        List<String> bannedNames = new ArrayList<String>();
        try {
            //Open reader and get the file (from a mc method)
            BufferedReader bufferedreader = new BufferedReader(new FileReader(((CraftServer) plugin.getServer()).getHandle().server.a("banned-players.txt")));
            // Init input string
            String s = "";
            // Reader
            while ((s = bufferedreader.readLine()) != null) {
                // Add line to list
                bannedNames.add(s.trim().toLowerCase());
            }
            // Close reader
            bufferedreader.close();
        } catch (Exception exception) {
            IPNotify.writelog("Failed to load ban list: " + exception, true);
        }
        // Return
        return bannedNames;
    }

    /**
     * Returns a list with all of the ip logged to this user
     * @param username
     * @param forceCaseCheck (use this when your not sure the casing is right)
     * @return
     */
    public abstract List<IPObject> getUserIplist(String username, int maxSize) throws DataException;

    /**
     * Checks if the users is already logged
     * @param username
     * @return
     */
    public abstract boolean isUserAlreadyLogged(String username) throws DataException;
    
    /**
     * Method that is called when the plugin is disabled.
     * @throws DataException 
     */
    public abstract void shutdown() throws DataException;
    
    /**
     * Returns the username that was last to use the given IP 
     * @param ip address
     * @return the username or null
     * @throws DataException 
     */
    public abstract String getLastUsedUsername(String IP) throws DataException;
}
