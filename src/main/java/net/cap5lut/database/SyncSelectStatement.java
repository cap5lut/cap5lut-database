package net.cap5lut.database;

import net.cap5lut.util.function.FunctionEx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

/**
 * Synchronous select statement.
 */
public interface SyncSelectStatement extends ParameterSingleSetterStatement<SyncSelectStatement> {
    /**
     * Executes the select statement.
     *
     * @param reader row reader
     * @param <T> result type
     * @return stream of all rows
     */
    <T> Stream<T> execute(FunctionEx<ResultSet, T, SQLException> reader) throws SQLException;
}
