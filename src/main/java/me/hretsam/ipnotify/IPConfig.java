package me.hretsam.ipnotify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author Hretsam
 */
public class IPConfig {

    public final String configfilename = "config.yml";
    public String selfnode;
    public String othernode;
    public String warningnode;
    public String aipbannode;
    public String dateSyntax;
    public WarnMode joinWarning;
    public int maxIpListSize;
    public String mysqllocation;
    public String mysqldbname;
    public String mysqlusername;
    public String mysqlpassword;
    public String source;

    public IPConfig(IPNotify plugin, File datafolder) throws IOException {
        if (!datafolder.exists()) {
            datafolder.mkdirs();
        }
        File configfile = new File(datafolder.getAbsolutePath() + File.separator + configfilename);

        if (!configfile.exists()) {

            // Copys the config file from within this jar (default package) and writes it to the datafolder.
            configfile.createNewFile();
            try {
                InputStream stream = IPNotify.class.getResourceAsStream("/config.yml");
                OutputStream out = new FileOutputStream(configfile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = stream.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                stream.close();
                out.close();
                IPNotify.writelog("Config file not found, created new file", false);
            } catch (IOException iex) {
                IPNotify.writelog("Cannot create config file! " + iex.getMessage(), true);
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }

        // Loads the config file
        Configuration config = new Configuration(configfile);
        config.load();

        setupConfig(config);
    }

    /**
     * This loads all values of the config file
     * @param config 
     */
    public void setupConfig(Configuration config) {

        // Checks for the config version
        if (config.getInt("configversion", 0) < 2) {
            IPNotify.writelog("Your using an old config file, please update!", true);
        }

        // Loads all values, default value only used when key not found!
        dateSyntax = config.getString("date syntax", "dd-MMM-yyyy hh:mm");

        selfnode = config.getString("self node", "IPNotify.self");
        othernode = config.getString("other node", "IPNotify.other");
        warningnode = config.getString("warning node", "IPNotify.warning");
        aipbannode = config.getString("aipbannode", "IPNotify.aipban");

        String warning = config.getString("warn double ip", "firstjoin");
        if (warning.equalsIgnoreCase("always")){
            joinWarning = WarnMode.ALWAYS;
        } else if (warning.equalsIgnoreCase("off")){
            joinWarning = WarnMode.OFF;
        } else {
            joinWarning = WarnMode.FIRSTJOIN;
        }

        maxIpListSize = config.getInt("max iplist size", 6);

        mysqldbname = config.getString("mysql.dbname", "ipnotify");
        mysqllocation = config.getString("mysql.location", "ipnotify");
        mysqlusername = config.getString("mysql.username", "ipnotify");
        mysqlpassword = config.getString("mysql.password", "ipnotify");

        source = config.getString("datasource", "flatfile");

    }

    public enum WarnMode {

        FIRSTJOIN,
        ALWAYS,
        OFF;
    }
}
