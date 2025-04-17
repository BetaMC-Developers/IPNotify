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
public class MysqlSource extends SqlSource {

    /** the connection with the database */
    public static Connection connection;

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void openConnection(IPNotify plugin) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            /** Open connection */
            connection = DriverManager.getConnection("jdbc:mysql://" + IPNotify.getConfig().mysqllocation
                    + "/" + IPNotify.getConfig().mysqldbname, IPNotify.getConfig().mysqlusername, IPNotify.getConfig().mysqlpassword);

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
            ResultSet result = conn.prepareStatement("select id from users limit 1;").executeQuery();
            if (result.next()) {
                return;
            }
        } catch (SQLException sqle) {
        }


        Statement statement = conn.createStatement();
        String sqltable1 = " CREATE  TABLE users ("
                + " id INT NOT NULL AUTO_INCREMENT, "
                + " username VARCHAR(45) NOT NULL, "
                + " PRIMARY KEY (id) );";
        String sqltable2 = " CREATE  TABLE userip ("
                + " ipid INT NOT NULL, "
                + " userid INT NOT NULL, "
                + " date varchar(45)  NOT NULL,"
                + " PRIMARY KEY (userid, ipid)); ";
        String sqltable3 = " CREATE  TABLE ips ("
                + " id INT NOT NULL AUTO_INCREMENT, "
                + " ip VARCHAR(45) NOT NULL, "
                + " PRIMARY KEY (id) ); ";

        statement.execute(sqltable1);
        statement.execute(sqltable2);
        statement.execute(sqltable3);

    }
}
