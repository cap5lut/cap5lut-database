package net.cap5lut.database;

import java.sql.SQLException;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@link Function} which can throw exceptions.
 */
@FunctionalInterface
public interface SQLSupplier<T> {
    /**
     * Computes a value.
     *
     * @return computed values
     * @throws SQLException possibly thrown error
     */
    T get() throws SQLException;
}