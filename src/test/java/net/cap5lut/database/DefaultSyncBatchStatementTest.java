package net.cap5lut.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultSyncBatchStatementTest {
    @Test
    public void execute() throws SQLException {
        final var resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getInt(1)).thenReturn(1).thenReturn(2).thenReturn(3);

        final var statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(resultSet);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        final var result = new DefaultSyncBatchStatement(
                connection,
                "INSERT INTO test_table (id) VALUES (?) RETURNING id;"
        )
                .add(new Batch().addParameter(1))
                .add(new Batch().addParameter(2))
                .add(new Batch().addParameter(3))
                .execute(row -> row.getInt(1))
                .mapToInt(i -> i)
                .toArray();
        assertArrayEquals(new int[] {1, 2, 3}, result);
    }

    @Test
    public void executeBatch() throws SQLException {
        final var statement = mock(PreparedStatement.class);
        when(statement.executeBatch()).thenReturn(new int[] {1, 1, 1});

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        final var result = new DefaultSyncBatchStatement(
                connection,
                "INSERT INTO test_table (id) VALUES (?);"
        )
                .add(new Batch().addParameter(1))
                .add(new Batch().addParameter(2))
                .add(new Batch().addParameter(3))
                .executeBatch();
        assertArrayEquals(new int[] {1, 1, 1}, result);
    }

    @Test
    public void executeLargeBatch() throws SQLException {
        final var statement = mock(PreparedStatement.class);
        when(statement.executeLargeBatch()).thenReturn(new long[] {1, 1, 1});

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        final var result = new DefaultSyncBatchStatement(
                connection,
                "INSERT INTO test_table (id) VALUES (?);"
        )
                .add(new Batch().addParameter(1))
                .add(new Batch().addParameter(2))
                .add(new Batch().addParameter(3))
                .executeLargeBatch();
        assertArrayEquals(new long[] {1, 1, 1}, result);
    }
}