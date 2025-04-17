package me.hretsam.ipnotify.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.hretsam.ipnotify.IPNotify;
import me.hretsam.ipnotify.IPObject;
import me.hretsam.ipnotify.data.sql.SqlSource;

/**
 *
 * @author Hretsam
 */
public class SqlHandler extends DataHandler {

    private SqlSource sqlEngine;

    public SqlHandler(IPNotify plugin, SqlSource sqlEngine) {
        super(plugin);
        this.sqlEngine = sqlEngine;

        sqlEngine.openConnection(plugin);
        try {
            sqlEngine.buildDatabase();
        } catch (SQLException ex) {
            Logger.getLogger(SqlHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addIp(String username, String ip, long datelong) throws DataException {
        try {
            int userid = getUserId(username, true);
            int ipid = getIpId(ip, true);
            PreparedStatement statement = sqlEngine.getConnection().prepareStatement("select date from userip where userid = ? and ipid = ?;");
            statement.setInt(1, userid);
            statement.setInt(2, ipid);
            if (!statement.executeQuery().next()) {
                statement = sqlEngine.getConnection().prepareStatement("insert into userip (userid, ipid, date) values (?,?,?);");
                statement.setInt(1, userid);
                statement.setInt(2, ipid);
                statement.setString(3, String.valueOf(datelong));
                statement.executeUpdate();
            } else {
                statement = sqlEngine.getConnection().prepareStatement("update userip set date = ? where userid = ? and ipid = ?;");
                statement.setInt(1, userid);
                statement.setInt(2, ipid);
                statement.setString(3, String.valueOf(datelong));
                statement.executeUpdate();

            }
        } catch (SQLException ex) {
            throw new DataException("SQL exception! " + ex.getMessage());
        }
    }

    /**
     * Returns the users id or -1 if users is not avaiable
     * @param ip
     * @return
     * @throws SQLException 
     */
    public int getIpId(String ip, boolean genNew) throws SQLException {
        ip = formatIP(ip);
        PreparedStatement statement = sqlEngine.getConnection().prepareStatement("select id from ips where ip = ?;");
        statement.setString(1, ip);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            return result.getInt("id");
        } else if (genNew) {
            statement = sqlEngine.getConnection().prepareStatement("insert into ips (ip) values (?);", statement.RETURN_GENERATED_KEYS);
            statement.setString(1, ip);
            statement.execute();
            result = statement.getGeneratedKeys();
            if (result.next()) {
                return result.getInt(1);
            }
            throw new SQLException("No key could be generated");
        } else {
            return -1;
        }
    }

    /**
     * Returns the users id or -1 if users is not avaiable
     * @param username
     * @return
     * @throws SQLException 
     */
    public int getUserId(String username, boolean genNew) throws SQLException {
        PreparedStatement statement = sqlEngine.getConnection().prepareStatement("select id from users where username = ?;");
        statement.setString(1, username);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            return result.getInt("id");
        } else if (genNew) {
            statement = sqlEngine.getConnection().prepareStatement("insert into users (username) values (?);", statement.RETURN_GENERATED_KEYS);
            statement.setString(1, username);
            statement.execute();
            result = statement.getGeneratedKeys();
            if (result.next()) {
                return result.getInt(1);
            }
            throw new SQLException("No key could be generated");
        } else {
            return -1;
        }
    }

    /**
     * Returns the username that is equal to the given username
     * but where the casing is not the same
     * @param username
     * @return 
     */
    @Override
    public String checkCaseIndependant(String username) throws DataException {
        try {
            PreparedStatement statement = sqlEngine.getConnection().prepareStatement("select id from users where LOWER(username) = ?;");
            statement.setString(1, username.toLowerCase());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("username");
            } else {
                return username;
            }
        } catch (SQLException ex) {
            throw new DataException("SQL exception! " + ex.getMessage());
        }
    }

