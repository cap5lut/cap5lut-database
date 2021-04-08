package net.cap5lut.database;

import net.cap5lut.util.function.BiConsumerEx;
import net.cap5lut.util.function.FunctionEx;

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
    private final Connection connection;

    /**
     * SQL statement.
     */
    private final String sql;

    /**
     * Parameter setters.
     */
    private final List<BiConsumerEx<PreparedStatement, Integer, SQLException>> setters = new ArrayList<>();

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
    public SyncSelectStatement addParameter(BiConsumerEx<PreparedStatement, Integer, SQLException> setter) {
        setters.add(setter);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Stream<T> execute(FunctionEx<ResultSet, T, SQLException> reader) throws SQLException {
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
