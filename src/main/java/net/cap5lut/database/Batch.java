package net.cap5lut.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Batch.
 */
public class Batch implements ParameterSingleSetter<Batch>, SQLConsumer<PreparedStatement> {
    /**
     * All added setters.
     */
    private final List<SQLBiConsumer<PreparedStatement, Integer>> setters = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Batch addParameter(SQLBiConsumer<PreparedStatement, Integer> setter) {
        setters.add(setter);
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

    /**
     * Sets the batch data on a given statement.
     * Note: this does not call {@link PreparedStatement#addBatch()}.
     *
     * @param statement statement to add batch data to
     * @throws SQLException if an error occurs
     */
    @Override
    public void accept(PreparedStatement statement) throws SQLException {
        set(statement);
    }
}
