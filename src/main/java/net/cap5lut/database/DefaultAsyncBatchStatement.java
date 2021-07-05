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
 * Default {@link AsyncBatchStatement} implementation.
 */
public class DefaultAsyncBatchStatement implements AsyncBatchStatement {
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
     * Batch setters.
     */
    protected final List<SQLConsumer<PreparedStatement>> setters = new ArrayList<>();

    /**
     * Creates a new instance.
     *
     * @param executor statement executor
     * @param connection connection supplier
     * @param sql SQL statement
     */
    public DefaultAsyncBatchStatement(ExecutorService executor, SQLSupplier<Connection> connection,
                                      String sql) {
        this.executor = executor;
        this.connection = connection;
        this.sql = sql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncBatchStatement add(SQLConsumer<PreparedStatement> setter) {
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
                } catch (SQLException e) {
                    throw new CompletionException(e);
                }
            },
            executor
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<int[]> executeBatch() {
        return CompletableFuture.supplyAsync(
            () -> {
                try (final var connection = this.connection.get()) {
                    connection.setAutoCommit(true);
                    try (final var statement = connection.prepareStatement(sql)) {
                        for (final var setter: setters) {
                            setter.accept(statement);
                            statement.addBatch();
                        }
                        return statement.executeBatch();
                    }
                } catch (SQLException e) {
                    throw new CompletionException(e);
                }
            },
            executor
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<long[]> executeLargeBatch() {
        return CompletableFuture.supplyAsync(
            () -> {
                try (final var connection = this.connection.get()) {
                    connection.setAutoCommit(true);
                    try (final var statement = connection.prepareStatement(sql)) {
                        for (final var setter: setters) {
                            setter.accept(statement);
                            statement.addBatch();
                        }
                        return statement.executeLargeBatch();
                    }
                } catch (SQLException e) {
                    throw new CompletionException(e);
                }
            },
            executor
        );
    }
}
