package net.cap5lut.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

/**
 * Synchronous batch statement.
 */
public interface SyncBatchStatement extends ParameterBatchSetterStatement<SyncBatchStatement> {
    /**
     * Executes the batch statement and reads all rows.
     *
     * @param reader row reader
     * @param <T> result type
     * @return stream of all rows
     */
    <T> Stream<T> execute(SQLFunction<ResultSet, T> reader) throws SQLException;

    /**
     * Executes the batch statement.
     *
     * @return execution result
     * @see PreparedStatement#executeBatch()
     */
    int[] executeBatch() throws SQLException;

    /**
     * Executes the batch statement.
     *
     * @return execution result
     * @see PreparedStatement#executeLargeBatch()
     */
    long[] executeLargeBatch() throws SQLException;
}
