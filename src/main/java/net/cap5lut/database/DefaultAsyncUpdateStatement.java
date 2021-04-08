package net.cap5lut.database;

import net.cap5lut.util.function.BiConsumerEx;
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

/**
 * Default {@link AsyncUpdateStatement} implementation.
 */
public class DefaultAsyncUpdateStatement implements AsyncUpdateStatement {
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
     * Parameter setters.
     */
    private final List<BiConsumerEx<PreparedStatement, Integer, SQLException>> setters = new ArrayList<>();

    /**
     * Creates a new instance.
     *
     * @param executor statement executor
     * @param connection connection supplier
     * @param sql SQL statement
     */
    public DefaultAsyncUpdateStatement(ExecutorService executor, SupplierEx<Connection, SQLException> connection,
                                       String sql) {
        this.executor = executor;
        this.connection = connection;
        this.sql = sql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncUpdateStatement addParameter(BiConsumerEx<PreparedStatement, Integer, SQLException> setter) {
        setters.add(setter);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Integer> execute() {
        return CompletableFuture.supplyAsync(
            () -> {
                try (final var connection = this.connection.get()) {
                    connection.setAutoCommit(true);
                    try (final var statement = connection.prepareStatement(sql)) {
                        var index = 0;
                        for (final var setter : setters) {
                            setter.accept(statement, ++index);
                        }
                        return statement.executeUpdate();
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
    public <T> CompletableFuture<T> execute(FunctionEx<ResultSet, T, SQLException> reader) {
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
                            if (!resultSet.next()) {
                                throw new SQLException("no row returned");
                            }
                            return reader.apply(resultSet);
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
