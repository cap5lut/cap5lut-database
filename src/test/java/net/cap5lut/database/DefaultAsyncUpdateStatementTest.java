package net.cap5lut.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ForkJoinPool;

import static net.cap5lut.database.Assertions.assertThrowsWithCause;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultAsyncUpdateStatementTest {
    @Test
    public void execute() throws SQLException {
        final var statement = mock(PreparedStatement.class);
        when(statement.executeUpdate()).thenReturn(1);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        assertEquals(
                1,
                new DefaultAsyncUpdateStatement(
                        ForkJoinPool.commonPool(),
                        () -> connection,
                        "INSERT INTO test_table (id) VALUES (?);"
                )
                        .addParameter(5)
                        .execute()
                        .join()
        );
    }

    @Test
    void execute_brokenConnection() {
        assertThrowsWithCause(
                CompletionException.class,
                SQLException.class,
                () -> new DefaultAsyncUpdateStatement(
                        ForkJoinPool.commonPool(),
                        () -> {
                            throw new SQLException();
                        },
                        "INSERT INTO test_table (id) VALUES ?;"
                )
                        .execute()
                        .join()
        );
    }

    @Test
    public void execute_read() throws SQLException {
        final var resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt(1)).thenReturn(5);

        final var statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(resultSet);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        assertEquals(
                5,
                new DefaultAsyncUpdateStatement(
                        ForkJoinPool.commonPool(),
                        () -> connection,
                        "INSERT INTO test_table (id) VALUES (?);"
                )
                        .addParameter(5)
                        .execute(row -> row.getInt(1))
                        .join()
        );
    }

    @Test
    public void execute_read_noResult() throws SQLException {
        final var resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);

        final var statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(resultSet);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        assertThrowsWithCause(
                CompletionException.class,
                SQLException.class,
                () -> new DefaultAsyncUpdateStatement(
                        ForkJoinPool.commonPool(),
                        () -> connection,
                        "SELECT id FROM test_table WHERE id = ?;"
                )
                        .addParameter(6)
                        .execute(row -> row.getInt(1))
                        .join()
        );
    }

    @Test
    void execute_read_brokenConnection() {
        assertThrowsWithCause(
                CompletionException.class,
                SQLException.class,
                () -> new DefaultAsyncUpdateStatement(
                        ForkJoinPool.commonPool(),
                        () -> {
                            throw new SQLException();
                        },
                        "INSERT INTO test_table (id) VALUES (?) RETURNING id;"
                )
                        .execute(row -> row.getInt(1))
                        .join()
        );
    }
}