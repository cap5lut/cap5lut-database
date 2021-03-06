package net.cap5lut.database;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

/**
 * Default {@link Database} implementation.
 */
public class DefaultDatabase implements Database {
    /**
     * Internal statement executor.
     */
    protected final ExecutorService executor;

    /**
     * Data source.
     */
    protected final DataSource dataSource;

    /**
     * Close action.
     */
    protected final Runnable closeAction;

    /**
     * Creates a new instance.
     *
     * @param dataSource data source
     * @param executor statement executor
     */
    public DefaultDatabase(DataSource dataSource, ExecutorService executor, Runnable closeAction) {
        this.dataSource = dataSource;
        this.executor = executor;
        this.closeAction = closeAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Void> create(String sql) {
        return CompletableFuture.runAsync(
            () -> {
                try (final var connection = dataSource.getConnection()) {
                    connection.setAutoCommit(true);
                    try (final var statement = connection.createStatement()) {
                        statement.execute(sql);
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
    public AsyncSelectStatement query(String sql) {
        return new DefaultAsyncSelectStatement(executor, dataSource::getConnection, sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncUpdateStatement update(String sql) {
        return new DefaultAsyncUpdateStatement(executor, dataSource::getConnection, sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncBatchStatement batch(String sql) {
        return new DefaultAsyncBatchStatement(executor, dataSource::getConnection, sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletableFuture<T> transaction(SQLFunction<TransactionContext, T> transaction) {
        return CompletableFuture.supplyAsync(() -> {
            try (final var connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);
                final var savepoint = connection.setSavepoint();
                try {
                    return transaction.apply(new DefaultTransactionContext(connection));
                } catch (SQLException e) {
                    connection.rollback(savepoint);
                    throw new CompletionException(e);
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (closeAction != null) {
            closeAction.run();
        }
    }
}
