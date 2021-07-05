package net.cap5lut.database;

import java.sql.SQLException;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

/**
 * {@link Function} which can throw exceptions.
 */
@FunctionalInterface
public interface SQLFunction<T, R> {
    /**
     * Computes a value from a given value.
     *
     * @param value value
     * @return computed result
     * @throws SQLException possibly thrown error
     */
    R apply(T value) throws SQLException;
}