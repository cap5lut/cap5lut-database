package net.cap5lut.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultSyncUpdateStatementTest {
    @Test
    public void execute() throws SQLException {
        final var statement = mock(PreparedStatement.class);
        when(statement.executeUpdate()).thenReturn(1);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        assertEquals(
                1,
                new DefaultSyncUpdateStatement(connection, "INSERT INTO test_table (id) VALUES (?);")
                        .addParameter(5)
                        .execute()
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
                new DefaultSyncUpdateStatement(connection, "INSERT INTO test_table (id) VALUES (?) RETURNING id;")
                        .addParameter(5)
                        .<Integer>execute(row -> row.getInt(1))
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

        assertThrows(
                SQLException.class,
                () -> new DefaultSyncUpdateStatement(connection, "SELECT id FROM test_table WHERE id = ?;")
                        .addParameter(6)
                        .execute(row -> row.getInt(1))
        );
    }
}