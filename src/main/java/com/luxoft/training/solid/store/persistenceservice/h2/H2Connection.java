package com.luxoft.training.solid.store.persistenceservice.h2;

import com.luxoft.training.solid.store.persistence.PersistenceException;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class H2Connection implements Closeable {

    private static final String DEFAULT_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private final Connection connection;
    private final String connectionUrl;

    public H2Connection() throws ClassNotFoundException, SQLException {
        this(DEFAULT_CONNECTION);
    }

    public H2Connection(String connectionUrl) throws ClassNotFoundException, SQLException {
        this.connectionUrl = connectionUrl;
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection(connectionUrl, "", "");
    }

    @Override
    public void close() throws IOException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public void executeSql(String sql) {
        try {
            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    public ResultSet executeQuery(String sqlQuery) {
        try {
            return connection.createStatement().executeQuery(sqlQuery);
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    public int insertAndGetId(String sqlQuery) {
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            statement.execute();
            return getKey(statement);
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    private int getKey(PreparedStatement statement) throws SQLException {
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new PersistenceException("Insert failed!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        H2Connection that = (H2Connection) o;
        return connectionUrl.equals(that.connectionUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectionUrl);
    }

    @Override
    public String toString() {
        return "H2Connection{" +
                ", connectionUrl='" + connectionUrl + '\'' +
                '}';
    }
}
