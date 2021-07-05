package net.cap5lut.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link SyncUpdateStatement} implementation.
 */
public class DefaultSyncUpdateStatement implements SyncUpdateStatement {
    /**
     * Internal connection.
     */
    private final Connection connection;

    /**
     * SQL statement.
     */
    private final String sql;

    /**
     * Parameter setters.
     */
    private final List<SQLBiConsumer<PreparedStatement, Integer>> setters = new ArrayList<>();

    /**
     * Creates a new instance.
     *
     * @param connection database connection
     * @param sql SQL statement
     */
    public DefaultSyncUpdateStatement(Connection connection, String sql) {
        this.connection = connection;
        this.sql = sql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SyncUpdateStatement addParameter(SQLBiConsumer<PreparedStatement, Integer> setter) {
        setters.add(setter);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int execute() throws SQLException {
        try (final var statement = connection.prepareStatement(sql)) {
            var index = 0;
            for (final var setter: setters) {
                setter.accept(statement, ++index);
            }
            return statement.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T execute(SQLFunction<ResultSet, T> reader) throws SQLException {
        try (final var statement = connection.prepareStatement(sql)) {
            var index = 0;
            for (final var setter: setters) {
                setter.accept(statement, ++index);
            }

            try (final var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("no row returned");
                }
                return reader.apply(resultSet);
            }
        }
    }
}
