package net.cap5lut.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

/**
 * Synchronous select statement.
 */
public interface SyncSelectStatement extends ParameterSingleSetter<SyncSelectStatement> {
    /**
     * Executes the select statement.
     *
     * @param reader row reader
     * @param <T> result type
     * @return stream of all rows
     */
    <T> Stream<T> execute(SQLFunction<ResultSet, T> reader) throws SQLException;
}
