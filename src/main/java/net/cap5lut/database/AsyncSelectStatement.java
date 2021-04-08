package net.cap5lut.database;

import net.cap5lut.util.function.FunctionEx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Asynchronous select statement.
 */
public interface AsyncSelectStatement extends ParameterSingleSetterStatement<AsyncSelectStatement> {
    /**
     * Executes the select statement.
     *
     * @param reader row reader
     * @param <T> result type
     * @return stream of all rows
     */
    <T> CompletableFuture<Stream<T>> execute(FunctionEx<ResultSet, T, SQLException> reader);
}
