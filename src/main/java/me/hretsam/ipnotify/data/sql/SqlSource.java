package me.hretsam.ipnotify.data.sql;

import java.sql.Connection;
import java.sql.SQLException;
import me.hretsam.ipnotify.IPNotify;

/**
 *
 * @author Hretsam
 */
public abstract class SqlSource {

    public abstract Connection getConnection();

    public abstract void openConnection(IPNotify plugin);

    public abstract void closeConnection() throws SQLException;

    public abstract void buildDatabase() throws SQLException;
}
