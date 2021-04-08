package net.cap5lut.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * Transaction context
 */
public interface TransactionContext {
    /**
     * Executes an asynchronous create statement.
     *
     * @param sql SQL statement
     */
    void create(String sql) throws SQLException;

    /**
     * Creates a new synchronous query statement.
     *
     * @param sql SQL statement
     * @return synchronous query statement
     */
    SyncSelectStatement query(String sql);

    /**
     * Creates a new synchronous update statement.
     *
     * @param sql SQL statement
     * @return synchronous update statement
     */
    SyncUpdateStatement update(String sql);

    /**
     * Creates a new synchronous batch statement.
     *
     * @param sql SQL statement
     * @return synchronous batch statement
     */
    SyncBatchStatement batch(String sql);

    /**
     * Commits the current transaction.
     *
     * @throws SQLException if an error occurs
     * @see Connection#commit()
     */
    void commit() throws SQLException;

    /**
     * Rolls back to the last save point.
     *
     * @throws SQLException if an error occurs
     * @see Connection#rollback()
     */
    void rollback() throws SQLException;

    /**
     * Rolls back to the given save point.
     *
     * @param savepoint save point to roll back to
     * @throws SQLException if an error occurs
     * @see Connection#rollback(Savepoint)
     */
    void rollback(Savepoint savepoint) throws SQLException;

    /**
     * Creates a new save point.
     *
     * @return new save point
     * @throws SQLException if an error occurs
     * @see Connection#setSavepoint()
     */
    Savepoint savepoint() throws SQLException;

    /**
     * Creates a new named save point.
     *
     * @param name save point name
     * @return new named save point
     * @throws SQLException if an error occurs
     */
    Savepoint savepoint(String name) throws SQLException;
}
