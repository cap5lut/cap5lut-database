package net.cap5lut.database;

import net.cap5lut.util.function.FunctionEx;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Synchronous update statement.
 */
public interface SyncUpdateStatement extends ParameterSingleSetterStatement<SyncUpdateStatement> {
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
    <T> T execute(FunctionEx<ResultSet, T, SQLException> reader) throws SQLException;
}
