package net.cap5lut.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Default {@link SyncSelectStatement} implementation.
 */
public class DefaultSyncSelectStatement implements SyncSelectStatement {
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
    protected final List<SQLBiConsumer<PreparedStatement, Integer>> setters = new ArrayList<>();

    /**
     * Creates a new instance.
     *
     * @param connection database connection
     * @param sql SQL statement
     */
    public DefaultSyncSelectStatement(Connection connection, String sql) {
        this.connection = connection;
        this.sql = sql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SyncSelectStatement addParameter(SQLBiConsumer<PreparedStatement, Integer> setter) {
        setters.add(setter);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Stream<T> execute(SQLFunction<ResultSet, T> reader) throws SQLException {
        try (final var statement = connection.prepareStatement(sql)) {
            var index = 0;
            for (final var setter: setters) {
                setter.accept(statement, ++index);
            }

            try (final var resultSet = statement.executeQuery()) {
                final var stream = Stream.<T>builder();
                while (resultSet.next()) {
                    stream.add(reader.apply(resultSet));
                }
                return stream.build();
            }
        }
    }
}
