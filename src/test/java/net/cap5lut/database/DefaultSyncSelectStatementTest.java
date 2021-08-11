package net.cap5lut.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultSyncSelectStatementTest {
    @Test
    public void execute() throws SQLException {
        final var resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt(1)).thenReturn(5);

        final var statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(resultSet);

        final var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        assertEquals(
                5,
                new DefaultSyncSelectStatement(connection, "SELECT ?;")
                        .addParameter(5)
                        .execute(row -> row.getInt(1))
                        .findFirst()
                        .orElseThrow()
        );
    }
}