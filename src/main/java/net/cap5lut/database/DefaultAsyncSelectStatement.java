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
import java.util.stream.Stream;

/**
 * Default {@link AsyncSelectStatement} implementation.
 */
public class DefaultAsyncSelectStatement implements AsyncSelectStatement {
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
    public DefaultAsyncSelectStatement(ExecutorService executor, SupplierEx<Connection, SQLException> connection,
                                       String sql) {
        this.executor = executor;
        this.connection = connection;
        this.sql = sql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultAsyncSelectStatement addParameter(BiConsumerEx<PreparedStatement, Integer, SQLException> setter) {
        setters.add(setter);
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
