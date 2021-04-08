package net.cap5lut.database;

import net.cap5lut.util.function.BiConsumerEx;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Batch.
 */
public class Batch {
    /**
     * All added setters.
     */
    private final List<BiConsumerEx<PreparedStatement, Integer, SQLException>> setters = new ArrayList<>();

    /**
     * Adds a value.
     *
     * @param value value to add
     * @return itself for method chaining
     */
    public Batch add(long value) {
        setters.add((statement, index) -> statement.setLong(index, value));
        return this;
    }

    /**
     * Adds a value.
     *
     * @param value value to add
     * @return itself for method chaining
     */
    public Batch add(String value) {
        setters.add((statement, index) -> statement.setString(index, value));
        return this;
    }

    /**
     * Sets the batch data on a given statement.
     * Note: this does not call {@link PreparedStatement#addBatch()}.
     *
     * @param statement statement to add batch data to
     * @throws SQLException if an error occurs
     */
    public void set(PreparedStatement statement) throws SQLException {
        int i = 0;
        for (final var setter: setters) {
            setter.accept(statement, ++i);
        }
    }
}
