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

class DefaultAsyncSelectStatementTest {
    @Test
    void addParameter() throws SQLException {
        final var resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt(1)).thenReturn(5);

        final var statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(resultSet);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        assertEquals(
                5,
                new DefaultAsyncSelectStatement(ForkJoinPool.commonPool(), () -> connection, "SELECT ?;")
                        .addParameter(5)
                        .execute(row -> row.getInt(1))
                        .join()
                        .findFirst()
                        .orElseThrow()
        );
    }

    @Test
    void execute() throws SQLException {
        final var resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt(1)).thenReturn(1);

        final var statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(resultSet);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        assertEquals(
                1,
                new DefaultAsyncSelectStatement(ForkJoinPool.commonPool(), () -> connection, "SELECT 1;")
                        .execute(row -> row.getInt(1))
                        .join()
                        .findFirst()
                        .orElseThrow()
        );
    }

    @Test
    void execute_brokenConnection() {
        assertThrowsWithCause(
                CompletionException.class,
                SQLException.class,
                () -> new DefaultAsyncSelectStatement(
                        ForkJoinPool.commonPool(),
                        () -> {
                            throw new SQLException();
                        },
                        "SELECT 1;"
                )
                        .execute(row -> row.getInt(1))
                        .join()
        );
    }
}