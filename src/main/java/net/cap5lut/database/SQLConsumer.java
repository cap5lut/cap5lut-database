package net.cap5lut.database;

import java.sql.SQLException;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

/**
 * {@link Consumer} which can throw exceptions.
 */
@FunctionalInterface
public interface SQLConsumer<T> {
    /**
     * Consumes a value.
     *
     * @param value value to consume
     * @throws SQLException possibly thrown error
     */
    void accept(T value) throws SQLException;
}