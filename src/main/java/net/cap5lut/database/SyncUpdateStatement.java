package net.cap5lut.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Synchronous update statement.
 */
public interface SyncUpdateStatement extends ParameterSingleSetter<SyncUpdateStatement> {
    /**
     * Executes the update statement.
     *
     * @return affected rows
     * @see PreparedStatement#executeUpdate()
     */
    int execute() throws SQLException;

    /**
     * Executes the update statement and reads the result.
     *
     * @param reader row reader
     * @param <T> result type
     * @return result
     */
    <T> T execute(SQLFunction<ResultSet, T> reader) throws SQLException;
}
