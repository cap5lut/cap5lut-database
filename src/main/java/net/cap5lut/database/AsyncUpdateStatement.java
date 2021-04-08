package net.cap5lut.database;

import net.cap5lut.util.function.FunctionEx;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous update statement.
 */
public interface AsyncUpdateStatement extends ParameterSingleSetterStatement<AsyncUpdateStatement> {
    /**
     * Executes the update statement.
     *
     * @return affected rows
     * @see PreparedStatement#executeUpdate()
     */
    CompletableFuture<Integer> execute();

    /**
     * Executes the update statement and reads the result.
     *
     * @param reader row reader
     * @param <T> result type
     * @return result
     */
    <T> CompletableFuture<T> execute(FunctionEx<ResultSet, T, SQLException> reader);
}
