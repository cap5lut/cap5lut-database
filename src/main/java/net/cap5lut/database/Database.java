package net.cap5lut.database;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * Database.
 */
public interface Database extends AutoCloseable {
    /**
     * HikariCP {@link DataSource} class name.
     */
    String HIKARICP_DATASOURCE_CLASS_NAME = "com.zaxxer.hikari.HikariDataSource";

    /**
     * Gets a {@link Database} instance of a given {@link DataSource}.
     *
     * @param dataSource data source
     * @param executor query executor
     * @return new {@link Database} instance
     */
    static Database of(DataSource dataSource, ExecutorService executor) {
        Runnable shutdownAction = null;
        switch (dataSource.getClass().getCanonicalName()) {
            case HIKARICP_DATASOURCE_CLASS_NAME:
                shutdownAction = ((HikariDataSource) dataSource)::close;
                break;
        }
        return new DefaultDatabase(dataSource, executor, shutdownAction);
    }

    /**
     * Gets a {@link Database} instance of a given {@link DataSource}.
     * Note: {@link ForkJoinPool#commonPool()} will be used as query executor.
     *
     * @param dataSource data source
     * @return new {@link Database} instance
     */
    static Database of(DataSource dataSource) {
        return of(dataSource, ForkJoinPool.commonPool());
    }

    /**
     * Executes an asynchronous create statement.
     *
     * @param sql SQL statement
     * @return execution result
     */
    CompletableFuture<Void> create(String sql);

    /**
     * Creates a new asynchronous query statement.
     *
     * @param sql SQL statement
     * @return asynchronous query statement
     */
    AsyncSelectStatement query(String sql);

    /**
     * Creates a new asynchronous update statement.
     *
     * @param sql SQL statement
     * @return asynchronous update statement
     */
    AsyncUpdateStatement update(String sql);

    /**
     * Creates a new asynchronous batch statement.
     *
     * @param sql SQL statement
     * @return asynchronous batch statement
     */
    AsyncBatchStatement batch(String sql);

    /**
     * Executes an transaction asynchronous.
     *
     * @param context transaction context
     * @param <T> transaction result type
     * @return context result
     */
    <T> CompletableFuture<T> transaction(SQLFunction<TransactionContext, T> context);

    /**
     * Closes the database.
     */
    @Override
    void close();
}
