package net.cap5lut.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * Default {@link TransactionContext} implementation.
 */
public class DefaultTransactionContext implements TransactionContext {
    /**
     * Database connection for this transaction.
     */
    private final Connection connection;

    /**
     * Creates a new instance.
     *
     * @param connection database connection for this transaction
     */
    public DefaultTransactionContext(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(String sql) throws SQLException {
        try (final var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SyncSelectStatement query(String sql) {
        return new DefaultSyncSelectStatement(connection, sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SyncUpdateStatement update(String sql) {
        return new DefaultSyncUpdateStatement(connection, sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SyncBatchStatement batch(String sql) {
        return new DefaultSyncBatchStatement(connection, sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Savepoint savepoint() throws SQLException {
        return connection.setSavepoint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Savepoint savepoint(String name) throws SQLException {
        return connection.setSavepoint(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        connection.rollback(savepoint);
    }
}
