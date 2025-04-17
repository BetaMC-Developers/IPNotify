package me.hretsam.ipnotify.data.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import me.hretsam.ipnotify.IPNotify;

/**
 *
 * @author Hretsam
 */
public class SqlLiteSource extends SqlSource {

    /** the connection with the database */
    public static Connection connection;

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void openConnection(IPNotify plugin) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/players.db");

            IPNotify.writelog("Connected to database", false);
        } catch (ClassNotFoundException e) {
            IPNotify.writelog("ClassNotFoundException " + e.getMessage(), true);
        } catch (SQLException e) {
            IPNotify.writelog("SQLException " + e.getMessage(), true);
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        connection.close();
    }

    @Override
    public void buildDatabase() throws SQLException {
        Connection conn = getConnection();
        if (getConnection() == null) {
            IPNotify.writelog("Could not get Connection!", true);
            return;
        }
        
        try {
            ResultSet result = conn.prepareStatement("select * from users limit 1;").executeQuery();
            if (result.next()) {
                return;
            }
        } catch (SQLException sqle) {
        }

        Statement statement = conn.createStatement();
        String sqltable1 = "CREATE TABLE 'users' ("
                + " 'id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + " 'username' VARCHAR(45)); ";
        String sqltable2 = "CREATE TABLE 'userip' ("
                + " 'ipid' INTEGER, "
                + " 'userid' INTEGER, "
                + " 'date' VARCHAR(45)); ";
        String sqltable3 = "CREATE TABLE 'ips' ("
                + " 'id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + " 'ip' VARCHAR(45)); ";
        statement.execute(sqltable1);
        statement.execute(sqltable2);
        statement.execute(sqltable3);
        
        IPNotify.writelog("New tables created", false);

    }
}
