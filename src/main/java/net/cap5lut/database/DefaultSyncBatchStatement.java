package net.cap5lut.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Default {@link SyncBatchStatement} implementation.
 */
public class DefaultSyncBatchStatement implements SyncBatchStatement {
    /**
     * Internal connection.
     */
    protected final Connection connection;

    /**
     * SQL statement.
     */
    protected final String sql;

    /**
     * Parameter setters.
     */
    protected final List<SQLConsumer<PreparedStatement>> setters = new ArrayList<>();

    /**
     * Creates a new instance.
     *
     * @param connection connection
     * @param sql SQL statement
     */
    public DefaultSyncBatchStatement(Connection connection, String sql) {
        this.connection = connection;
        this.sql = sql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SyncBatchStatement add(SQLConsumer<PreparedStatement> setter) {
        setters.add(setter);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Stream<T> execute(SQLFunction<ResultSet, T> reader) throws SQLException {
        final var stream = Stream.<T>builder();
        for (final var setter: setters) {
            try (final var statement = connection.prepareStatement(sql)) {
                setter.accept(statement);
                try (final var resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        stream.add(reader.apply(resultSet));
                    }
                }
            }
        }
        return stream.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] executeBatch() throws SQLException {
        try (final var statement = connection.prepareStatement(sql)) {
            for (final var setter: setters) {
                setter.accept(statement);
                statement.addBatch();
            }
            return statement.executeBatch();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long[] executeLargeBatch() throws SQLException {
        try (final var statement = connection.prepareStatement(sql)) {
            for (final var setter: setters) {
                setter.accept(statement);
                statement.addBatch();
            }
            return statement.executeLargeBatch();
        }
    }
}
