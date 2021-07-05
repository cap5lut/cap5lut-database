package net.cap5lut.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

/**
 * Default {@link AsyncSelectStatement} implementation.
 */
public class DefaultAsyncSelectStatement implements AsyncSelectStatement {
    /**
     * Internal statement executor.
     */
    protected final ExecutorService executor;

    /**
     * Internal connection supplier.
     */
    protected final SQLSupplier<Connection> connection;

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
     * @param executor statement executor
     * @param connection connection supplier
     * @param sql SQL statement
     */
    public DefaultAsyncSelectStatement(ExecutorService executor, SQLSupplier<Connection> connection,
                                       String sql) {
        this.executor = executor;
        this.connection = connection;
        this.sql = sql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultAsyncSelectStatement addParameter(SQLBiConsumer<PreparedStatement, Integer> setter) {
        setters.add(setter);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<Stream<T>> execute(SQLFunction<ResultSet, T> reader) {
        return CompletableFuture.supplyAsync(
            () -> {
                try (final var connection = this.connection.get()) {
                    connection.setAutoCommit(true);
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
                } catch (SQLException e) {
                    throw new CompletionException(e);
                }
            },
            executor
        );
    }
}
