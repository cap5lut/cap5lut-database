package net.cap5lut.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Asynchronous batch statement.
 */
public interface AsyncBatchStatement extends ParameterBatchSetterStatement<AsyncBatchStatement> {
    /**
     * Executes the batch statement and reads all rows.
     *
     * @param reader row reader
     * @param <T> result type
     * @return stream of all rows
     */
    <T> CompletableFuture<Stream<T>> execute(SQLFunction<ResultSet, T> reader);

    /**
     * Executes the batch statement.
     *
     * @return execution result
     * @see PreparedStatement#executeBatch()
     */
    CompletableFuture<int[]> executeBatch();

    /**
     * Executes the batch statement.
     *
     * @return execution result
     * @see PreparedStatement#executeLargeBatch()
     */
    CompletableFuture<long[]> executeLargeBatch();
}
