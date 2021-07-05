package net.cap5lut.database;

import java.sql.SQLException;
import java.util.concurrent.CompletionException;
import java.util.function.BiConsumer;

/**
 * {@link BiConsumer} which can throw exceptions.
 */
@FunctionalInterface
public interface SQLBiConsumer<T, U> {
    /**
     * Consumes two values.
     *
     * @param value0 value 0 to consume
     * @param value1 value 1 to consume
     * @throws SQLException possibly thrown error
     */
    void accept(T value0, U value1) throws SQLException;
}