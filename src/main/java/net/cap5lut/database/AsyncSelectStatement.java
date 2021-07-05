package net.cap5lut.database;

import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Asynchronous select statement.
 */
public interface AsyncSelectStatement extends ParameterSingleSetter<AsyncSelectStatement> {
    /**
     * Executes the select statement.
     *
     * @param reader row reader
     * @param <T> result type
     * @return stream of all rows
     */
    <T> CompletableFuture<Stream<T>> execute(SQLFunction<ResultSet, T> reader);
}