    @Override
    public List<String> getIndirectlyBannedUserList() throws DataException {
        try {

            // Get IP list
            List<String> bannedIps = getMCBannedIpsList();
            List<String> bannedNames = getMCBannedNamesList();

            int userid;
            // Get banned names
            for (String name : bannedNames) {
                // Get right cased name
                name = checkCaseIndependant(name);
                userid = getUserId(name, false);

                // Check DB
                PreparedStatement statement = sqlEngine.getConnection().prepareStatement("Select ip from ips as a, userip as b "
                        + "where a.id = b.ipid AND b.userid = ?;");
                statement.setInt(1, userid);
                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    bannedIps.add(result.getString("ip"));
                }
            }

            // Create a new list for the names of players who aren't banned
            // But an/the ip they used is.
            List<String> namesWithBannedIpUsage = new ArrayList<String>();

            // Loops trough all ips that are in the bannedIps list
            for (String ip : bannedIps) {
                // Gets all names that are used by the given ipo
                for (String name : getIpUserList(ip)) {
                    // Convert to lowercase (thats how mc stores it)
                    name = name.toLowerCase();
                    // Check if the name is not already banned
                    if (!bannedNames.contains(name) && !namesWithBannedIpUsage.contains(name)) {
                        // Add to list
                        namesWithBannedIpUsage.add(name);
                    }
                }
            }

            //Return the list
            return namesWithBannedIpUsage;
        } catch (SQLException ex) {
            throw new DataException("SQL exception! " + ex.getMessage());
        }
    }

    /**
     * Returns a list with all of the usernames used by the given ip
     * @param username
     * @return 
     */
    @Override
    public List<String> getIpUserList(String ip) throws DataException {
        try {
            int ipid = getIpId(ip, false);

            // Create the return list
            ArrayList<String> usernamelist = new ArrayList<String>();
            // Check DB
            PreparedStatement statement = sqlEngine.getConnection().prepareStatement("Select username from users as a, userip as b "
                    + "where a.id = b.userid AND b.ipid = ?;");
            statement.setInt(1, ipid);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                usernamelist.add(result.getString("username"));
            }
            // Return the list
            return usernamelist;
        } catch (SQLException ex) {
            throw new DataException("SQL exception! " + ex.getMessage());
        }
    }

    @Override
    public List<IPObject> getUserIplist(String username, int maxSize) throws DataException {
        try {
            int userid = getUserId(username, false);

            // Get IP list
            List<IPObject> iplist = new ArrayList<IPObject>();

            // Check DB
            PreparedStatement statement = sqlEngine.getConnection().prepareStatement("Select ip, date from ips as a, userip as b "
                    + "where a.id = b.ipid AND b.userid = ?;");
            statement.setInt(1, userid);
            ResultSet result = statement.executeQuery();
            int i = 0;
            while (result.next() && i < maxSize) {
                i++;
                iplist.add(new IPObject(result.getString("ip"), result.getLong("date")));
            }

            return iplist;
        } catch (SQLException ex) {
            throw new DataException("SQL exception! " + ex.getMessage());
        }
    }

    /**
     * Checks if the users is already logged
     * @param username
     * @return 
     */
    @Override
    public boolean isUserAlreadyLogged(String username) throws DataException {
        try {
            PreparedStatement statement = sqlEngine.getConnection().prepareStatement("select id from users where username = ?;");
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            return result.next();
        } catch (SQLException ex) {
            throw new DataException("SQL exception! " + ex.getMessage());
        }
    }

    @Override
    public void shutdown() throws DataException {
        try {
            sqlEngine.closeConnection();
        } catch (SQLException ex) {
            throw new DataException("SQL exception! " + ex.getMessage());
        }
    }

    @Override
    public String getLastUsedUsername(String ip) throws DataException {
        try {
            PreparedStatement statement = sqlEngine.getConnection().prepareStatement(
                    "select username, id from users as u where u.id in "
                    + "(select a.id from users as a, userip as b, ips as c where a.id = b.userid "
                    + "AND b.ipid = c.id AND c.ip = ? order by b.date desc limit 1);");
            statement.setString(1, ip);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("username");
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new DataException("SQL exception! " + ex.getMessage());
        }
    }
}
