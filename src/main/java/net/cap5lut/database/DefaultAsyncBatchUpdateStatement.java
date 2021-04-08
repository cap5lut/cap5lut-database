package net.cap5lut.database;

import net.cap5lut.util.function.ConsumerEx;
import net.cap5lut.util.function.FunctionEx;
import net.cap5lut.util.function.SupplierEx;

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
public class DefaultAsyncBatchUpdateStatement implements AsyncBatchStatement {
    /**
     * Internal statement executor.
     */
    private final ExecutorService executor;

    /**
     * Internal connection supplier.
     */
    private final SupplierEx<Connection, SQLException> connection;

    /**
     * SQL statement.
     */
    private final String sql;

    /**
     * Batch setters.
     */
    private final List<ConsumerEx<PreparedStatement, SQLException>> setters = new ArrayList<>();

    /**
     * Creates a new instance.
     *
     * @param executor statement executor
     * @param connection connection supplier
     * @param sql SQL statement
     */
    public DefaultAsyncBatchUpdateStatement(ExecutorService executor, SupplierEx<Connection, SQLException> connection,
                                            String sql) {
        this.executor = executor;
        this.connection = connection;
        this.sql = sql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncBatchStatement add(ConsumerEx<PreparedStatement, SQLException> setter) {
        setters.add(setter);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncBatchStatement add(Batch batch) {
        setters.add(batch::set);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<Stream<T>> execute(FunctionEx<ResultSet, T, SQLException> reader) {
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
